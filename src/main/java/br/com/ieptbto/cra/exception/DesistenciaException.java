package br.com.ieptbto.cra.exception;

/**
 * @author Thasso Aráujo
 *
 */
public class DesistenciaException extends RuntimeException {

	/** **/
	private static final long serialVersionUID = 1L;
	private final String municipio;
	private final String codigoErro;
	
	/**
	 * @param message
	 * @param municipio
	 * @param codigoErro
	 */
	public DesistenciaException(String message, String municipio, String codigoErro) {
		super(message);
		this.municipio=municipio;
		this.codigoErro=codigoErro;
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
	public String getCodigoErro() {
		return codigoErro;
	}	
	
	@Override
	public String toString() {
		return "Comarca Rejeitada: Município=["+getMunicipio()+"] Código=[ "+getCodigoErro()+" ] Descrição=[ "+getMessage()+" ]";
	}
}
