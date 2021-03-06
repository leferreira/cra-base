package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum SituacaoTituloRelatorio {

										GERAL("Todos"),
										SEM_CONFIRMACAO("Pendentes de Confirmação"),
										COM_CONFIRMACAO("Em Aberto (Com Confirmação)"),
										COM_RETORNO("Finalizados (Com Retorno)"),
										PAGOS("Pagos"),
										PROTESTADOS("Protestados"),
										RETIRADOS_DEVOLVIDOS("Retirados ou Devolvidos"),
										DESISTÊNCIA_DE_PROTESTO("Desistências de Protesto"),
										CANCELAMENTO_DE_PROTESTO("Cancelamentos de Protesto"),
										AUTORIZACAO_CANCELAMENTO("Autorização de Cancelamento");

	private String label;

	private SituacaoTituloRelatorio(String label) {
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return label.toUpperCase();
	}
}
