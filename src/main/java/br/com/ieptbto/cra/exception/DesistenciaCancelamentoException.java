package br.com.ieptbto.cra.exception;

import br.com.ieptbto.cra.error.CodigoErro;

/**
 * @author Thasso Aráujo
 *
 */
public class DesistenciaCancelamentoException extends RuntimeException {

	/** **/
	private static final long serialVersionUID = 1L;
	private String municipio;
	private CodigoErro codigoErro;
	private String descricao;

	/**
	 * @param message
	 * @param municipio
	 * @param codigoErro
	 */
	public DesistenciaCancelamentoException(String message, String municipio, CodigoErro erro) {
		super(message);
		this.municipio = municipio;
		this.codigoErro = erro;
		this.descricao = message;
	}

	/**
	 * @return the municipio
	 */
	public String getMunicipio() {
		return municipio;
	}

	/**
	 * @return the codigoErro
	 */
	public CodigoErro getCodigoErro() {
		return codigoErro;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return "COMARCA " + getMunicipio() + " REJEITADA. " + getMessage() + "";
	}
}
