package br.com.ieptbto.cra.processador;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;

/**
 * 
 * @author leandro
 *
 */
public class ProcessadorArquivoDeParaBB {

	protected static final Logger logger = Logger.getLogger(ProcessadorArquivoDeParaBB.class);
	private FileReader uploadedFile;
	private List<AgenciaBancoDoBrasil> listaAgencias;

	@Autowired
	ArquivoDeParaDAO deParaDAO;

	public ProcessadorArquivoDeParaBB() {
		iniciarProcessamento();
	}

	private void iniciarProcessamento() {
		try {
			uploadedFile = new FileReader("/usr/share/tomcat/docs/arquivoBB.txt");
			processarDados(uploadedFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void processarDados(FileReader arquivo) {
		try {
			BufferedReader reader = new BufferedReader(arquivo);
			logger.info("inicio processamento arquivo DePara BB ");
			String linha = "";
			int cont = 0;

			while ((linha = reader.readLine()) != null) {
				cont++;
				AgenciaBancoDoBrasil agenciaBancoDoBrasil = new AgenciaBancoDoBrasil();
				agenciaBancoDoBrasil.setNumeroContrato(linha.substring(1, 9));
				agenciaBancoDoBrasil.setAgenciaDestino(linha.substring(10, 13));

				listaAgencias.add(agenciaBancoDoBrasil);

				if (listaAgencias.size() >= 1000) {
					final List<AgenciaBancoDoBrasil> listAgencia = listaAgencias;
					logger.info("Linha que será salva " + cont + " :::: " + linha);
					Thread thread = new Thread() {
						@Override
						public void run() {
							deParaDAO.salvarArquivoBancoDoBrasil(listAgencia);
						}
					};
					Thread.sleep(1000);
					thread.start();
					listaAgencias = new ArrayList<>();
				}

			}
			reader.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
