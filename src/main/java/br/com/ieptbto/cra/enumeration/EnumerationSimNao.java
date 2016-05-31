package br.com.ieptbto.cra.enumeration;

public enum EnumerationSimNao {

								SIM("Sim", true),
								NAO("Não", false);

	private String label;
	private Boolean status;

	private EnumerationSimNao(String label, Boolean status) {
		this.label = label;
		this.status = status;
	}

	public static EnumerationSimNao getSimNao(Boolean bool) {
		EnumerationSimNao[] values = EnumerationSimNao.values();
		for (EnumerationSimNao enumerationSimNao : values) {
			if (bool.equals(enumerationSimNao.getStatus())) {
				return enumerationSimNao;
			}
		}
		return EnumerationSimNao.NAO;
	}

	public String getLabel() {
		return label;
	}

	public Boolean getStatus() {
		return status;
	}
}
