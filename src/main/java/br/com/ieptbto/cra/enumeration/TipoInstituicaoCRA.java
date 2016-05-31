package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

public enum TipoInstituicaoCRA implements AbstractCraEnum {

	CRA("1", "Central de Remessa de Arquivos"), 
	CARTORIO("2", "Cartório de Protesto"), 
	INSTITUICAO_FINANCEIRA("3", "Instituicão Financeira"), 
	CONVENIO("4", "Convênio");

	private String constante;
	private String label;

	private TipoInstituicaoCRA(String constante, String label) {
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

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoInstituicaoCRA get(String valor) {
		TipoInstituicaoCRA[] values = TipoInstituicaoCRA.values();
		for (TipoInstituicaoCRA tipoInstituicao : values) {
			if (tipoInstituicao.getConstante().equals(valor)) {
				return tipoInstituicao;
			}
		}
		throw new InfraException("Tipo de instituição desconhecido : " + valor);
	}

}
