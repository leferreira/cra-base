package br.com.ieptbto.cra.enumeration;



/**
 * @author Thasso Araújo
 *
 */
public enum TipoRelatorio {

	GERAL("Todos");
//	SEM_CONFIRMACAO("Pendentes de Confirmação"),
//	COM_CONFIRMACAO("Em Aberto (Com Confirmação)"),
//	SEM_RETORNO("Pendentes de Retorno"),
//	COM_RETORNO("Finalizados (Com Retorno)"),
//	PAGOS("Pagos"),
//	PROTESTADOS("Protestados"),
//	RETIRADOS_DEVOLVIDOS("Retirados ou Devolvidos"),
//	DESISTÊNCIA_DE_PROTESTO("Desistências de Protesto");
//	CANCELAMENTO_DE_PROTESTO("Cancelamentos de Protesto");
	
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
		return label;
	}
}
