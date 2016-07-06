package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum TipoRegistroCnp {

								CANCELAMENTO("E"),
								PROTESTO("I");

	private String codigoOperacao;

	private TipoRegistroCnp(String codigoOperacao) {
		this.codigoOperacao = codigoOperacao;
	}

	public String getCodigoOperacao() {
		return codigoOperacao;
	}
}
