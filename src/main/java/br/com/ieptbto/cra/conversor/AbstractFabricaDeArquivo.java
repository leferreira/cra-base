package br.com.ieptbto.cra.conversor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Arquivo;

/**
 * 
 * @author Lefer
 *
 */
public abstract class AbstractFabricaDeArquivo {

	protected static final Logger logger = Logger.getLogger(AbstractFabricaDeArquivo.class);
	protected File file;
	protected Arquivo arquivo;
	protected List<Exception> erros;

	public abstract Arquivo converter(File arquivoFisico, Arquivo arquivo, List<Exception> erros);

	public File getFile() {
		return file;
	}

	public void setFile(File arquivoFisico) {
		this.file = arquivoFisico;
	}

	public Arquivo getArquivo() {
		if (arquivo == null) {
			arquivo = new Arquivo();
		}
		return arquivo;
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}