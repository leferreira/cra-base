package br.com.ieptbto.cra.enumeration;

public enum BooleanSimNao {

	TRUE("Sim", true),
	FALSE("Não", false);

	private String label;
	private boolean bool;

	private BooleanSimNao(String label, boolean bool) {
		this.label = label;
		this.bool = bool;
	}

	public String getLabel() {
		return label;
	}

	public boolean getBool() {
		return bool;
	}
}