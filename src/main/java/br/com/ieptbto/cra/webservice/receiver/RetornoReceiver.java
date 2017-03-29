package br.com.ieptbto.cra.webservice.receiver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.arquivo.ConversorArquivo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RetornoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.TituloException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.vo.AbstractMensagemVO;
import br.com.ieptbto.cra.webservice.vo.DescricaoVO;
import br.com.ieptbto.cra.webservice.vo.DetalhamentoVO;
import br.com.ieptbto.cra.webservice.vo.MensagemVO;
import br.com.ieptbto.cra.webservice.vo.MensagemXmlVO;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RetornoReceiver extends AbstractArquivoReceiver {

	@Autowired
	ArquivoMediator arquivoMediator;

	@Override
	public AbstractMensagemVO receber(Usuario usuario, String nomeArquivo, String dados) {
		List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();
		remessasVO.add(ConversorArquivo.conversorParaArquivoRetorno(converterStringArquivoVO(dados, nomeArquivo)));

		List<Exception> erros = new ArrayList<Exception>();
		Arquivo arquivo = arquivoMediator.salvarWS(remessasVO, usuario, nomeArquivo, erros);

		if (!erros.isEmpty()) {
			return gerarRespostaErrosRetorno(arquivo, usuario, erros, dados);
		}
		return gerarRespostaSucesso(arquivo, usuario);
	}

	private RetornoVO converterStringArquivoVO(String dados, String nomeArquivo) {
		JAXBContext context;
		RetornoVO arquivo = null;

		try {
			context = JAXBContext.newInstance(RetornoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			if (dados.contains("<?xml version=")) {
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			}
			dados = dados.replaceAll("& ", "&amp;");

			InputStream xml = new ByteArrayInputStream(dados.getBytes());
			arquivo = (RetornoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Erro ao converter o contéudo xml do arquivo " + nomeArquivo + ".");
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
		descricao.setTipoArquivo(CraAcao.ENVIO_ARQUIVO_RETORNO.getConstante());
		descricao.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		descricao.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		descricao.setUsuario(usuario.getNome());

		for (Remessa remessa : arquivo.getRemessas()) {
			MensagemVO mensagem = new MensagemVO();
			mensagem.setCodigo(CodigoErro.CRA_SUCESSO.getCodigo());
			mensagem.setMunicipio(remessa.getCabecalho().getCodigoMunicipio());
			mensagem.setDescricao("Instituicao: " + remessa.getInstituicaoDestino().getNomeFantasia() + " - "
					+ remessa.getCabecalho().getQtdTitulosRemessa() + " títulos receberam retorno.");
			mensagens.add(mensagem);
		}
        return mensagemXml;
	}

	private MensagemXmlVO gerarRespostaErrosRetorno(Arquivo arquivo, Usuario usuario, List<Exception> erros, String dados) {
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
		descricao.setTipoArquivo(DescricaoVO.XML_UPLOAD_RETORNO);
		descricao.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		descricao.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		descricao.setUsuario(usuario.getNome());

		String descricaoLog = "Ocorrência(s) e(ou) erros encontrado(s) no arquivo " + arquivo.getNomeArquivo() + " enviado:</span>";
		descricaoLog = descricaoLog + "<ul>";
		for (Exception ex : erros) {
			if (TituloException.class.isInstance(ex)) {
				TituloException exception = TituloException.class.cast(ex);
				MensagemVO mensagem = new MensagemVO();
				mensagem.setCodigo(exception.getCodigoErro().getCodigo());
				mensagem.setDescricao(exception.getDescricao());
				mensagem.setNossoNumero(exception.getNossoNumero());
				mensagem.setNumeroProtocoloCartorio(exception.getNumeroProtocoloCartorio());
				mensagem.setNumeroSequencialRegistro(Integer.valueOf(exception.getNumeroSequencialRegistro()));
				mensagens.add(mensagem);

				descricaoLog =
						descricaoLog + "<li><span class=\"alert-link\">Nº Sequencial do Registro " + exception.getNumeroSequencialRegistro()
								+ ": </span> [ Nosso Número = " + exception.getNossoNumero() + " ] " + exception.getDescricao() + ";</li>";
			}
			if (CabecalhoRodapeException.class.isInstance(ex)) {
				CabecalhoRodapeException exception = CabecalhoRodapeException.class.cast(ex);
				MensagemVO mensagem = new MensagemVO();
				mensagem.setCodigo(exception.getCodigoErro().getCodigo());
				mensagem.setDescricao(exception.getDescricao());
				mensagens.add(mensagem);

				descricaoLog = descricaoLog + "<li>" + exception.getDescricao() + ";</li>";
			}
		}
		descricaoLog = descricaoLog + "</ul>";
		if (!erros.isEmpty()) {
			loggerCra.error(usuario, CraAcao.ENVIO_ARQUIVO_RETORNO, descricaoLog, dados);
		}
		return mensagemXml;
	}
}