package br.com.ieptbto.cra.enumeration;


public enum AgenciaCentralizadora {

	BRADESCO_AG_102397("237", "102397");

	private String codigoPortador;
	private String agenciaCentralizadora;

	private AgenciaCentralizadora(String codigoPortador, String agenciaCentralizadora) {
		this.codigoPortador = codigoPortador;
		this.agenciaCentralizadora = agenciaCentralizadora;
	}
	
	public String getCodigoPortador() {
		return codigoPortador;
	}

	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}
	
	public static AgenciaCentralizadora getAgenciaCentralizadoraEnum(String numeroCodigoPortador) {
		AgenciaCentralizadora[] values = AgenciaCentralizadora.values();
		for (AgenciaCentralizadora agenciaCentralizadora : values) {
			if (numeroCodigoPortador.startsWith(agenciaCentralizadora.getCodigoPortador())) {
				return agenciaCentralizadora;
			}
		}
		return null;
	}

}
