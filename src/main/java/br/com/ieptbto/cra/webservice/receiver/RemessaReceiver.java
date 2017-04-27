package br.com.ieptbto.cra.webservice.receiver;

import br.com.ieptbto.cra.conversor.arquivo.ConversorArquivo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoRemessaConvenioVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoRemessaSerproVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoRemessaVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.TituloConvenioException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.vo.*;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RemessaReceiver extends AbstractArquivoReceiver {

	@Autowired
	private ArquivoMediator arquivoMediator;

	@Override
	public AbstractMensagemVO receber(Usuario usuario, String nomeArquivo, String dados) {
		ArquivoRemessaVO arquivoRemessaVO = null;
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			arquivoRemessaVO = converterStringArquivoVOSerpro(dados);
		} else {
			arquivoRemessaVO = converterStringArquivoVO(dados);
		}
		List<RemessaVO> remessasVO = ConversorArquivo.conversorParaArquivoRemessa(arquivoRemessaVO);

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

	private ArquivoRemessaVO converterStringArquivoVOSerpro(String dados) {
		JAXBContext context;

		ArquivoRemessaSerproVO arquivo = null;
		try {
			context = JAXBContext.newInstance(ArquivoRemessaSerproVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", StringUtils.EMPTY);
				}
			}
			scanner.close();
			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivo = (ArquivoRemessaSerproVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Erro ao converter o contéudo xml do arquivo.");
		}
		return ArquivoRemessaSerproVO.parseToArquivoRemessaVO(arquivo);
	}

	private ArquivoRemessaVO converterStringArquivoVO(String dados) {
		JAXBContext context;

		ArquivoRemessaVO arquivo = null;
		try {
			context = JAXBContext.newInstance(ArquivoRemessaVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			boolean inicioRemessaComarca = true;
			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				String line = scanner.nextLine().replaceAll("& ", "&amp;");
				if (line.contains("<hd") && inicioRemessaComarca == true) {
					line = "<arquivo_comarca>" + line;
					inicioRemessaComarca = false;
				} else if (line.contains("<hd") && inicioRemessaComarca == false) {
					line = "</arquivo_comarca><arquivo_comarca>" + line;
				} else if (line.contains("</remessa>") && inicioRemessaComarca == false) {
					line = line.replace("</remessa>", "</arquivo_comarca></remessa>");
				}

				xmlRecebido = xmlRecebido + line;
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", StringUtils.EMPTY);
				}
			}
			scanner.close();
			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivo = (ArquivoRemessaVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Erro ao converter o contéudo xml do arquivo.");
		}
		return arquivo;
	}

	private MensagemXmlVO gerarRespostaSucesso(Arquivo arquivo, Usuario usuario) {
		List<MensagemVO> mensagens = new ArrayList<MensagemVO>();
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		DetalhamentoVO detalhamento = new DetalhamentoVO();
		detalhamento.setMensagem(mensagens);

		mensagemXml.setDescricao(descricao);
		mensagemXml.setDetalhamento(detalhamento);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_SUCESSO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_SUCESSO.getDescricao());

		descricao.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		descricao.setTipoArquivo(DescricaoVO.XML_UPLOAD_REMESSA);
		descricao.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		descricao.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		descricao.setUsuario(usuario.getNome());

		for (Remessa remessa : arquivo.getRemessas()) {
			MensagemVO mensagem = new MensagemVO();
			mensagem.setCodigo(CodigoErro.CRA_SUCESSO.getCodigo());
			mensagem.setMunicipio(remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE());
			mensagem.setDescricao("Município: " + remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString() + " - "
					+ remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio() + " - "
					+ remessa.getCabecalho().getQtdTitulosRemessa() + " Títulos.");
			mensagens.add(mensagem);
		}
		return mensagemXml;
	}

	private MensagemXmlSerproVO gerarRespostaSerproSucesso(Arquivo arquivo, Usuario usuario) {
		MensagemXmlSerproVO mensagemSerpro = new MensagemXmlSerproVO();
		mensagemSerpro.setNomeArquivo(arquivo.getNomeArquivo());

		List<ComarcaDetalhamentoSerproVO> listaComarcas = new ArrayList<ComarcaDetalhamentoSerproVO>();
		for (Remessa remessa : arquivo.getRemessas()) {
			ComarcaDetalhamentoSerproVO comarcaDetalhamento = new ComarcaDetalhamentoSerproVO();
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

	private AbstractMensagemVO gerarRespostaErrosRemessa(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			return gerarRespostaSerproErrosRemessa(arquivo, usuario, erros);
		}

		return gerarRespostaErro(arquivo, usuario, erros);
	}

	private AbstractMensagemVO gerarRespostaErro(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		List<MensagemVO> mensagens = new ArrayList<MensagemVO>();
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		DetalhamentoVO detalhamento = new DetalhamentoVO();
		detalhamento.setMensagem(mensagens);

		mensagemXml.setDescricao(descricao);
		mensagemXml.setDetalhamento(detalhamento);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao());

		descricao.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		descricao.setTipoArquivo(DescricaoVO.XML_UPLOAD_CONFIRMACAO);
		descricao.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		descricao.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		descricao.setUsuario(usuario.getNome());

		String descricaoLog = "Ocorrência(s) e(ou) erros encontrado(s) no arquivo " + arquivo.getNomeArquivo() + " enviado:</span>";
		descricaoLog = descricaoLog + "<ul>";
		for (Exception ex : erros) {
			if (CabecalhoRodapeException.class.isInstance(ex)) {
				CabecalhoRodapeException exception = CabecalhoRodapeException.class.cast(ex);
				MensagemVO mensagem = new MensagemVO();
				mensagem.setCodigo(exception.getCodigoErro().getCodigo());
				mensagem.setDescricao(exception.getDescricao());
				mensagem.setMunicipio(exception.getCodigoMunicipio());
				mensagens.add(mensagem);

				descricaoLog = descricaoLog + "<li>" + exception.getDescricao() + ";</li>";
			}
            if (TituloConvenioException.class.isInstance(ex)) {
                TituloConvenioException exception = TituloConvenioException.class.cast(ex);
                MensagemVO mensagem = new MensagemVO();
                mensagem.setCodigo(exception.getCodigoErro().getCodigo());
                mensagem.setDescricao(exception.getDescricao());
                mensagem.setNomeDevedor(exception.getNomeDevedor());
                mensagem.setNumeroTitulo(exception.getNumeroTitulo());
                mensagens.add(mensagem);
                descricaoLog = descricaoLog + "<li>" + exception.getDescricao() + ";</li>";
            }
		}
		descricaoLog = descricaoLog + "</ul>";
		if (!erros.isEmpty()) {
			loggerCra.error(usuario, CraAcao.ENVIO_ARQUIVO_REMESSA, descricaoLog);
		}
		return mensagemXml;
	}

	private AbstractMensagemVO gerarRespostaSerproErrosRemessa(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		MensagemXmlSerproVO mensagemSerpro = new MensagemXmlSerproVO();
		mensagemSerpro.setNomeArquivo(arquivo.getNomeArquivo());
		mensagemSerpro.setComarca(new ArrayList<ComarcaDetalhamentoSerproVO>());

		for (Exception ex : erros) {
			if (CabecalhoRodapeException.class.isInstance(ex)) {
				CabecalhoRodapeException exception = CabecalhoRodapeException.class.cast(ex);

				ComarcaDetalhamentoSerproVO comarcaDetalhamento = new ComarcaDetalhamentoSerproVO();
				comarcaDetalhamento.setCodigoMunicipio(exception.getCodigoMunicipio());
				comarcaDetalhamento.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
				comarcaDetalhamento.setRegistro(ConfiguracaoBase.UM);

				CodigoErro codigoErroSerpro = CodigoErro.SERPRO_ARQUIVO_INVALIDO_REMESSA_DESISTENCIA_CANCELAMENTO;
				if (CodigoErro.CRA_ARQUIVO_CORROMPIDO_SOMA_DE_REGISTROS_DE_TRANSACAO_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER
						.equals(exception.getCodigoErro())) {
					codigoErroSerpro = CodigoErro.SERPRO_QUANTIDADE_REGISTROS_INVALIDA;
				}
				comarcaDetalhamento.setCodigo(codigoErroSerpro.getCodigo());
				comarcaDetalhamento.setOcorrencia(codigoErroSerpro.getDescricao());
				comarcaDetalhamento.setTotalRegistros(0);
				mensagemSerpro.getComarca().add(comarcaDetalhamento);
			}
		}
		return mensagemSerpro;
	}

	/**
	 * @param usuario
	 * @param nomeArquivo
	 * @param dados
	 * @return
	 */
	public AbstractMensagemVO receberRemessaConvenio(Usuario usuario, String nomeArquivo, String dados) {
		List<Exception> erros = new ArrayList<Exception>();
		ArquivoRemessaConvenioVO remessaConvenioVO = converterStringArquivoConvenioVO(dados);
		
		Arquivo arquivo = arquivoMediator.salvarWSConvenio(remessaConvenioVO, usuario, nomeArquivo, erros);
		if (!erros.isEmpty()) {
			return gerarRespostaErrosRemessa(arquivo, usuario, erros);
		}
		return gerarRespostaSucesso(arquivo, usuario);
	}

    private ArquivoRemessaConvenioVO converterStringArquivoConvenioVO(String dados) {
		JAXBContext context;

		ArquivoRemessaConvenioVO arquivo = null;
		try {
			context = JAXBContext.newInstance(ArquivoRemessaConvenioVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(dados.getBytes()));
			while (scanner.hasNext()) {
				String line = scanner.nextLine().replaceAll("& ", "&amp;").replace("\\n\\t", "");
				xmlRecebido = xmlRecebido + line;
			}
			scanner.close();
			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivo = (ArquivoRemessaConvenioVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Erro ao converter o contéudo xml do arquivo.");
		}
		return arquivo;
	}
}