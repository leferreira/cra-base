package br.com.ieptbto.cra.fabrica;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.exception.InfraException;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

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
	 * Método responsável em receber os arquivos pelo ws
	 * 
	 * @param arquivoRecebido
	 * @param arquivo
	 * @param erros
	 * @return
	 */
	public Arquivo fabricaWS(List<RemessaVO> arquivoRecebido, Arquivo arquivo, List<Exception> erros) {
		return fabricaDeArquivoXML.converterWS(arquivoRecebido, arquivo, erros);
	}

	public File baixarRemessaConfirmacaoRetornoTXT(Remessa remessa, File file) {
		return fabricaDeArquivoTXT.fabricaArquivoCartorioTXT(file, remessa);
	}

	public File baixarRemessaConfirmacaoRetornoTXT(List<Remessa> remessas, File file) {
		return fabricaDeArquivoTXT.fabricaArquivoInstituicaoConvenioTXT(file, remessas);
	}

	public File baixarRetornoRecebimentoEmpresaTXT(List<Remessa> remessas, File file, Instituicao instituicao, LocalDate dataGeracao, Integer sequencialArquivo) {
		return fabricaDeArquivoTXT.baixarRetornoRecebimentoEmpresaTXT(file, remessas, instituicao, dataGeracao, sequencialArquivo);
	}
	
	public File baixarDesistenciaTXT(RemessaDesistenciaProtesto remessa, File file) {
		return fabricaDeArquivoTXT.fabricaArquivoDesistenciaProtestoTXT(file, remessa);
	}

	public File baixarCancelamentoTXT(RemessaCancelamentoProtesto remessa, File file) {
		return fabricaDeArquivoTXT.fabricaArquivoCancelamentoProtestoTXT(file, remessa);
	}

	public File baixarAutorizacaoCancelamentoTXT(RemessaAutorizacaoCancelamento remessa, File file) {
		return fabricaDeArquivoTXT.fabricaArquivoAutorizacaoCancelamentoTXT(file, remessa);
	}
}