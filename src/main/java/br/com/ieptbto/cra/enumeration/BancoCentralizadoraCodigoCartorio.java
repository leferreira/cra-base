package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum BancoCentralizadoraCodigoCartorio {

												BRADESCO("237", "102397", 1),
												BANCO_DO_BRASIL("001", null, 1),
												ITAU("341", "", 0);

	private String codigoPortador;
	private String agenciaCentralizadora;
	private Integer codigoCartorio;

	private BancoCentralizadoraCodigoCartorio(String codigoPortador, String agenciaCentralizadora, Integer codigoCartorio) {
		this.codigoPortador = codigoPortador;
		this.agenciaCentralizadora = agenciaCentralizadora;
		this.codigoCartorio = codigoCartorio;
	}

	public String getCodigoPortador() {
		return codigoPortador;
	}

	public String getAgenciaCentralizadora(String codigoMunicipio) {
		if (this.equals(ITAU)) {
			return ItauCodigoCartorio.getAgenciaCentralizadora(codigoMunicipio);
		}
		return agenciaCentralizadora;
	}

	public Integer getCodigoCartorio() {
		return codigoCartorio;
	}

	public Integer getCodigoCartorio(String codigoMunicipio) {
		this.codigoCartorio = 1;
		if (this.equals(ITAU)) {
			return ItauCodigoCartorio.getCodigoCartorio(codigoMunicipio);
		}
		return codigoCartorio;
	}

	public static BancoCentralizadoraCodigoCartorio getBanco(String numeroCodigoPortador) {
		BancoCentralizadoraCodigoCartorio[] values = BancoCentralizadoraCodigoCartorio.values();
		for (BancoCentralizadoraCodigoCartorio bancoAgencia : values) {
			if (numeroCodigoPortador.startsWith(bancoAgencia.getCodigoPortador())) {
				return bancoAgencia;
			}
		}
		return null;
	}
}
