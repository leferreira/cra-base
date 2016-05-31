package br.com.ieptbto.cra.webservice.VO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cartorio")
@XmlAccessorType(XmlAccessType.FIELD)
public class CnpCartorio {

	@XmlElement(name = "codigo", required = true)
	private String codigo;

	@XmlElement(name = "nome", required = true)
	private String nomeFantasia;

	@XmlElement(name = "razao", required = true)
	private String razaoSocial;

	@XmlElement(name = "tabeliao", required = true)
	private String tabeliao;

	@XmlElement(name = "responsavel", required = true)
	private String responsavel;

	@XmlElement(name = "cnpj", required = true)
	private String cnpj;

	@XmlElement(name = "endereco", required = true)
	private String endereco;

	@XmlElement(name = "bairro", required = true)
	private String bairro;

	@XmlElement(name = "uf", required = true)
	private String uf;

	@XmlElement(name = "telefone", required = true)
	private String telefone;

	@XmlElement(name = "participante", required = true)
	private String participante;

	@XmlElement(name = "ultimoEnvio", required = true)
	private String ultimoEnvio;

	@XmlElement(name = "municipio", required = true)
	private CnpMunicipioCartorio municipio;

	public String getCodigo() {
		return codigo;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public String getTabeliao() {
		return tabeliao;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public String getCnpj() {
		return cnpj;
	}

	public String getEndereco() {
		return endereco;
	}

	public String getBairro() {
		return bairro;
	}

	public String getUf() {
		return uf;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getParticipante() {
		return participante;
	}

	public String getUltimoEnvio() {
		return ultimoEnvio;
	}

	public CnpMunicipioCartorio getMunicipio() {
		return municipio;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public void setTabeliao(String tabeliao) {
		this.tabeliao = tabeliao;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public void setParticipante(String participante) {
		this.participante = participante;
	}

	public void setUltimoEnvio(String ultimoEnvio) {
		this.ultimoEnvio = ultimoEnvio;
	}

	public void setMunicipio(CnpMunicipioCartorio municipio) {
		this.municipio = municipio;
	}
}
