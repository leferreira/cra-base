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
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoRetornoVO;
import br.com.ieptbto.cra.entidade.vo.RetornoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.XmlCraException;
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
public class RetornoReceiver implements IArquivoReceiver {

	protected static final Logger logger = Logger.getLogger(ConfirmacaoReceiver.class);

	@Autowired
	ArquivoMediator arquivoMediator;

	private List<Exception> erros;

	@Override
	public MensagemCra receber(Usuario usuario, String nomeArquivo, String dados) {
		ArquivoRetornoVO arquivoRetornoVO = converterStringArquivoVO(dados);
		RetornoVO retornoVO = ConversorArquivoVO.converterParaRemessaVO(arquivoRetornoVO);

		ArquivoMediator arquivoRetorno = arquivoMediator.salvarWS(null, usuario, nomeArquivo);
		if (!arquivoRetorno.getErros().isEmpty()) {

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
		return erros;
	}
}