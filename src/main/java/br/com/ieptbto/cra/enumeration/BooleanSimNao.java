package br.com.ieptbto.cra.enumeration;

public enum BooleanSimNao {

	SIM("Sim"),
	NAO("Não");
	
	private String label;
	
	private BooleanSimNao(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
