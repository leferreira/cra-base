package br.com.ieptbto.cra.enumeration;

public enum TipoVisualizacaoArquivos {

	ARQUIVOS_CARTORIOS("Arquivos de Cartórios"),
	ARQUIVOS_BANCOS_CONVENIOS("Arquivos de Bancos/Convênios");

	private String label;

	private TipoVisualizacaoArquivos(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
