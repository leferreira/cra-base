package br.com.ieptbto.cra.ireport;

public class SlipEnvelopeBean {

	private String nomeApresentante;
	private String numeroAgencia;
	private String municipio;
	private int quantidadeInstrumentos;

	public String getNomeApresentante() {
		return nomeApresentante;
	}

	public String getNumeroAgencia() {
		return numeroAgencia;
	}

	public String getMunicipio() {
		return municipio;
	}

	public int getQuantidadeInstrumentos() {
		return quantidadeInstrumentos;
	}

	public void setNomeApresentante(String nomeApresentante) {
		this.nomeApresentante = nomeApresentante;
	}

	public void setNumeroAgencia(String numeroAgencia) {
		this.numeroAgencia = numeroAgencia;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public void setQuantidadeInstrumentos(int quantidadeInstrumentos) {
		this.quantidadeInstrumentos = quantidadeInstrumentos;
	}

}
