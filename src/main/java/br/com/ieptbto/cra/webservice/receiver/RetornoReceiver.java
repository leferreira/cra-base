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
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.arquivo.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoRetornoVO;
import br.com.ieptbto.cra.entidade.vo.RetornoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.TituloException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;
import br.com.ieptbto.cra.webservice.VO.MensagemErroCartorio;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RetornoReceiver extends AbstractArquivoReceiver {

	@Override
	public MensagemCra receber(Usuario usuario, String nomeArquivo, String dados) {
		ArquivoRetornoVO arquivoRetornoVO = converterStringArquivoVO(dados);
		RetornoVO retornoVO = ConversorArquivoVO.converterParaRemessaVO(arquivoRetornoVO);

		ArquivoMediator arquivoRetorno = arquivoMediator.salvarWS(null, usuario, nomeArquivo);
		if (!arquivoRetorno.getErros().isEmpty()) {
			return gerarRespostaErrosRetorno(arquivoRetorno.getArquivo(), usuario, arquivoRetorno.getErros());
		}
		return gerarResposta(arquivoRetorno.getArquivo(), usuario);
	}

	private ArquivoRetornoVO converterStringArquivoVO(String dados) {
		JAXBContext context;
		ArquivoRetornoVO arquivo = null;

		try {
			context = JAXBContext.newInstance(ArquivoRetornoVO.class);
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
			arquivo = (ArquivoRetornoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return arquivo;
	}

	private MensagemXml gerarResposta(Arquivo arquivo, Usuario usuario) {
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
		descricao.setTipoArquivo(CraAcao.ENVIO_ARQUIVO_RETORNO.getConstante());
		descricao.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		descricao.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		descricao.setUsuario(usuario.getNome());

		for (Remessa remessa : arquivo.getRemessas()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo(CodigoErro.CRA_SUCESSO.getCodigo());
			mensagem.setMunicipio(remessa.getCabecalho().getCodigoMunicipio());
			mensagem.setDescricao("Instituicao: " + remessa.getInstituicaoDestino().getNomeFantasia() + " - "
					+ remessa.getCabecalho().getQtdTitulosRemessa() + " títulos receberam retorno.");
			mensagens.add(mensagem);
		}
		return mensagemXml;
	}

	private MensagemCra gerarRespostaErrosRetorno(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
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

		for (Exception ex : erros) {
			TituloException exception = TituloException.class.cast(ex);
			MensagemErroCartorio mensagem = new MensagemErroCartorio();
			mensagem.setCodigo(exception.getCodigoErro().getCodigo());
			mensagem.setDescricao(exception.getDescricao());
			mensagem.setNossoNumero(exception.getNossoNumero());
			mensagem.setNumeroSequencialRegistro(Integer.valueOf(exception.getNumeroSequencialRegistro()));
			mensagens.add(mensagem);
		}
		return mensagemXml;
	}
}