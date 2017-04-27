package br.com.ieptbto.cra.slip.regra;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import org.apache.log4j.Logger;

public abstract class RegraAgenciaBanco {

	protected static final Logger logger = Logger.getLogger(RegraAgenciaBanco.class);
	
	private String agencia;
	
	protected abstract String aplicarRegraEspecifica(TituloRemessa titulo);

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}
}
