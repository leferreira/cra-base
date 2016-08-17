package br.com.ieptbto.cra.regra.titulo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

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

	public TipoArquivoEnum getTipoArquivo(Arquivo arquivo) {
		return arquivo.getTipoArquivo().getTipoArquivo();
	}
}