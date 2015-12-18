package br.com.ieptbto.cra.exception;

/**
 * @author Thasso Araújo
 *
 */
public enum ErroTitulo {

	/** */
	CAMPOS_INCONSISTENTES("9999", ""); 

	private String codigoErro;
	private String mensagem;

	private ErroTitulo(String codigoErro, String mensagem) { 
		this.codigoErro = codigoErro;
		this.mensagem = mensagem;
	}

	/**
	 * Retorna o codigo de erro
	 * 
	 * @return
	 */
	public String getCodigoErro() {
		return codigoErro;
	}

	/**
	 * Retorna a mensagem de erro
	 * 
	 * @return
	 */
	public String getMensagemErro() {
		return mensagem;
	}
}
