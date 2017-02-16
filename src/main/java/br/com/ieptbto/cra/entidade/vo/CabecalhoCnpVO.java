package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "cabecalho")
@XmlAccessorType(XmlAccessType.FIELD)
public class CabecalhoCnpVO extends AbstractArquivoVO {

	/***/
	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private String codigoRegistro;

	@XmlAttribute(required = true)
	private String emBranco2;

	@XmlAttribute(required = true)
	private String dataMovimento;

	@XmlAttribute(required = true)
	private String emBranco53;

	@XmlAttribute(required = true)
	private String numeroRemessaArquivo;

	@XmlAttribute(required = true)
	private String emBranco68;

	@XmlAttribute(required = true)
	private String tipoDocumento;

	@XmlAttribute(required = true)
	private String numeroCnpjCpfResponsavelCartorio;

	@XmlAttribute(required = true)
	private String identificacaoDoArquivo;

	@XmlAttribute(required = true)
	private String codigoRemessa;

	@XmlAttribute(required = true)
	private String numeroDDD;

	@XmlAttribute(required = true)
	private String numeroTelefoneInstituicaoInformante;

	@XmlAttribute(required = true)
	private String numeroRamalContato;

	@XmlAttribute(required = true)
	private String nomeContatoInstituicaoInformante;

	@XmlAttribute(required = true)
	private String numeroVersaoSoftware;

	@XmlAttribute(required = true)
	private String codigoEDI;

	@XmlAttribute(required = true)
	private String periodicidadeEnvio;

	@XmlAttribute(required = true)
	private String endereco;

	@XmlAttribute(required = true)
	private String emBranco262;

	@XmlAttribute(required = true)
	private String codigoErro3Posicoes;

	@XmlAttribute(required = true)
	private String sequenciaRegistro;

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

	public String getPeriodicidadeEnvio() {
		return periodicidadeEnvio;
	}

	public void setPeriodicidadeEnvio(String periodicidadeEnvio) {
		this.periodicidadeEnvio = periodicidadeEnvio;
	}

	@Override
	public String getIdentificacaoRegistro() {
		return TipoIdentificacaoRegistro.CABECALHO.getConstante();
	}

	public String getEndereco() {
		return endereco;
	}

	public String getEmBranco262() {
		return emBranco262;
	}

	public String getCodigoErro3Posicoes() {
		return codigoErro3Posicoes;
	}

	public String getSequenciaRegistro() {
		return sequenciaRegistro;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setEmBranco262(String emBranco262) {
		this.emBranco262 = emBranco262;
	}

	public void setCodigoErro3Posicoes(String codigoErro3Posicoes) {
		this.codigoErro3Posicoes = codigoErro3Posicoes;
	}

	public void setSequenciaRegistro(String sequenciaRegistro) {
		this.sequenciaRegistro = sequenciaRegistro;
	}
}
