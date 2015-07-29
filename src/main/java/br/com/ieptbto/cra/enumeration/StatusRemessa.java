package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum StatusRemessa implements CraEnum {
	
	ENVIADO("E","Enviado"), AGUARDANDO("A","Aguardando"), RECEBIDO("R","Recebido");

	private String constante;
	private String label;
	
	private StatusRemessa(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}
	
	@Override
	public String getConstante() {
		return constante;
	}

	@Override
	public String getLabel() {
		return label;
	}
}