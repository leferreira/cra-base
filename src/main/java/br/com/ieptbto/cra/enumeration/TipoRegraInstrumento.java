package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum TipoRegraInstrumento {

	POSICAO_5_A_8_AGENCIA_CODIGO_CEDENTE(4,7),
	POSICAO_7_A_10_AGENCIA_CODIGO_CEDENTE(6,9);

	private Integer posicaoInicialCampo;
	private Integer posicaoFinalCampo;
	
	private TipoRegraInstrumento(Integer posicaoInicialCampo, Integer posicaoFinalCampo) {
		this.posicaoInicialCampo = posicaoInicialCampo;
		this.posicaoFinalCampo = posicaoFinalCampo;
	}
	
	public Integer getPosicaoInicialCampo() {
		return posicaoInicialCampo;
	}

	public Integer getPosicaoFinalCampo() {
		return posicaoFinalCampo;
	}
}
