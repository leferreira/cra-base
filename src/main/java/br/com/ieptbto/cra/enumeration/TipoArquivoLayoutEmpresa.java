package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
public enum TipoArquivoLayoutEmpresa implements CraEnum {
	TXT("TXT", "Arquivo texto"), CSV("CSV", "Arquivo CSV");

	private String constante;
	private String label;

	private TipoArquivoLayoutEmpresa(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoArquivoLayoutEmpresa get(String valor) {
		TipoArquivoLayoutEmpresa[] values = TipoArquivoLayoutEmpresa.values();
		for (TipoArquivoLayoutEmpresa tipo : values) {
			if (valor.equals(tipo.getConstante())) {
				return tipo;
			}
		}
		throw new InfraException("O Tipo de arquivo layout " + valor + " não foi encontrado");
	}

	@Override
	public String getConstante() {
		return this.constante;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

}
