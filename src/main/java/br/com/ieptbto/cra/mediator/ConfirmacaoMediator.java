package br.com.ieptbto.cra.mediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import br.com.ieptbto.cra.dao.ConfirmacaoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ConfirmacaoVO;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.validacao.FabricaValidacaoArquivo;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;

/**
 * @author thasso
 *
 */
@Service
public class ConfirmacaoMediator {

	private static final int NUMERO_SEQUENCIAL_CONFIRMACAO = 1;
	protected static final Logger logger = Logger.getLogger(ConfirmacaoMediator.class);

	@Autowired
	private ConfirmacaoDAO confirmacaoDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private FabricaDeArquivoXML fabricaDeArquivosXML;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private FabricaValidacaoArquivo fabricaValidacaoArquivo;
	private Instituicao cra;
	private TipoArquivo tipoArquivo;
	private List<Exception> erros;

	public List<Remessa> buscarConfirmacoesPendentesDeEnvio() {
		return confirmacaoDAO.buscarConfirmacoesPendentesDeEnvio();
	}

	public Boolean verificarArquivoConfirmacaoGeradoCra() {
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		return confirmacaoDAO.verificarArquivoConfirmacaoGeradoCra(getCra());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public MensagemXml processarXML(ConfirmacaoVO confirmacaoVO, Usuario usuario, String nomeArquivo) {
		Arquivo arquivo = new Arquivo();

		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.CONFIRMACAO));
		arquivo.setStatusArquivo(getStatusEnviado());

		logger.info("Iniciar processamento do arquivo " + nomeArquivo + " enviado por " + usuario.getLogin());
		fabricaDeArquivosXML.processarConfirmacaoXML(arquivo, confirmacaoVO, getErros());
		fabricaValidacaoArquivo.validar(arquivo, usuario, getErros());
		logger.info("Fim de processamento do arquivo " + nomeArquivo + " enviado por " + usuario.getLogin());

		arquivo = arquivoDAO.salvar(arquivo, usuario, new ArrayList<Exception>());
		return gerarResposta(arquivo, usuario);
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
		desc.setTipoArquivo(Descricao.XML_UPLOAD_REMESSA);
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
					+ " títulos receberam confirmação.";
		}
		return StringUtils.EMPTY;

	}

	public void gerarConfirmacoes(Usuario usuarioCorrente, List<Remessa> confirmacoesParaEnvio) {
		List<Arquivo> arquivosDeConfirmacao = new ArrayList<Arquivo>();
		setCra(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));
		setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.CONFIRMACAO));

		Arquivo arquivo = null;
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa confirmacao : confirmacoesParaEnvio) {

			if (arquivo == null || !instituicaoDestino.equals(confirmacao.getInstituicaoDestino())) {
				instituicaoDestino = confirmacao.getInstituicaoDestino();
				arquivo = criarNovoArquivoDeConfirmacao(instituicaoDestino, confirmacao);

				List<Remessa> confirmacoes = confirmacaoDAO.buscarConfirmacoesPendentesPorInstituicao(instituicaoDestino);
				arquivo.setRemessas(confirmacoes);

				if (!arquivosDeConfirmacao.contains(arquivo) && arquivo != null) {
					arquivosDeConfirmacao.add(arquivo);
				}
			}
		}
		confirmacaoDAO.salvarArquivosDeConfirmacaoGerados(usuarioCorrente, arquivosDeConfirmacao);
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

	private Arquivo criarNovoArquivoDeConfirmacao(Instituicao destino, Remessa confirmacao) {
		Arquivo arquivo = new Arquivo();
		arquivo.setTipoArquivo(getTipoArquivo());
		arquivo.setNomeArquivo(gerarNomeArquivoConfirmacao(confirmacao));
		arquivo.setInstituicaoRecebe(destino);
		arquivo.setInstituicaoEnvio(getCra());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		return arquivo;
	}

	private StatusArquivo getStatusEnviado() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	private String gerarNomeArquivoConfirmacao(Remessa confirmacao) {

		return TipoArquivoEnum.CONFIRMACAO.getConstante() + confirmacao.getCabecalho().getNumeroCodigoPortador() + gerarDataArquivo()
				+ NUMERO_SEQUENCIAL_CONFIRMACAO;
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

	public void setCra(Instituicao cra) {
		this.cra = cra;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}
}
