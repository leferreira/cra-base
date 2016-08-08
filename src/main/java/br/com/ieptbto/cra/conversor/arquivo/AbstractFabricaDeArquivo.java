package br.com.ieptbto.cra.conversor.arquivo;

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
	protected File arquivoFisico;
	protected Arquivo arquivo;
	protected List<Exception> erros;

	public abstract Arquivo converter();

	public File getArquivoFisico() {
		return arquivoFisico;
	}

	public void setArquivoFisico(File arquivoFisico) {
		this.arquivoFisico = arquivoFisico;
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