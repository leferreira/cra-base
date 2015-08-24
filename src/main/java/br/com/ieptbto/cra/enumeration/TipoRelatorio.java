package br.com.ieptbto.cra.enumeration;



/**
 * @author Thasso Araújo
 *
 */
public enum TipoRelatorio {

	GERAL("Todos", null),
	EM_ABERTO("Em Aberto", TipoOcorrencia.EM_ABERTO),
	PENDENTES("Pendentes", null),
	TITULOS_PAGOS("Pagos", TipoOcorrencia.PAGO),
	TITULOS_PROTESTADOS("Protestados",TipoOcorrencia.PROTESTADO),
	TITULOS_DEVOLVIDOS("Devolvidos", TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS),
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
