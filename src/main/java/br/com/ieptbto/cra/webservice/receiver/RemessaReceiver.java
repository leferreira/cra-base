package br.com.ieptbto.cra.webservice.receiver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;
import br.com.ieptbto.cra.webservice.VO.ComarcaDetalhamentoSerpro;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;
import br.com.ieptbto.cra.webservice.VO.MensagemXmlSerpro;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RemessaReceiver implements IArquivoReceiver {

	protected static final Logger logger = Logger.getLogger(RemessaReceiver.class);

	@Autowired
	ArquivoMediator arquivoMediator;

	private List<Exception> erros;

	@Override
	public MensagemCra receber(Usuario usuario, String nomeArquivo, String dados) {
		List<RemessaVO> remessasVO = ConversorArquivoVO.converterParaRemessaVO(converterStringArquivoVO(dados));

		ArquivoMediator arquivoRetorno = arquivoMediator.salvarWS(remessasVO, usuario, nomeArquivo);
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			return gerarRespostaSerpro(arquivoRetorno.getArquivo(), usuario);
		}
		return gerarResposta(arquivoRetorno.getArquivo(), usuario);
	}

	private ArquivoVO converterStringArquivoVO(String dados) {
		JAXBContext context;

		ArquivoVO arquivo = null;
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", StringUtils.EMPTY);
				}
				if (xmlRecebido.contains("<comarca CodMun")) {
					xmlRecebido = xmlRecebido.replaceAll("<comarca CodMun=.[0-9]+..", StringUtils.EMPTY);
				}
				if (xmlRecebido.contains("</comarca>")) {
					xmlRecebido = xmlRecebido.replaceAll("</comarca>", StringUtils.EMPTY);
				}
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivo = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return arquivo;
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
			mensagem.setDescricao(
					"Município: " + exception.getCodigoIbge() + " - " + exception.getMunicipio() + " - " + exception.getErro().getDescricao());
			mensagens.add(mensagem);
		}

		return mensagemRetorno;
	}

	private MensagemXmlSerpro gerarRespostaSerpro(Arquivo arquivo, Usuario usuario) {
		MensagemXmlSerpro msgSucesso = new MensagemXmlSerpro();
		msgSucesso.setNomeArquivo(arquivo.getNomeArquivo());

		List<ComarcaDetalhamentoSerpro> listaComarcas = new ArrayList<ComarcaDetalhamentoSerpro>();
		for (Remessa remessa : arquivo.getRemessas()) {
			ComarcaDetalhamentoSerpro comarcaDetalhamento = new ComarcaDetalhamentoSerpro();
			comarcaDetalhamento.setCodigoMunicipio(remessa.getCabecalho().getCodigoMunicipio());
			comarcaDetalhamento.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
			comarcaDetalhamento.setRegistro(StringUtils.EMPTY);

			CodigoErro codigoErroSerpro = getCodigoErroSucessoSerpro(arquivo.getNomeArquivo());
			comarcaDetalhamento.setCodigo(codigoErroSerpro.getCodigo());
			comarcaDetalhamento.setOcorrencia(codigoErroSerpro.getDescricao());
			comarcaDetalhamento.setTotalRegistros(remessa.getCabecalho().getQtdRegistrosRemessa());
			listaComarcas.add(comarcaDetalhamento);
		}

		msgSucesso.setComarca(listaComarcas);
		return msgSucesso;
	}

	private CodigoErro getCodigoErroSucessoSerpro(String nomeArquivo) {
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);

		CodigoErro codigoErro = null;
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_SUCESSO_REMESSA;
		} else if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo) || TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO;
		} else {
			codigoErro = CodigoErro.CRA_SUCESSO;
		}
		return codigoErro;
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

	private String formatarMensagemRetorno(Remessa remessa) {
		if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return "Município: " + remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString() + " - "
					+ remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio() + " - " + remessa.getCabecalho().getQtdTitulosRemessa()
					+ " Títulos.";
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())
				|| TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return "Instituicao: " + remessa.getInstituicaoDestino().getNomeFantasia() + " - " + remessa.getCabecalho().getQtdTitulosRemessa()
					+ " títulos confirmados.";
		}
		return StringUtils.EMPTY;
	}

	public List<Exception> getErros() {
		if (erros == null) {
			this.erros = new ArrayList<Exception>();
		}
		return erros;
	}
}
