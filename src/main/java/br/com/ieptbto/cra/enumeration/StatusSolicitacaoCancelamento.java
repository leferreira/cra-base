package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum StatusSolicitacaoCancelamento {

	SOLICITACAO_AUTORIZACAO_CANCELAMENTO("AC"),
	SOLICITACAO_CANCELAMENTO_PROTESTO("CP"),
	SOLICITACAO_ENVIADA("");

	private String label;

	private StatusSolicitacaoCancelamento(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}