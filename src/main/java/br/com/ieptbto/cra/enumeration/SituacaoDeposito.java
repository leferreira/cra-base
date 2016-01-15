package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum SituacaoDeposito {
	
	NAO_IDENTIFICADO("Não Identificado"), IDENTIFICADO("Identificado");
	
	private String label;
	
	private SituacaoDeposito(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label.toUpperCase();
	}
}
