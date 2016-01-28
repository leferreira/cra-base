package br.com.ieptbto.cra.enumeration;



/**
 * @author Thasso Araújo
 *
 */
public enum TipoRelatorio {

	SINTETICO("Sintético"),
	ANALITICO("Analítico"),
	DETALHADO("Detalhado");
	
	private String label;
	
	private TipoRelatorio(String label) {
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		if (label != null) {
			return label.toUpperCase();
		}
		return label;
	}
	
	@Override
	public String toString() {
		return label.toUpperCase();
	}
}
