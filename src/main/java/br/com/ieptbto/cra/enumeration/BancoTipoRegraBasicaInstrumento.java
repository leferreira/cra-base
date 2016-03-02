package br.com.ieptbto.cra.enumeration;


/**
 * @author Thasso Araújo
 *
 */
public enum BancoTipoRegraBasicaInstrumento {

	ITAU("341", TipoRegraInstrumento.POSICAO_5_A_8_AGENCIA_CODIGO_CEDENTE),
	BRADESCO("237", TipoRegraInstrumento.POSICAO_7_A_10_AGENCIA_CODIGO_CEDENTE),
	SANTANDER("033", TipoRegraInstrumento.POSICAO_7_A_10_AGENCIA_CODIGO_CEDENTE),
	HSBC("399", TipoRegraInstrumento.POSICAO_5_A_8_AGENCIA_CODIGO_CEDENTE),
	MERCANTIL("389", TipoRegraInstrumento.POSICAO_7_A_10_AGENCIA_CODIGO_CEDENTE),
	BRB("070", TipoRegraInstrumento.POSICAO_5_A_8_AGENCIA_CODIGO_CEDENTE),
	BIC("", TipoRegraInstrumento.POSICAO_5_A_8_AGENCIA_CODIGO_CEDENTE),
	BANCO_DO_BRASIL("001", null),
	SAFRA("422", TipoRegraInstrumento.POSICAO_5_A_8_AGENCIA_CODIGO_CEDENTE);
	
	private String codigoPortador;
	private TipoRegraInstrumento tipoRegraBasicaInstrumento;
	
	private BancoTipoRegraBasicaInstrumento(String codigoPortador, TipoRegraInstrumento regraBasica) {
		this.codigoPortador = codigoPortador;
		this.tipoRegraBasicaInstrumento = regraBasica;
	}
	
	public String getCodigoPortador() {
		return codigoPortador;
	}

	public TipoRegraInstrumento getTipoRegraBasicaInstrumento() {
		return tipoRegraBasicaInstrumento;
	}
	
	/**
	 * retorna o o banco e a regra basica aplicada para cada um
	 * 
	 * @param codigoPortado
	 * @return bancoRegra
	 */
	public static BancoTipoRegraBasicaInstrumento getBancoRegraBasicaInstrumento(String codigoPortador) {
		BancoTipoRegraBasicaInstrumento[] values = BancoTipoRegraBasicaInstrumento.values();
		for (BancoTipoRegraBasicaInstrumento bancoRegra : values) {
			if (codigoPortador.equals(bancoRegra.getCodigoPortador())) {
				return bancoRegra;
			}
		}
		return null;
	}
}
