package br.com.ieptbto.cra.fabrica;

import br.com.ieptbto.cra.conversor.AbstractFabricaDeArquivo;
import br.com.ieptbto.cra.conversor.arquivo.ConversorArquivo;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaCancelamento;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRemessaArquivo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.vo.*;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaDeArquivoXML extends AbstractFabricaDeArquivo {

	@Autowired
	TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	ConversorRemessaArquivo conversorRemessaArquivo;
	@Autowired
	ConversorDesistenciaCancelamento conversorDesistenciaCancelamento;

	/**
	 * @param arquivoFisico
	 * @param arquivo
	 * @param erros
	 * @return
	 */
	public Arquivo converter(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		if (TipoArquivoFebraban.REMESSA.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterRemessa(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterConfirmacao(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoFebraban.RETORNO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterRetorno(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterDesistenciaProtesto(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			logger.error("Não é possível o envio de arquivos do tipo Cancelamento de Protesto desta forma. Favor entrar em contato com a CRA....");
			throw new InfraException("Não é possível o envio de arquivos do tipo Cancelamento de Protesto desta forma. Favor entrar em contato com a CRA...");
		} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			logger.error("Não é possível o envio de arquivos do tipo Autorização de Cancelamento desta forma. Favor entrar em contato com a CRA...");
			throw new InfraException("Não é possível o envio de arquivos do tipo Autorização de Cancelamento desta forma. Favor entrar em contato com a CRA...");
		}
		return null;
	}

	/**
	 * @param arquivoRecebido
	 * @param arquivo
	 * @param erros
	 * @return
	 */
	public Arquivo converterWS(List<RemessaVO> arquivoRecebido, Arquivo arquivo, List<Exception> erros) {
		return conversorRemessaArquivo.converterParaArquivo(arquivoRecebido, arquivo, erros);
	}

	private Arquivo converterDesistenciaProtesto(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoDesistenciaProtestoVO arquivoVO = new ArquivoDesistenciaProtestoVO();
		try {
			context = JAXBContext.newInstance(ArquivoDesistenciaProtestoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {
				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoDesistenciaProtestoVO) unmarshaller.unmarshal(new InputSource(xml));
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO));
		} catch (JAXBException e) {
			System.out.println(e.getMessage());
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorDesistenciaCancelamento.converterParaArquivo(arquivoVO, arquivo, erros);
	}

	private Arquivo converterRetorno(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		RetornoVO arquivoVO = new RetornoVO();
		try {
			context = JAXBContext.newInstance(RetornoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {
				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (RetornoVO) unmarshaller.unmarshal(new InputSource(xml));
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoFebraban.RETORNO));

		} catch (JAXBException | IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
        List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();
		remessasVO.add(ConversorArquivo.conversorParaArquivoRetorno(arquivoVO));
		return conversorRemessaArquivo.converterParaArquivo(remessasVO, arquivo, erros);
	}

	private Arquivo converterConfirmacao(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;

		ConfirmacaoVO arquivoVO = new ConfirmacaoVO();
		try {
			context = JAXBContext.newInstance(ConfirmacaoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {

				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ConfirmacaoVO) unmarshaller.unmarshal(new InputSource(xml));
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoFebraban.CONFIRMACAO));

		} catch (JAXBException | IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
        List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();
		remessasVO.add(ConversorArquivo.conversorParaArquivoConfirmacao(arquivoVO));
		return conversorRemessaArquivo.converterParaArquivo(remessasVO, arquivo, erros);
	}

	private Arquivo converterRemessa(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;

		ArquivoRemessaVO arquivoVO = new ArquivoRemessaVO();
		try {
			if (arquivo.getInstituicaoEnvio().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
				context = JAXBContext.newInstance(ArquivoRemessaSerproVO.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				String xmlRecebido = "";

				Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
				while (scanner.hasNext()) {
					xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
					if (xmlRecebido.contains("<?xml version=")) {
						xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", StringUtils.EMPTY);
					}
				}
				scanner.close();
				InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
				ArquivoRemessaSerproVO arquivoRemessaSerpro = (ArquivoRemessaSerproVO) unmarshaller.unmarshal(new InputSource(xml));
				arquivoVO = ArquivoRemessaSerproVO.parseToArquivoRemessaVO(arquivoRemessaSerpro);

			} else {
				context = JAXBContext.newInstance(ArquivoRemessaVO.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				String xmlRecebido = "";

				boolean inicioRemessaComarca = true;
				Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
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
				arquivoVO = (ArquivoRemessaVO) unmarshaller.unmarshal(new InputSource(xml));
			}

		} catch (JAXBException | IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
        List<RemessaVO> remessasVO = ConversorArquivo.conversorParaArquivoRemessa(arquivoVO);
		return conversorRemessaArquivo.converterParaArquivo(remessasVO, arquivo, erros);
	}
}