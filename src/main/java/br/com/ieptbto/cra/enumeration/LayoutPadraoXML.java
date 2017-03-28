package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum LayoutPadraoXML {

	CRA_NACIONAL("CRA-Nacional"),
	ENTRADA_MANUAL_LAYOUT_PERSONALIZADO("Entrada Manual e Layout Personalizado"),
	SERPRO("Serpro");

	private String label;

	private LayoutPadraoXML(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}