package br.com.ieptbto.cra.regra.titulo;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
public abstract class RegraTitulo {

	protected static final Logger logger = Logger.getLogger(RegraTitulo.class);
	protected static final int ZERO = 0;
	protected Usuario usuario;
	protected Arquivo arquivo;
	protected List<Exception> erros;

	protected abstract void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros);

	protected abstract void executar();

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return this.erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}
}