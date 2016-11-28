package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum TipoSolicitacaoDesistenciaCancelamento {

													SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE("DP",
															"Desistência de Protesto por Irregularidade"),
													SOLICITACAO_DESISTENCIA_PROTESTO("DP", "Desistência de Protesto por Pagamento"),
													SOLICITACAO_AUTORIZACAO_CANCELAMENTO("AC",
															"Carta de Anuência Eletrônica (Pagamento ao Credor)"),
													SOLICITACAO_CANCELAMENTO_PROTESTO("CP", "Cancelamento de Protesto por Irregularidade");

	private String label;
	private String descricao;

	private TipoSolicitacaoDesistenciaCancelamento(String label, String descricao) {
		this.label = label;
		this.descricao = descricao;
	}

	public String getLabel() {
		return label;
	}

	public String getDescricao() {
		return descricao;
	}
}