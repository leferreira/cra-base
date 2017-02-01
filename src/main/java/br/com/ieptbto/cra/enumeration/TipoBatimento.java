package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum TipoBatimento {

							BATIMENTO_REALIZADO_PELA_CRA("BATIMENTO_REALIZADO_PELA_CRA","Realizado pela CRA"),
							BATIMENTO_REALIZADO_PELA_INSTITUICAO("BATIMENTO_REALIZADO_PELA_INSTITUICAO","Realizado pela Instituição"),
							LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO("LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO","Liberação sem Identificação de Depósito");

	private String label;
	private String constante;
	
	private TipoBatimento(String constante, String label) {
		this.label = label;
		this.constante = constante;
	}

	public String getLabel() {
		return label;
	}
	
	public String getConstante() {
		return constante;
	}
	
	/**
	 * retorna o tipo de batimento
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoBatimento getTipoBatimento(String constante) {
		TipoBatimento[] values = TipoBatimento.values();
		for (TipoBatimento tipo : values) {
			if (constante.startsWith(tipo.getConstante())) {
				return tipo;
			}
		}
		return null;
	}
}
