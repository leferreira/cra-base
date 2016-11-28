package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "instituicao")
@XmlAccessorType(XmlAccessType.NONE)
public class InstituicaoVO {

	@XmlElement(name = "razao_social")
	private String razaoSocial;

	@XmlElement(name = "codigo_compensacao")
	private String codigoCompensacao;

	@XmlElement(name = "agencia_centralizadora")
	private String agenciaCentralizadora;

	@XmlElement(name = "selo_diferido")
	private String seloDiferido;

	@XmlElement(name = "taxa_cra")
	private String taxaCra;

	@XmlElement(name = "verificacao_manual")
	private String verificacaoManual;

	@XmlElement(name = "tipo_campo_51")
	private String tipoCampo51;

	@XmlElement(name = "tipo_instituicao")
	private String tipoInstituicao;

	@XmlElement(name = "versao")
	private Integer versao;

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getCodigoCompensacao() {
		return codigoCompensacao;
	}

	public void setCodigoCompensacao(String codigoCompensacao) {
		this.codigoCompensacao = codigoCompensacao;
	}

	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
	}

	public String getSeloDiferido() {
		return seloDiferido;
	}

	public void setSeloDiferido(String seloDiferido) {
		this.seloDiferido = seloDiferido;
	}

	public String getTaxaCra() {
		return taxaCra;
	}

	public void setTaxaCra(String taxaCra) {
		this.taxaCra = taxaCra;
	}

	public String getVerificacaoManual() {
		return verificacaoManual;
	}

	public void setVerificacaoManual(String verificacaoManual) {
		this.verificacaoManual = verificacaoManual;
	}

	public String getTipoCampo51() {
		return tipoCampo51;
	}

	public void setTipoCampo51(String tipoCampo51) {
		this.tipoCampo51 = tipoCampo51;
	}

	public String getTipoInstituicao() {
		return tipoInstituicao;
	}

	public void setTipoInstituicao(String tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public Integer getVersao() {
		return versao;
	}

	public void setVersao(Integer versao) {
		this.versao = versao;
	}
}
