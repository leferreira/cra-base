package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.conversor.arquivo.FabricaDeArquivoXML;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.RetornoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.validacao.FabricaValidacaoArquivo;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RetornoMediator {

	private static final int NUMERO_SEQUENCIAL_RETORNO = 1;
	protected static final Logger logger = Logger.getLogger(ConfirmacaoMediator.class);

	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private RetornoDAO retornoDAO;
	@Autowired
	private BatimentoDAO batimentoDAO;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private FabricaDeArquivoXML fabricaDeArquivosXML;
	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private FabricaValidacaoArquivo fabricaValidacaoArquivo;

	private Instituicao cra;
	private TipoArquivo tipoArquivo;
	private List<Exception> erros;

	public Retorno carregarTituloRetornoPorId(int id) {
		Retorno retorno = new Retorno();
		retorno.setId(id);
		return retornoDAO.carregarTituloRetornoPorId(retorno);
	}

	public List<Remessa> buscarRetornosParaBatimento() {
		return retornoDAO.buscarRetornosParaBatimento();
	}

	public List<Remessa> buscarRetornosAguardandoLiberacao(Instituicao instiuicao, LocalDate dataBatimento, boolean dataComoDataLimite) {
		return retornoDAO.buscarRetornosAguardandoLiberacao(instiuicao, dataBatimento, dataComoDataLimite);
	}

	public List<Remessa> buscarRetornosParaPagamentoInstituicao(LocalDate dataBatimento) {
		return retornoDAO.buscarRetornosParaPagamentoInstituicao(dataBatimento);
	}

	public List<Remessa> buscarRetornosConfirmados() {
		return retornoDAO.buscarRetornosConfirmados();
	}

	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno) {
		return retornoDAO.buscarValorDeTitulosPagos(retorno);
	}

	public BigDecimal buscarSomaValorTitulosPagosRemessas(Instituicao instituicao, LocalDate dataBatimento, boolean dataComoDataLimite) {
		return retornoDAO.buscarSomaValorTitulosPagosRemessas(instituicao, dataBatimento, dataComoDataLimite);
	}

	public BigDecimal buscarValorDeCustasCartorio(Remessa retorno) {
		return retornoDAO.buscarValorDeCustasCartorio(retorno);
	}

	public Boolean verificarArquivoRetornoGeradoCra() {
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		return retornoDAO.verificarArquivoRetornoGeradoCra(getCra());
	}

	public void salvarBatimentos(List<Remessa> retornos) {
		Boolean arquivoRetornoGeradoHoje = verificarArquivoRetornoGeradoCra();
		for (Remessa retorno : retornos) {
			Batimento batimento = new Batimento();
			batimento.setData(aplicarRegraDataBatimento(arquivoRetornoGeradoHoje));
			batimento.setRemessa(retorno);
			batimento.setDepositosBatimento(new ArrayList<BatimentoDeposito>());

			for (Deposito depositosIdentificado : retorno.getListaDepositos()) {
				BatimentoDeposito depositosBatimento = new BatimentoDeposito();
				depositosBatimento.setBatimento(batimento);
				depositosBatimento.setDeposito(depositosIdentificado);

				batimento.getDepositosBatimento().add(depositosBatimento);
			}
			retornoDAO.salvarBatimento(batimento);
		}
	}

	public LocalDate aplicarRegraDataBatimento(Boolean arquivoRetornoGeradoHoje) {

		if (arquivoRetornoGeradoHoje.equals(false)) {
			return new LocalDate();
		} else if (arquivoRetornoGeradoHoje.equals(true)) {
			Integer contadorDeDias = 1;
			while (true) {
				LocalDate proximoDiaUtil = new LocalDate().plusDays(contadorDeDias);
				Date date = proximoDiaUtil.toDate();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					return proximoDiaUtil;
				}
				contadorDeDias++;
			}
		}
		return null;
	}

	public void removerBatimento(Remessa retorno) {
		Batimento batimento = batimentoDAO.buscarBatimentoDoRetorno(retorno);

		for (BatimentoDeposito depositosBatimento : batimento.getDepositosBatimento()) {
			Deposito deposito = depositosBatimento.getDeposito();
			List<Batimento> batimentosDoDeposito = batimentoDAO.buscarBatimentosDoDeposito(deposito);
			if (batimentosDoDeposito.size() > 1) {
				throw new InfraException("O arquivo de retorno possui um depósito vínculado a mais de um batimento! Não é possível removê-lo...");
			} else if (!batimentosDoDeposito.isEmpty()) {
				deposito.setSituacaoDeposito(SituacaoDeposito.NAO_IDENTIFICADO);
				batimentoDAO.atualizarDeposito(deposito);
			}
		}
		retornoDAO.removerBatimento(retorno, batimento);
	}

	public void liberarRetornoBatimentoInstituicao(List<Remessa> retornoLiberados) {
		retornoDAO.liberarRetornoBatimento(retornoLiberados);
	}

	public void gerarRetornos(Usuario usuarioAcao, List<Remessa> retornos) {
		HashMap<String, Arquivo> arquivosRetorno = new HashMap<String, Arquivo>();
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		this.tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.RETORNO);

		Arquivo arquivo = null;
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa retorno : retornos) {

			retorno = retornoDAO.buscarPorPK(retorno);
			if (arquivo == null || !instituicaoDestino.equals(retorno.getInstituicaoDestino())) {
				instituicaoDestino = retorno.getInstituicaoDestino();
				arquivo = criarNovoArquivoDeRetorno(instituicaoDestino, retorno);

				if (!arquivosRetorno.containsKey(instituicaoDestino.getCodigoCompensacao()) && arquivo != null) {
					List<Remessa> retornosDaInstituicao = retornoDAO.buscarRetornosConfirmadosPorInstituicao(instituicaoDestino);
					arquivo.setRemessas(retornosDaInstituicao);
					arquivosRetorno.put(instituicaoDestino.getCodigoCompensacao(), arquivo);
				}
			}
		}
		List<Arquivo> retornosArquivos = new ArrayList<Arquivo>(arquivosRetorno.values());
		retornoDAO.gerarRetornos(usuarioAcao, retornosArquivos);
	}

	private Arquivo criarNovoArquivoDeRetorno(Instituicao destino, Remessa retorno) {
		Arquivo arquivo = new Arquivo();
		arquivo.setTipoArquivo(getTipoArquivo());
		arquivo.setNomeArquivo(gerarNomeArquivoRetorno(retorno));
		arquivo.setInstituicaoRecebe(destino);
		arquivo.setInstituicaoEnvio(getCra());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setHoraEnvio(new LocalTime());
		return arquivo;
	}

	private String gerarNomeArquivoRetorno(Remessa retorno) {
		return TipoArquivoEnum.RETORNO.getConstante() + retorno.getCabecalho().getNumeroCodigoPortador() + gerarDataArquivo()
				+ NUMERO_SEQUENCIAL_RETORNO;
	}

	private String gerarDataArquivo() {
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(new Date()).toString();
	}

	public Instituicao getCra() {
		return cra;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public MensagemXml processarXML(RetornoVO retornoVO, Usuario usuario, String nomeArquivo) {
		Arquivo arquivo = new Arquivo();
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setTipoArquivo(getTipoArquivo());
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.RETORNO));
		arquivo.setStatusArquivo(getStatusEnviado());

		logger.info("Iniciar processamento do arquivo " + nomeArquivo + " enviado por " + usuario.getLogin());
		fabricaDeArquivosXML.processarRetornoXML(arquivo, retornoVO, erros);
		fabricaValidacaoArquivo.validar(arquivo, usuario, getErros());
		logger.info("Fim do processamento do arquivo " + nomeArquivo + " enviado por " + usuario.getLogin());

		arquivo = arquivoDAO.salvar(arquivo, usuario, new ArrayList<Exception>());
		return gerarResposta(arquivo, usuario);
	}

	private StatusArquivo getStatusEnviado() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	private MensagemXml gerarResposta(Arquivo arquivo, Usuario usuario) {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		MensagemXml mensagemRetorno = new MensagemXml();
		Descricao desc = new Descricao();
		Detalhamento detal = new Detalhamento();
		detal.setMensagem(mensagens);

		mensagemRetorno.setDescricao(desc);
		mensagemRetorno.setDetalhamento(detal);
		mensagemRetorno.setCodigoFinal("0000");
		mensagemRetorno.setDescricaoFinal("Arquivo processado com sucesso");

		desc.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		desc.setTipoArquivo(CraAcao.ENVIO_ARQUIVO_RETORNO.getConstante());
		desc.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		desc.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		desc.setUsuario(usuario.getNome());

		for (Remessa remessa : arquivo.getRemessas()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(getMunicipio(remessa));
			mensagem.setDescricao(formatarMensagemRetorno(remessa));
			mensagens.add(mensagem);
		}

		if (getErros() != null) {
			for (Exception ex : getErros()) {
				XmlCraException exception = XmlCraException.class.cast(ex);
				Mensagem mensagem = new Mensagem();
				mensagem.setCodigo(exception.getErro().getCodigo());
				mensagem.setMunicipio(exception.getCodigoIbge());
				mensagem.setDescricao(
						"Município: " + exception.getCodigoIbge() + " - " + exception.getMunicipio() + " - " + exception.getErro().getDescricao());
				mensagens.add(mensagem);
			}
		}
		return mensagemRetorno;
	}

	private String formatarMensagemRetorno(Remessa remessa) {
		if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return "Município: " + remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString() + " - "
					+ remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio() + " - " + remessa.getCabecalho().getQtdTitulosRemessa()
					+ " Títulos.";
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())
				|| TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return "Instituicao: " + remessa.getInstituicaoDestino().getNomeFantasia() + " - " + remessa.getCabecalho().getQtdTitulosRemessa()
					+ " títulos receberam retorno.";
		}
		return StringUtils.EMPTY;

	}

	private String getMunicipio(Remessa remessa) {
		if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString();
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())
				|| TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return remessa.getCabecalho().getNumeroCodigoPortador();
		}
		return StringUtils.EMPTY;
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}
