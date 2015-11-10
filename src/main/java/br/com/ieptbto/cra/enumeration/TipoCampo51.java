package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum TipoCampo51 {

	ALFANUMERICO("Alfanumérico"),
	LINK("Link Externo"),
	DOCUMENTOS_ZIPADOS("Documentos Zipados");
	
	private String label;
	
	private TipoCampo51(String label) {
		this.label=label;
	}

	public String getLabel() {
		return label;
	}
}
