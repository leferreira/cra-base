package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum BancoAgenciaCentralizadoraCodigoCartorio {

														BRADESCO("237", "102397", 1),
														BANCO_DO_BRASIL("001", null, 1);

	private String codigoPortador;
	private String agenciaCentralizadora;
	private Integer codigoCartorio;

	private BancoAgenciaCentralizadoraCodigoCartorio(String codigoPortador, String agenciaCentralizadora, Integer codigoCartorio) {
		this.codigoPortador = codigoPortador;
		this.agenciaCentralizadora = agenciaCentralizadora;
		this.codigoCartorio = codigoCartorio;
	}

	public String getCodigoPortador() {
		return codigoPortador;
	}

	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	public Integer getCodigoCartorio() {
		return codigoCartorio;
	}

	public static BancoAgenciaCentralizadoraCodigoCartorio getBancoAgenciaCodigoCartorio(String numeroCodigoPortador) {
		BancoAgenciaCentralizadoraCodigoCartorio[] values = BancoAgenciaCentralizadoraCodigoCartorio.values();
		for (BancoAgenciaCentralizadoraCodigoCartorio bancoAgencia : values) {
			if (numeroCodigoPortador.startsWith(bancoAgencia.getCodigoPortador())) {
				return bancoAgencia;
			}
		}
		return null;
	}
}
