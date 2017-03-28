package br.com.ieptbto.cra.enumeration.regra;

import br.com.ieptbto.cra.enumeration.AbstractCraEnum;
import br.com.ieptbto.cra.exception.InfraException;

public enum TipoIdentificacaoRegistro implements AbstractCraEnum {

	TITULO("1", "Título"), CABECALHO("0", "Cabeçalho"), RODAPE("9", "Rodapé"),
	REGISTRO_EMPRESA("G", "Registro"), HEADER_EMPRESA("A", "Header"), TRAILLER_EMPRESA("Z", "Trailler");

	private String constante;
	private String label;

	TipoIdentificacaoRegistro(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	@Override
	public String getConstante() {
		return constante;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoIdentificacaoRegistro get(String valor) {
		TipoIdentificacaoRegistro[] values = TipoIdentificacaoRegistro.values();
		for (TipoIdentificacaoRegistro tipoRegistro : values) {
			if (valor.startsWith(tipoRegistro.getConstante())) {
				return tipoRegistro;
			}
		}
		throw new InfraException("Tipo de Registro desconhecido : " + valor);
	}
}