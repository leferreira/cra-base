package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
public enum TipoRegistroDP implements CraEnum {
	HEADER_APRESENTANTE("0", "Header do Apresentante"), //
	HEADER_CARTORIO("1", "Header do Cartório"), //
	REGISTRO_PEDIDO_DESISTENCIA("2", "Registros dos Pedidos de Desistência de Protesto (Transação)"), //
	TRAILLER_CARTORIO("8", "Trailler do Cartório"), //
	TRAILLER_APRESENTANTE("9", "Trailler do Apresentatne (final do arquivo)");

	private String label;
	private String constante;

	private TipoRegistroDP(String constante, String label) {
		this.label = label;
		this.constante = constante;
	}

	@Override
	public String getConstante() {
		return this.constante;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	public static TipoRegistroDP get(String valor) {
		TipoRegistroDP[] values = TipoRegistroDP.values();
		for (TipoRegistroDP tipoRegistroDP : values) {
			if (tipoRegistroDP.getConstante().equals(valor)) {
				return tipoRegistroDP;
			}
		}
		throw new InfraException("Tipo de registro DP não existe : " + valor);
	}

}
