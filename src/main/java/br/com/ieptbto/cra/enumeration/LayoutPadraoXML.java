package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum LayoutPadraoXML {

	CRA_NACIONAL("CRA-Nacional"),
	SERPRO("Serpro");
	
	private String label;
	
	private LayoutPadraoXML(String label) {
		this.label=label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
