package br.com.ieptbto.cra.regra.entrada;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
public abstract class RegraEntrada {

	protected static final Logger logger = Logger.getLogger(RegraEntrada.class);
	protected File file;
	protected Usuario usuario;
	protected Arquivo arquivo;
	protected List<Exception> erros;

	protected abstract void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros);

	protected abstract void executar();

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return this.erros;
	}

	public void addErro(Exception erro) {
		getErros().add(erro);
	}
}
