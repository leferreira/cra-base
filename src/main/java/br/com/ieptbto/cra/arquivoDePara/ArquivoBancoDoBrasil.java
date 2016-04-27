package br.com.ieptbto.cra.arquivoDePara;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;

/**
 * @author Thasso Araújo
 *
 */
public class ArquivoBancoDoBrasil extends AbstractDePara {

	private List<AgenciaBancoDoBrasil> listaAgencias = new ArrayList<AgenciaBancoDoBrasil>();

	@Override
	public List<AgenciaBancoDoBrasil> processar(FileUpload file) {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));
			String linha = "";

			while ((linha = reader.readLine()) != null) {
				AgenciaBancoDoBrasil agenciaBancoDoBrasil = new AgenciaBancoDoBrasil();
				agenciaBancoDoBrasil.setNumeroContrato(linha.substring(0, 8));
				agenciaBancoDoBrasil.setAgenciaDestino(linha.substring(9, 12));

				listaAgencias.add(agenciaBancoDoBrasil);
			}
			reader.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listaAgencias;
	}
}
