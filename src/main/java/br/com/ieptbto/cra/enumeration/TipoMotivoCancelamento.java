package br.com.ieptbto.cra.enumeration;

public enum TipoMotivoCancelamento {

									CANCELAMENTO_POR_IRREGULARIDADE("Cancelado por Irregularidade"),
									CANCELAMENTO_POR_PAGAMENTO("Cancelado por Pagamento ao Credor");

	private String label;

	private TipoMotivoCancelamento(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
