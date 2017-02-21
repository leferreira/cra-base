package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum TipoDeposito {

	NAO_INFORMADO("Escolha"), 
	DEPOSITO_CARTORIO_PARA_BANCO("Cartório para Bancos/Convênio"), 
	DEPOSITO_CARTORIO_PARA_IEPTB("Cartório para o IEPTB"), 
	DEPOSITO_TAXA_CRA("Taxa CRA"), 
	DEPOSITOS_ERRADOS("Deposito Errado"), 
	OUTROS_DEPOSITOS("Outros depositos");
	
	private String label;
	
	private TipoDeposito(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
