package br.com.ieptbto.cra.enumeration;



/**
 * @author Thasso Araújo
 *
 */
public enum TipoRelatorio {

	GERAL("Todos", null),
	SEM_CONFIRMACAO("S/ CONFIRMAÇÃO", null),
	COM_CONFIRMACAO("C/ CONFIRMAÇÃO", null),
	SEM_RETORNO("S/ RETORNO", null),
	COM_RETORNO("C/ RETORNO", null),
	TITULOS_CANCELADOS("Cancelados",TipoOcorrencia.DESISTENCIA_DO_PROTESTO);
	
	private String label;
	private TipoOcorrencia tipoOcorrencia;
	
	private TipoRelatorio(String label, TipoOcorrencia tipo) {
		this.label = label;
		this.tipoOcorrencia = tipo;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return label.toUpperCase();
	}

	public TipoOcorrencia getTipoOcorrencia() {
		return tipoOcorrencia;
	}
}
