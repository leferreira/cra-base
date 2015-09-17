package br.com.ieptbto.cra.ireport;

public class SlipEnvelopeBean {

	private String nomeApresentante;
	private String numeroAgencia;
	private String municipio;
	private String uf;
	private String codeBar;
	private int quantidadeInstrumentos;
	
	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getNomeApresentante() {
		return nomeApresentante;
	}

	public String getNumeroAgencia() {
		return numeroAgencia;
	}

	public String getMunicipio() {
		if (municipio != null) {
			municipio = municipio.trim();
		}
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

	public String getCodeBar() {
		return codeBar;
	}

	public void setCodeBar(String codeBar) {
		this.codeBar = codeBar;
	}

}
