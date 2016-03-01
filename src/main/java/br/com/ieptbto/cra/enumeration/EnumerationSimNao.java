package br.com.ieptbto.cra.enumeration;

public enum EnumerationSimNao {

	SIM("Sim"),
	NAO("Não");
	
	private String label;
	
	private EnumerationSimNao(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
