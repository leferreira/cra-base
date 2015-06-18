package br.com.ieptbto.cra.enumeration;

public enum TipoDocumento implements CraEnum {

	CNPJ("001","CNPJ"),
	CPF("002","CPF");
	
	private String constante;
	private String label;

	private TipoDocumento(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	@Override
	public String getConstante() {
		return this.constante;
	}

	@Override
	public String getLabel() {
		return this.label;
	}
}
