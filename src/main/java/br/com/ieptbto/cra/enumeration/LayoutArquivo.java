package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
public enum LayoutArquivo implements AbstractCraEnum {
	TXT("0", "Arquivo TXT", ".txt"), XML("<", "Arquivo XML", ".xml"), CSV("", "Arquivo CSV", ".csv");

	private String constante;
	private String label;
	private String extensao;
	
	private LayoutArquivo(String constante, String label, String extensao) {
		this.constante = constante;
		this.label = label;
		this.extensao = extensao;
	}

	@Override
	public String getConstante() {
		return this.constante;
	}

	@Override
	public String getLabel() {
		return this.label;
	}
	
	public String getExtensao() {
		return extensao;
	}

	/**
	 * retorna o layout do arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static LayoutArquivo get(String valor) {
		LayoutArquivo[] values = LayoutArquivo.values();
		for (LayoutArquivo layout : values) {
			if (valor.startsWith(layout.getConstante())) {
				return layout;
			}
		}
		throw new InfraException("O layout do arquivo é desconhecido : " + valor);
	}

}
