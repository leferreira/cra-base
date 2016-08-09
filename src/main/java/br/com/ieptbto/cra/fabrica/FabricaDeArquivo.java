package br.com.ieptbto.cra.fabrica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.exception.InfraException;

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

	/**
	 * Método responsável em receber os arquivos físicos pela aplicação
	 * (txt/xml)
	 * 
	 * @param arquivoFisico
	 * @param arquivo
	 * @param erros
	 * @return
	 */
	public Arquivo fabricaAplicacao(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		String linha = getLinhaArquivo(arquivoFisico);

		if (LayoutArquivo.TXT.equals(LayoutArquivo.get(linha))) {
			return fabricaDeArquivoTXT.converter(arquivoFisico, arquivo, erros);
		} else if (LayoutArquivo.XML.equals(LayoutArquivo.get(linha))) {
			return fabricaDeArquivoXML.converter(arquivoFisico, arquivo, erros);
		} else {
			throw new InfraException(
					"Não foi possível identificar o layout do arquivo. Os dados internos podem estar ilegíveis ou não segue o manual FEBRABAN.");
		}
	}

	private static String getLinhaArquivo(File arquivoFisico) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoFisico)));
			String linha = reader.readLine();
			reader.close();
			return linha;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Arquivo não encontrado!");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Arquivo não encontrado!");
		}
	}

	/**
	 * @param arquivoRecebido
	 * @param arquivo
	 * @param erros
	 * @return
	 */
	public Arquivo fabricaWS(List<RemessaVO> arquivoRecebido, Arquivo arquivo, List<Exception> erros) {
		return null;
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