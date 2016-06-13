package br.com.ieptbto.cra.conversor.arquivo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
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
public class FabricaDeArquivo {

	private static final Logger logger = Logger.getLogger(FabricaDeArquivo.class);
	@Autowired
	private FabricaDeArquivoTXT fabricaDeArquivoTXT;
	@Autowired
	private FabricaDeArquivoXML fabricaDeArquivoXML;
	@Autowired
	private ConversorRemessaArquivo conversorRemessaArquivo;
	@Autowired
	private ConversorDesistenciaProtesto conversorArquivoDesistenciaProtesto;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;

	public Arquivo processarArquivoFisico(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {

		String linha = getLinhaArquivo(arquivoFisico);

		if (LayoutArquivo.TXT.equals(LayoutArquivo.get(linha))) {
			return fabricaDeArquivoTXT.fabrica(arquivoFisico, arquivo, erros).converter();
		} else if (LayoutArquivo.XML.equals(LayoutArquivo.get(linha))) {
			arquivo.setNomeArquivo(arquivoFisico.getName());
			return converterXML(arquivoFisico, arquivo, erros);
		} else {
			throw new InfraException(
					"Não foi possível identificar o layout do arquivo. Os dados internos podem estar ilegíveis ou não segue o manual FEBRABAN.");
		}
	}

	private Arquivo converterXML(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {

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
		arquivo = conversorArquivoDesistenciaProtesto.converter(arquivoVO, erros);
		return arquivo;
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
		arquivo = conversorRemessaArquivo.converter(arquivoVO, arquivo, erros);
		return arquivo;
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
		arquivo = conversorRemessaArquivo.converter(arquivoVO, arquivo, erros);
		return arquivo;
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
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		arquivo = conversorRemessaArquivo.converter(arquivoVO, arquivo, erros);
		return arquivo;
	}

	private static String getLinhaArquivo(File arquivoFisico) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoFisico)));
			String linha = reader.readLine();
			reader.close();
			return linha;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException("arquivoFisico não encontrado");
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException("arquivoFisico não encontrado");
		}
	}

	public void processarArquivoXML(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo, Arquivo arquivo, List<Exception> erros) {
		arquivo.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo)));
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setStatusArquivo(getStatusEnviado());
		fabricaDeArquivoXML.fabrica(arquivoRecebido, arquivo, erros);
	}

	private StatusArquivo getStatusEnviado() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	public void processarArquivoPersistente(Remessa remessa, File remessaTXT, List<Exception> erros) {
		fabricaDeArquivoTXT.fabricaTXT(remessaTXT, remessa, erros).converterParaTXT();
	}

	public void processarArquivoPersistente(List<Remessa> remessas, File arquivoTXT, List<Exception> erros) {
		fabricaDeArquivoTXT.fabricaArquivoTXT(arquivoTXT, remessas, erros).converterParaArquivoTXT();
	}

	public File processarArquivoPersistenteDesistenciaProtesto(RemessaDesistenciaProtesto remessa, File arquivoFisico, List<Exception> erros) {
		return fabricaDeArquivoTXT.fabricaArquivoDesistenciaProtestoTXT(arquivoFisico, remessa, erros);
	}

	public File processarArquivoPersistenteCancelamentoProtesto(RemessaCancelamentoProtesto remessa, File arquivoFisico, List<Exception> erros) {
		return fabricaDeArquivoTXT.fabricaArquivoCancelamentoProtestoTXT(arquivoFisico, remessa, erros);
	}

	public File processarArquivoPersistenteAutorizacaoCancelamentoProtesto(RemessaAutorizacaoCancelamento remessa, File arquivoFisico,
			List<Exception> erros) {
		return fabricaDeArquivoTXT.fabricaArquivoAutorizacaoCancelamentoTXT(arquivoFisico, remessa, erros);
	}
}
