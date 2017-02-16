package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
public enum StatusDownload implements AbstractCraEnum {
	
	AGUARDANDO("A","Aguardando"),ENVIADO("E","Enviado"),RECEBIDO("R","Recebido"); 
	 
	private String constante;
	private String label;

	StatusDownload(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getConstante() {
		return constante;
	}

	/**
	 * retorna o status do arquivo dependendo da constante
	 * 
	 * @param valor
	 * @return status arquivo
	 */
	public static StatusDownload getStatus(String constante) {
		StatusDownload[] values = StatusDownload.values();
		for (StatusDownload situacao : values) {
			if (constante.equalsIgnoreCase(situacao.getLabel())) {
				return situacao;
			}
		}
		throw new InfraException("Situacao do Arquivo desconhecido : " + constante);
	} 
	
	@Override
	public String toString() {
		return label.toUpperCase();
	}
}
