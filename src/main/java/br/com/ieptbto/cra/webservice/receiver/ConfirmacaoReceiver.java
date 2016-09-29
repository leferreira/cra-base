package br.com.ieptbto.cra.webservice.receiver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.arquivo.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.TituloException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ConfirmacaoReceiver extends AbstractArquivoReceiver {

	@Autowired
	private ArquivoMediator arquivoMediator;

	@Override
	public MensagemCra receber(Usuario usuario, String nomeArquivo, String dados) {
		List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();
		ConfirmacaoVO confirmacaoVO = converterStringArquivoVO(dados, nomeArquivo);
		remessasVO.add(ConversorArquivoVO.converterConfirmacaoParaRemessaVO(confirmacaoVO));

		List<Exception> erros = new ArrayList<Exception>();
		Arquivo arquivo = arquivoMediator.salvarWS(remessasVO, usuario, nomeArquivo, erros);
		if (!erros.isEmpty()) {
			return gerarRespostaErrosConfirmacao(arquivo, usuario, erros);
		}
		return gerarRespostaSucesso(arquivo, usuario);
	}

	private ConfirmacaoVO converterStringArquivoVO(String dados, String nomeArquivo) {
		JAXBContext context;
		ConfirmacaoVO arquivo = null;

		try {
			context = JAXBContext.newInstance(ConfirmacaoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
				}
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivo = (ConfirmacaoVO) unmarshaller.unmarshal(new InputSource(xml));
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Erro ao converter o contéudo xml do arquivo " + nomeArquivo + ".");
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
		descricao.setTipoArquivo(Descricao.XML_UPLOAD_CONFIRMACAO);
		descricao.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		descricao.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		descricao.setUsuario(usuario.getNome());

		for (Remessa remessa : arquivo.getRemessas()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo(CodigoErro.CRA_SUCESSO.getCodigo());
			mensagem.setMunicipio(remessa.getCabecalho().getCodigoMunicipio());
			mensagem.setDescricao("Instituicao: " + remessa.getInstituicaoDestino().getNomeFantasia() + " - " + remessa.getCabecalho().getQtdTitulosRemessa()
					+ " títulos receberam confirmação.");
			mensagens.add(mensagem);
		}
		return mensagemXml;
	}

	private MensagemCra gerarRespostaErrosConfirmacao(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		MensagemXml mensagemXml = new MensagemXml();
		Descricao descricao = new Descricao();
		Detalhamento detalhamento = new Detalhamento();
		detalhamento.setMensagem(mensagens);

		mensagemXml.setDescricao(descricao);
		mensagemXml.setDetalhamento(detalhamento);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao());

		descricao.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		descricao.setTipoArquivo(Descricao.XML_UPLOAD_CONFIRMACAO);
		descricao.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		descricao.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		descricao.setUsuario(usuario.getNome());

		String descricaoLog = "Ocorrência(s) e(ou) erros encontrado(s) no arquivo " + arquivo.getNomeArquivo() + " enviado:</span>";
		descricaoLog = descricaoLog + "<ul>";
		for (Exception ex : erros) {
			if (TituloException.class.isInstance(ex)) {
				TituloException exception = TituloException.class.cast(ex);
				Mensagem mensagem = new Mensagem();
				mensagem.setCodigo(exception.getCodigoErro().getCodigo());
				mensagem.setDescricao(exception.getDescricao());
				mensagem.setNossoNumero(exception.getNossoNumero());
				mensagem.setNumeroSequencialRegistro(Integer.valueOf(exception.getNumeroSequencialRegistro()));
				mensagens.add(mensagem);

				descricaoLog = descricaoLog + "<li><span class=\"alert-link\">Linha " + exception.getNumeroSequencialRegistro() + ": </span> [ Nosso Número = "
						+ exception.getNossoNumero() + " ] " + exception.getDescricao() + ";</li>";
			}
			if (CabecalhoRodapeException.class.isInstance(ex)) {
				CabecalhoRodapeException exception = CabecalhoRodapeException.class.cast(ex);
				Mensagem mensagem = new Mensagem();
				mensagem.setCodigo(exception.getCodigoErro().getCodigo());
				mensagem.setDescricao(exception.getDescricao());
				mensagens.add(mensagem);

				descricaoLog = descricaoLog + "<li>" + exception.getDescricao() + ";</li>";
			}
		}
		descricaoLog = descricaoLog + "</ul>";
		if (!erros.isEmpty()) {
			loggerCra.error(usuario, CraAcao.ENVIO_ARQUIVO_CONFIRMACAO, descricaoLog);
		}
		return mensagemXml;
	}
}