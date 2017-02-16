package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = "cartorio")
@XmlAccessorType(XmlAccessType.FIELD)
public class CnpCartorioVO {

	@XmlElement(name = "codigo", defaultValue = StringUtils.EMPTY)
	private String codigo;

	@XmlElement(name = "nome", defaultValue = StringUtils.EMPTY)
	private String nomeFantasia;

	@XmlElement(name = "razao", defaultValue = StringUtils.EMPTY)
	private String razaoSocial;

	@XmlElement(name = "tabeliao", defaultValue = StringUtils.EMPTY)
	private String tabeliao;

	@XmlElement(name = "responsavel", defaultValue = StringUtils.EMPTY)
	private String responsavel;

	@XmlElement(name = "cnpj", defaultValue = StringUtils.EMPTY)
	private String cnpj;

	@XmlElement(name = "endereco", defaultValue = StringUtils.EMPTY)
	private String endereco;

	@XmlElement(name = "bairro", defaultValue = StringUtils.EMPTY)
	private String bairro;

	@XmlElement(name = "uf", defaultValue = StringUtils.EMPTY)
	private String uf;

	@XmlElement(name = "telefone", defaultValue = StringUtils.EMPTY)
	private String telefone;

	@XmlElement(name = "participante", defaultValue = StringUtils.EMPTY)
	private String participante;

	@XmlElement(name = "ultimoEnvio", defaultValue = StringUtils.EMPTY)
	private String ultimoEnvio;

	@XmlElement(name = "municipio", defaultValue = StringUtils.EMPTY)
	private CnpMunicipioCartorioVO municipio;

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

	public CnpMunicipioCartorioVO getMunicipio() {
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

	public void setMunicipio(CnpMunicipioCartorioVO municipio) {
		this.municipio = municipio;
	}
}
