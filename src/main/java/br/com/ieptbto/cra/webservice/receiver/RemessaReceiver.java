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
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.arquivo.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.util.DataUtil;
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
public class RemessaReceiver extends AbstractArquivoReceiver {

	@Autowired
	private ArquivoMediator arquivoMediator;

	@Override
	public MensagemCra receber(Usuario usuario, String nomeArquivo, String dados) {
		List<RemessaVO> remessasVO = ConversorArquivoVO.converterParaRemessaVO(converterStringArquivoVO(dados));

		List<Exception> erros = new ArrayList<Exception>();
		Arquivo arquivo = arquivoMediator.salvarWS(remessasVO, usuario, nomeArquivo, erros);
		if (!erros.isEmpty()) {
			return gerarRespostaErrosRemessa(arquivo, usuario, erros);
		}
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			return gerarRespostaSerproSucesso(arquivo, usuario);
		}
		return gerarRespostaSucesso(arquivo, usuario);
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
			logger.error(e.getMessage(), e);
			throw new InfraException("Erro ao converter o contéudo xml do arquivo.");
		}
		return arquivo;
	}

	private MensagemXml gerarRespostaSucesso(Arquivo arquivo, Usuario usuario) {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		MensagemXml mensagemXml = new MensagemXml();
		Descricao descricao = new Descricao();
		Detalhamento detalhamento = new Detalhamento();
		detalhamento.setMensagem(mensagens);

		mensagemXml.setDescricao(descricao);
		mensagemXml.setDetalhamento(detalhamento);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_SUCESSO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_SUCESSO.getDescricao());

		descricao.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		descricao.setTipoArquivo(Descricao.XML_UPLOAD_REMESSA);
		descricao.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		descricao.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		descricao.setUsuario(usuario.getNome());

		for (Remessa remessa : arquivo.getRemessas()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo(CodigoErro.CRA_SUCESSO.getCodigo());
			mensagem.setMunicipio(remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE());
			mensagem.setDescricao("Município: " + remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString() + " - "
					+ remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio() + " - " + remessa.getCabecalho().getQtdTitulosRemessa() + " Títulos.");
			mensagens.add(mensagem);
		}
		return mensagemXml;
	}

	private MensagemXmlSerpro gerarRespostaSerproSucesso(Arquivo arquivo, Usuario usuario) {
		MensagemXmlSerpro mensagemSerpro = new MensagemXmlSerpro();
		mensagemSerpro.setNomeArquivo(arquivo.getNomeArquivo());

		List<ComarcaDetalhamentoSerpro> listaComarcas = new ArrayList<ComarcaDetalhamentoSerpro>();
		for (Remessa remessa : arquivo.getRemessas()) {
			ComarcaDetalhamentoSerpro comarcaDetalhamento = new ComarcaDetalhamentoSerpro();
			comarcaDetalhamento.setCodigoMunicipio(remessa.getCabecalho().getCodigoMunicipio());
			comarcaDetalhamento.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
			comarcaDetalhamento.setRegistro(StringUtils.EMPTY);

			CodigoErro codigoErroSerpro = CodigoErro.SERPRO_SUCESSO_REMESSA;
			comarcaDetalhamento.setCodigo(codigoErroSerpro.getCodigo());
			comarcaDetalhamento.setOcorrencia(codigoErroSerpro.getDescricao());
			comarcaDetalhamento.setTotalRegistros(remessa.getCabecalho().getQtdRegistrosRemessa());
			listaComarcas.add(comarcaDetalhamento);
		}
		mensagemSerpro.setComarca(listaComarcas);
		return mensagemSerpro;
	}

	private MensagemCra gerarRespostaErrosRemessa(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			return gerarRespostaSerproErrosRemessa(arquivo, usuario, erros);
		}

		return null;
	}

	private MensagemCra gerarRespostaSerproErrosRemessa(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		// HashMap<String, ComarcaDetalhamentoSerpro> mapComarcasErros = new
		// HashMap<String, ComarcaDetalhamentoSerpro>();
		//
		// MensagemXmlSerpro mensagemSerpro = new MensagemXmlSerpro();
		// mensagemSerpro.setNomeArquivo(arquivo.getNomeArquivo());
		// for (Exception ex : erros) {
		//
		// ComarcaDetalhamentoSerpro comarcaDetalhamento = new
		// ComarcaDetalhamentoSerpro();
		// comarcaDetalhamento.setCodigoMunicipio(remessa.getCabecalho().getCodigoMunicipio());
		// comarcaDetalhamento.setDataHora(DataUtil.localDateToStringddMMyyyy(new
		// LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
		// comarcaDetalhamento.setRegistro(StringUtils.EMPTY);
		//
		// CodigoErro codigoErroSerpro = CodigoErro.SERPRO_SUCESSO_REMESSA;
		// comarcaDetalhamento.setCodigo(codigoErroSerpro.getCodigo());
		// comarcaDetalhamento.setOcorrencia(codigoErroSerpro.getDescricao());
		// comarcaDetalhamento.setTotalRegistros(remessa.getCabecalho().getQtdRegistrosRemessa());
		// listaComarcas.add(comarcaDetalhamento);
		// }
		// mensagemSerpro.setComarca(listaComarcas);
		// return mensagemSerpro;
		return null;
	}
}