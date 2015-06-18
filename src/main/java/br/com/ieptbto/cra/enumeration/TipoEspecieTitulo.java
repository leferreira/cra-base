package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

public enum TipoEspecieTitulo implements CraEnum {
	
	CDA("CDA","Certidão Dívida Ativa"),
	CH("CH","Cheque"),
	DMI("DMI","Duplicata Venda Mercantil por Indicação"),
	DSI("DSI","Duplicata de Prestação de Serviços"),
	NP("NP","Nota Promissória"),
	SJ("SJ","Sentença Judicial");
	
	private String constante;
	private String label;

	private TipoEspecieTitulo(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoEspecieTitulo getTipoEspecieTitulo(String valor) {
		TipoEspecieTitulo[] values = TipoEspecieTitulo.values();
		for (TipoEspecieTitulo especieTitulo : values) {
			if (valor.startsWith(especieTitulo.getConstante())) {
				return especieTitulo;
			}
		}
		throw new InfraException("Tipo Espécie de título não encontrado. - " + valor);
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
