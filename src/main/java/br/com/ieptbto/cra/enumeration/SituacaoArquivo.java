package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
public enum SituacaoArquivo implements AbstractCraEnum {
	
	AGUARDANDO("A","Aguardando"),ENVIADO("E","Enviado"),RECEBIDO("R","Recebido"); 
	 
	private String constante;
	private String label;

	SituacaoArquivo(String constante, String label) {
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
	public static SituacaoArquivo getSituacao(String constante) {
		SituacaoArquivo[] values = SituacaoArquivo.values();
		for (SituacaoArquivo situacao : values) {
			if (constante.startsWith(situacao.getLabel())) {
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
