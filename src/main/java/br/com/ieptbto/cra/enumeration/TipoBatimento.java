package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum TipoBatimento {

							BATIMENTO_REALIZADO_PELA_CRA("Realizado pela CRA"),
							BATIMENTO_REALIZADO_PELA_INSTITUICAO("Realizado pela Instituição"),
							LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO("Liberação sem Identificação de Depósito");

	private String label;

	private TipoBatimento(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
