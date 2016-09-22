package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum LayoutPadraoXML {

	ENTRADA_MANUAL("Entrada Manual (Convênios)"),
	CRA_NACIONAL("CRA-Nacional"),
	SERPRO("Serpro"),
	LAYOUT_PERSONALIZADO_CONVENIOS("Personalizado (Convênios)");

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
