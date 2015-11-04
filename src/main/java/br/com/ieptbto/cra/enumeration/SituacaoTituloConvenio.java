package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum SituacaoTituloConvenio {

	ENVIADO("Enviado"),
	AGUARDANDO("Aguardando"),
	REMOVIDO("Removido"),
	EM_PROCESSO("Em Processo"),
	FINALIZADO("Finalizado");
	
	private String situacao;
	
	private SituacaoTituloConvenio(String situacao) {
		this.situacao=situacao;
	}

	public String getSituacao() {
		return situacao;
	}
}
