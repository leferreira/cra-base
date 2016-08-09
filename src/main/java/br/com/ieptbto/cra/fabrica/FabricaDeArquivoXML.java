package br.com.ieptbto.cra.fabrica;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.AbstractFabricaDeArquivo;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaProtesto;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRemessaArquivo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaDeArquivoXML extends AbstractFabricaDeArquivo {

	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private ConversorRemessaArquivo conversorRemessaArquivo;
	@Autowired
	private ConversorDesistenciaProtesto conversorDesistenciaProtesto;

	/**
	 * @param arquivoFisico
	 * @param arquivo
	 * @param erros
	 * @return
	 */
	public Arquivo converter(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		if (TipoArquivoEnum.REMESSA.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterRemessa(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterConfirmacao(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoEnum.RETORNO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterRetorno(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterDesistenciaProtesto(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {

		} else if (TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {

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
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO));
		} catch (JAXBException e) {
			System.out.println(e.getMessage());
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorDesistenciaProtesto.converterParaArquivo(arquivoVO, arquivo, erros);
	}

	private Arquivo converterRetorno(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {

				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
				xmlGerado = xmlGerado.replaceAll("retorno", "remessa").trim();
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.RETORNO));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorRemessaArquivo.converterParaArquivo(arquivoVO, arquivo, erros);
	}

	private Arquivo converterConfirmacao(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {

				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
				xmlGerado = xmlGerado.replaceAll("confirmacao", "remessa").trim();
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.CONFIRMACAO));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorRemessaArquivo.converterParaArquivo(arquivoVO, arquivo, erros);
	}

	private Arquivo converterRemessa(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {

				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorRemessaArquivo.converterParaArquivo(arquivoVO, arquivo, erros);
	}
}