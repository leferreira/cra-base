package br.com.ieptbto.cra.mediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	ConfirmacaoDAO confirmacaoDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	FabricaDeArquivoXML fabricaDeArquivosXML;
	@Autowired
	TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	ArquivoDAO arquivoDAO;

	private Instituicao cra;
	private TipoArquivo tipoArquivo;
	private Arquivo arquivo;
	private List<Exception> erros;

	public List<Remessa> buscarConfirmacoesPendentesDeEnvio() {
		return confirmacaoDAO.buscarConfirmacoesPendentesDeEnvio();
	}
	
	public Boolean verificarArquivoConfirmacaoGeradoCra(){
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		return confirmacaoDAO.verificarArquivoConfirmacaoGeradoCra(getCra());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MensagemXml processarXML(ConfirmacaoVO confirmacaoVO, Usuario usuario, String nomeArquivo) {
		this.arquivo = new Arquivo();
		getArquivo().setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		getArquivo().setHoraEnvio(new LocalTime());
		getArquivo().setInstituicaoEnvio(usuario.getInstituicao());
		getArquivo().setNomeArquivo(nomeArquivo);
		getArquivo().setTipoArquivo(getTipoArquivo());
		getArquivo().setUsuarioEnvio(usuario);
		getArquivo().setRemessas(new ArrayList<Remessa>());
		getArquivo().setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.CONFIRMACAO));
		getArquivo().setStatusArquivo(getStatusEnviado());

		logger.info("Iniciar processo do arquivo " + nomeArquivo);

		fabricaDeArquivosXML.processarConfirmacaoXML(getArquivo(), confirmacaoVO, erros);

		logger.info("Fim de processo do arquivo " + nomeArquivo);

		setArquivo(salvarArquivo(getArquivo(), usuario));

		return gerarResposta(getArquivo(), usuario);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo salvarArquivo(Arquivo arquivo, Usuario usuario) {
		return arquivoDAO.salvar(arquivo, usuario, new ArrayList<Exception>());
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

		for (Exception ex : getErros()) {
			XmlCraException exception = XmlCraException.class.cast(ex);
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo(exception.getErro().getCodigo());
			mensagem.setMunicipio(exception.getCodigoIbge());
			mensagem.setDescricao("Município: " + exception.getCodigoIbge() + " - " + exception.getMunicipio() + " - "
			        + exception.getErro().getDescricao());
			mensagens.add(mensagem);
		}

		return mensagemRetorno;
	}

	public void gerarConfirmacoes(Usuario usuarioCorrente, List<Remessa> confirmacoesParaEnvio) {
		List<Arquivo> arquivosDeConfirmacao = new ArrayList<Arquivo>();
		setCra(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));
		setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.CONFIRMACAO));

		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa confirmacao : confirmacoesParaEnvio) {

			if (arquivo == null || !instituicaoDestino.equals(confirmacao.getInstituicaoDestino())) {
				instituicaoDestino = confirmacao.getInstituicaoDestino();
				criarNovoArquivoDeConfirmacao(instituicaoDestino, confirmacao);

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
		return "";
	}

	private void criarNovoArquivoDeConfirmacao(Instituicao destino, Remessa confirmacao) {
		this.arquivo = new Arquivo();
		getArquivo().setTipoArquivo(getTipoArquivo());
		getArquivo().setNomeArquivo(gerarNomeArquivoConfirmacao(confirmacao));
		getArquivo().setInstituicaoRecebe(destino);
		getArquivo().setInstituicaoEnvio(getCra());
		getArquivo().setDataEnvio(new LocalDate());
		getArquivo().setHoraEnvio(new LocalTime());
	}

	private String formatarMensagemRetorno(Remessa remessa) {
		if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return "Município: " + remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString() + " - "
			        + remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio() + " - "
			        + remessa.getCabecalho().getQtdTitulosRemessa() + " Títulos.";
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())
		        || TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return "Instituicao: " + remessa.getInstituicaoDestino().getNomeFantasia() + " - "
			        + remessa.getCabecalho().getQtdTitulosRemessa() + " títulos confirmados.";
		}
		return "";

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

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setCra(Instituicao cra) {
		this.cra = cra;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public List<Exception> getErros() {
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}
}
