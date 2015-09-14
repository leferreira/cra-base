package br.com.ieptbto.cra.slip.regra;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.TituloRemessa;

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
