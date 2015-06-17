package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum SituacaoTituloConvenio {

	ENVIADO("Enviado"),
	AGUARDANDO("Aguardando"),
	REMOVIDO("Removido");
	
	private String situacao;
	
	private SituacaoTituloConvenio(String situacao) {
		this.situacao=situacao;
	}

	public String getSituacao() {
		return situacao;
	}
}
