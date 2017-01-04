package br.com.ieptbto.cra.enumeration;

public enum MotivoSolicitacaoDesistenciaCancelamento {

														PAGAMENTO_AO_CREDOR("Carta de Anuência Eletrônica (Pagamento ao Credor)"),
														IRREGULARIDADE_NO_TITULO_APRESENTADO(
																"Irregularidade nas informações do Título Apresentado");

	private String label;

	private MotivoSolicitacaoDesistenciaCancelamento(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}