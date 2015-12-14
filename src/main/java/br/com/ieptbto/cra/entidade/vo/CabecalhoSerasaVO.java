package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "registro_header")
@XmlAccessorType(XmlAccessType.NONE)
public class CabecalhoSerasaVO {

	@XmlAttribute
	private String codigoRegistro;
	
	@XmlAttribute
	private String emBranco2;
	
	@XmlAttribute
	private String dataMovimento;
	
	@XmlAttribute
	private String emBranco53;
	
	@XmlAttribute
	private String numeroRemessaArquivo;
	
	@XmlAttribute
	private String emBranco68;
	
	@XmlAttribute
	private String tipoDocumento;
	
	@XmlAttribute
	private String numeroCnpjCpfResponsavelCartorio; // ajustar a direita e preencher com 0 a esquerda
	
	@XmlAttribute
	private String identificacaoDoArquivo;
	
	@XmlAttribute
	private String codigoRemessa;
	
	@XmlAttribute
	private String numeroDDD;
	
	@XmlAttribute
	private String numeroTelefoneInstituicaoInformante;
	
	@XmlAttribute
	private String numeroRamalContato;
	
	@XmlAttribute
	private String nomeContatoInstituicaoInformante;
	
	@XmlAttribute
	private String numeroVersaoSoftware;
	
	@XmlAttribute
	private String codigoEDI;
	
	@XmlAttribute
	private String propriedadeEnvio;

	public String getCodigoRegistro() {
		return codigoRegistro;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public String getEmBranco2() {
		return emBranco2;
	}

	public void setEmBranco2(String emBranco2) {
		this.emBranco2 = emBranco2;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public String getEmBranco53() {
		return emBranco53;
	}

	public void setEmBranco53(String emBranco53) {
		this.emBranco53 = emBranco53;
	}

	public String getNumeroRemessaArquivo() {
		return numeroRemessaArquivo;
	}

	public void setNumeroRemessaArquivo(String numeroRemessaArquivo) {
		this.numeroRemessaArquivo = numeroRemessaArquivo;
	}

	public String getEmBranco68() {
		return emBranco68;
	}

	public void setEmBranco68(String emBranco68) {
		this.emBranco68 = emBranco68;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNumeroCnpjCpfResponsavelCartorio() {
		return numeroCnpjCpfResponsavelCartorio;
	}

	public void setNumeroCnpjCpfResponsavelCartorio(String numeroCnpjCpfResponsavelCartorio) {
		this.numeroCnpjCpfResponsavelCartorio = numeroCnpjCpfResponsavelCartorio;
	}

	public String getIdentificacaoDoArquivo() {
		return identificacaoDoArquivo;
	}

	public void setIdentificacaoDoArquivo(String identificacaoDoArquivo) {
		this.identificacaoDoArquivo = identificacaoDoArquivo;
	}

	public String getCodigoRemessa() {
		return codigoRemessa;
	}

	public void setCodigoRemessa(String codigoRemessa) {
		this.codigoRemessa = codigoRemessa;
	}

	public String getNumeroDDD() {
		return numeroDDD;
	}

	public void setNumeroDDD(String numeroDDD) {
		this.numeroDDD = numeroDDD;
	}

	public String getNumeroTelefoneInstituicaoInformante() {
		return numeroTelefoneInstituicaoInformante;
	}

	public void setNumeroTelefoneInstituicaoInformante(String numeroTelefoneInstituicaoInformante) {
		this.numeroTelefoneInstituicaoInformante = numeroTelefoneInstituicaoInformante;
	}

	public String getNumeroRamalContato() {
		return numeroRamalContato;
	}

	public void setNumeroRamalContato(String numeroRamalContato) {
		this.numeroRamalContato = numeroRamalContato;
	}

	public String getNomeContatoInstituicaoInformante() {
		return nomeContatoInstituicaoInformante;
	}

	public void setNomeContatoInstituicaoInformante(String nomeContatoInstituicaoInformante) {
		this.nomeContatoInstituicaoInformante = nomeContatoInstituicaoInformante;
	}

	public String getNumeroVersaoSoftware() {
		return numeroVersaoSoftware;
	}

	public void setNumeroVersaoSoftware(String numeroVersaoSoftware) {
		this.numeroVersaoSoftware = numeroVersaoSoftware;
	}

	public String getCodigoEDI() {
		return codigoEDI;
	}

	public void setCodigoEDI(String codigoEDI) {
		this.codigoEDI = codigoEDI;
	}

	public String getPropriedadeEnvio() {
		return propriedadeEnvio;
	}

	public void setPropriedadeEnvio(String propriedadeEnvio) {
		this.propriedadeEnvio = propriedadeEnvio;
	}
}
