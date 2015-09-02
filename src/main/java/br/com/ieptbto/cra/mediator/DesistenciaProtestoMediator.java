package br.com.ieptbto.cra.mediator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.arquivo.ConversorArquivoDesistenciaProtesto;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class DesistenciaProtestoMediator {

	protected static final Logger logger = Logger.getLogger(DesistenciaProtestoMediator.class);
	@Autowired
	private ConversorArquivoDesistenciaProtesto conversorArquivoDesistenciaProtesto;
	@Autowired
	private ArquivoDAO arquivoDAO;

	private ArquivoDesistenciaProtestoVO converterStringParaVO(String dados) {
		JAXBContext context;
		ArquivoDesistenciaProtestoVO desistenciaVO = null;

		try {
			context = JAXBContext.newInstance(ArquivoDesistenciaProtestoVO.class);
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
			desistenciaVO = (ArquivoDesistenciaProtestoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return desistenciaVO;
	}

	@Transactional
	public Arquivo processar(Arquivo arquivo, String dados, List<Exception> erros, Usuario usuario) {
		arquivo = conversorArquivoDesistenciaProtesto.converter(converterStringParaVO(dados), erros);
		arquivo = salvarArquivo(arquivo, usuario, erros);
		return arquivo;
	}

	private Arquivo salvarArquivo(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		return arquivoDAO.salvar(arquivo, usuario, erros);
	}

}
