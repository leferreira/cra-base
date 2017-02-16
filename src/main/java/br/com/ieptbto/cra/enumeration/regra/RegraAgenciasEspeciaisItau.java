package br.com.ieptbto.cra.enumeration.regra;

/**
 * @author Thasso Araújo
 *
 */
public enum RegraAgenciasEspeciaisItau {

	AGENCIA_0933("0933"),//AGENCIA_0933("0933")
	AGENCIA_0910("0910"),
	AGENCIA_0911("0911"),
	AGENCIA_0912("0912"),
	AGENCIA_1248("1248"),
	AGENCIA_2938("2938");
	
	private String agencia;
	
	private RegraAgenciasEspeciaisItau(String agencia) {
		this.setAgencia(agencia);
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}
}
