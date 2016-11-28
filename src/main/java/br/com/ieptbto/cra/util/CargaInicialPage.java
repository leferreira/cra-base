package br.com.ieptbto.cra.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.mediator.AdministracaoMediator;

/**
 * 
 * 
 */
public class CargaInicialPage extends WebPage {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private AdministracaoMediator administracaoMediator;

	public CargaInicialPage() {
		String municipioParametro = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("codigoMunicipio").toString();

		if (municipioParametro != null) {
			if (!municipioParametro.trim().isEmpty()) {
				administracaoMediator.gerarArquivo5AnosPorMunicipio(municipioParametro);
			}
		} else {
			administracaoMediator.gerarArquivo5AnosTocantins();
		}
	}

	public static void main(String[] args) {
		BufferedReader reader = null;
		File diretorio = new File("C:\\Users\\Thasso Araújo\\Documents\\jboss-as-7.1.1.Final\\ARQUIVOS_CRA\\");

		try {
			StringBuffer cnp = new StringBuffer();
			cnp.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>");
			cnp.append("<cnp>");
			if (diretorio.exists()) {

				for (File file : Arrays.asList(diretorio.listFiles())) {
					reader = new BufferedReader(new FileReader(file));
					System.out.println(file.getName());
					String linha = reader.readLine();
					while ((linha = reader.readLine()) != null) {
						linha = linha.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>", "");
						linha = linha.replace("<cnp>", "");
						linha = linha.replace("</cnp>", "");
						cnp.append(linha);
						cnp.append("\r\n");
					}
				}
				cnp.append("</cnp>");
				System.out.println("=============================================================");
				File arquivo =
						new File("C:\\Users\\Thasso Araújo\\Documents\\jboss-as-7.1.1.Final\\ARQUIVOS_CRA\\" + "CNP-TO-19082016.xml");
				BufferedWriter bWrite = new BufferedWriter(new FileWriter(arquivo));
				System.out.println("Escrevendo os dados no arquivo...");
				bWrite.write(cnp.toString());
				bWrite.flush();
				bWrite.close();
			} else {
				System.out.println("Diretório não encontrado...");
			}

		} catch (IOException ex) {
			System.out.println("Erro ao ler arquivo.");
		}
	}
}