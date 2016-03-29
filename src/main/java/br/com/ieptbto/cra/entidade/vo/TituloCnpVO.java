package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.ieptbto.cra.enumeration.TipoRegistro;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "titulo")
@XmlAccessorType(XmlAccessType.FIELD)
public class TituloCnpVO extends AbstractArquivoVO {

	/***/
	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private String codigoRegistro;

	@XmlAttribute(required = true)
	private String tipoInformacao;

	@XmlAttribute(required = true)
	private String codigoOperacao;

	@XmlAttribute(required = true)
	private String codigoUnidadeFederativa;

	@XmlAttribute(required = true)
	private String codigoPracaEmbratel;

	@XmlAttribute(required = true)
	private String emBranco10;

	@XmlAttribute(required = true)
	private String nomeCredor;

	@XmlAttribute(required = true)
	private String numeroDocumentoCredor;

	@XmlAttribute(required = true)
	private String complementoDocumentoCredor;

	@XmlAttribute(required = true)
	private String digitoControleDocumentoCredor;

	@XmlAttribute(required = true)
	private String enderecoCredor;

	@XmlAttribute(required = true)
	private String cepCredor;

	@XmlAttribute(required = true)
	private String cidadeCredor;

	@XmlAttribute(required = true)
	private String ufCredor;

	@XmlAttribute(required = true)
	private String municipioEnderecoCredor;

	@XmlAttribute(required = true)
	private String tipoPessoaCredor;

	@XmlAttribute(required = true)
	private String tipoDocumentoCredor;

	@XmlAttribute(required = true)
	private String emBranco226;

	@XmlAttribute(required = true)
	private String valorProtesto;

	@XmlAttribute(required = true)
	private String dataProtesto;

	@XmlAttribute(required = true)
	private String tipoPessoaDevedor;

	@XmlAttribute(required = true)
	private String tipoDocumentoDevedor;

	@XmlAttribute(required = true)
	private String numeroCoResponsavel;

	@XmlAttribute(required = true)
	private String emBranco273;

	@XmlAttribute(required = true)
	private String nomeDevedor;

	@XmlAttribute(required = true)
	private String emBranco343;

	@XmlAttribute(required = true)
	private String numeroDocumentoDevedor;

	@XmlAttribute(required = true)
	private String complementoDocumentoDevedor;

	@XmlAttribute(required = true)
	private String digitoControleDocumentoDevedor;

	@XmlAttribute(required = true)
	private String emBranco361;

	@XmlAttribute(required = true)
	private String enderecoDevedor;

	@XmlAttribute(required = true)
	private String cepDevedor;

	@XmlAttribute(required = true)
	private String cidadeDevedor;

	@XmlAttribute(required = true)
	private String ufDevedor;

	@XmlAttribute(required = true)
	private String numeroCartorio;

	@XmlAttribute(required = true)
	private String numeroProtocoloCartorio;

	@XmlAttribute(required = true)
	private String emBranco458;

	@XmlAttribute(required = true)
	private String dataCancelamentoProtesto;

	@XmlAttribute(required = true)
	private String especieProtesto;

	@XmlAttribute(required = true)
	private String emBranco487;

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

	public String getTipoInformacao() {
		return tipoInformacao;
	}

	public void setTipoInformacao(String tipoInformacao) {
		this.tipoInformacao = tipoInformacao;
	}

	public String getCodigoOperacao() {
		return codigoOperacao;
	}

	public void setCodigoOperacao(String codigoOperacao) {
		this.codigoOperacao = codigoOperacao;
	}

	public String getCodigoUnidadeFederativa() {
		return codigoUnidadeFederativa;
	}

	public void setCodigoUnidadeFederativa(String codigoUnidadeFederativa) {
		this.codigoUnidadeFederativa = codigoUnidadeFederativa;
	}

	public String getCodigoPracaEmbratel() {
		return codigoPracaEmbratel;
	}

	public void setCodigoPracaEmbratel(String codigoPracaEmbratel) {
		this.codigoPracaEmbratel = codigoPracaEmbratel;
	}

	public String getEmBranco10() {
		return emBranco10;
	}

	public void setEmBranco10(String emBranco10) {
		this.emBranco10 = emBranco10;
	}

	public String getNomeCredor() {
		return nomeCredor;
	}

	public void setNomeCredor(String nomeCredor) {
		this.nomeCredor = nomeCredor;
	}

	public String getNumeroDocumentoCredor() {
		return numeroDocumentoCredor;
	}

	public void setNumeroDocumentoCredor(String numeroDocumentoCredor) {
		this.numeroDocumentoCredor = numeroDocumentoCredor;
	}

	public String getComplementoDocumentoCredor() {
		return complementoDocumentoCredor;
	}

	public void setComplementoDocumentoCredor(String complementoDocumentoCredor) {
		this.complementoDocumentoCredor = complementoDocumentoCredor;
	}

	public String getDigitoControleDocumentoCredor() {
		return digitoControleDocumentoCredor;
	}

	public void setDigitoControleDocumentoCredor(String digitoControleDocumentoCredor) {
		this.digitoControleDocumentoCredor = digitoControleDocumentoCredor;
	}

	public String getEnderecoCredor() {
		return enderecoCredor;
	}

	public void setEnderecoCredor(String enderecoCredor) {
		this.enderecoCredor = enderecoCredor;
	}

	public String getCepCredor() {
		return cepCredor;
	}

	public void setCepCredor(String cepCredor) {
		this.cepCredor = cepCredor;
	}

	public String getCidadeCredor() {
		return cidadeCredor;
	}

	public void setCidadeCredor(String cidadeCredor) {
		this.cidadeCredor = cidadeCredor;
	}

	public String getUfCredor() {
		return ufCredor;
	}

	public void setUfCredor(String ufCredor) {
		this.ufCredor = ufCredor;
	}

	public String getMunicipioEnderecoCredor() {
		return municipioEnderecoCredor;
	}

	public void setMunicipioEnderecoCredor(String municipioEnderecoCredor) {
		this.municipioEnderecoCredor = municipioEnderecoCredor;
	}

	public String getTipoPessoaCredor() {
		return tipoPessoaCredor;
	}

	public void setTipoPessoaCredor(String tipoPessoaCredor) {
		this.tipoPessoaCredor = tipoPessoaCredor;
	}

	public String getTipoDocumentoCredor() {
		return tipoDocumentoCredor;
	}

	public void setTipoDocumentoCredor(String tipoDocumentoCredor) {
		this.tipoDocumentoCredor = tipoDocumentoCredor;
	}

	public String getEmBranco226() {
		return emBranco226;
	}

	public void setEmBranco226(String emBranco226) {
		this.emBranco226 = emBranco226;
	}

	public String getValorProtesto() {
		return valorProtesto;
	}

	public void setValorProtesto(String valorProtesto) {
		this.valorProtesto = valorProtesto;
	}

	public String getDataProtesto() {
		return dataProtesto;
	}

	public void setDataProtesto(String dataProtesto) {
		this.dataProtesto = dataProtesto;
	}

	public String getTipoPessoaDevedor() {
		return tipoPessoaDevedor;
	}

	public void setTipoPessoaDevedor(String tipoPessoaDevedor) {
		this.tipoPessoaDevedor = tipoPessoaDevedor;
	}

	public String getTipoDocumentoDevedor() {
		return tipoDocumentoDevedor;
	}

	public void setTipoDocumentoDevedor(String tipoDocumentoDevedor) {
		this.tipoDocumentoDevedor = tipoDocumentoDevedor;
	}

	public String getNumeroCoResponsavel() {
		return numeroCoResponsavel;
	}

	public void setNumeroCoResponsavel(String numeroCoResponsavel) {
		this.numeroCoResponsavel = numeroCoResponsavel;
	}

	public String getEmBranco273() {
		return emBranco273;
	}

	public void setEmBranco273(String emBranco273) {
		this.emBranco273 = emBranco273;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public String getEmBranco343() {
		return emBranco343;
	}

	public void setEmBranco343(String emBranco343) {
		this.emBranco343 = emBranco343;
	}

	public String getNumeroDocumentoDevedor() {
		return numeroDocumentoDevedor;
	}

	public void setNumeroDocumentoDevedor(String numeroDocumentoDevedor) {
		this.numeroDocumentoDevedor = numeroDocumentoDevedor;
	}

	public String getComplementoDocumentoDevedor() {
		return complementoDocumentoDevedor;
	}

	public void setComplementoDocumentoDevedor(String complementoDomentoDevedor) {
		this.complementoDocumentoDevedor = complementoDomentoDevedor;
	}

	public String getDigitoControleDocumentoDevedor() {
		return digitoControleDocumentoDevedor;
	}

	public void setDigitoControleDocumentoDevedor(String digitoControleDocumentoDevedor) {
		this.digitoControleDocumentoDevedor = digitoControleDocumentoDevedor;
	}

	public String getEmBranco361() {
		return emBranco361;
	}

	public void setEmBranco361(String emBranco361) {
		this.emBranco361 = emBranco361;
	}

	public String getEnderecoDevedor() {
		return enderecoDevedor;
	}

	public void setEnderecoDevedor(String enderecoDevedor) {
		this.enderecoDevedor = enderecoDevedor;
	}

	public String getCepDevedor() {
		return cepDevedor;
	}

	public void setCepDevedor(String cepDevedor) {
		this.cepDevedor = cepDevedor;
	}

	public String getCidadeDevedor() {
		return cidadeDevedor;
	}

	public void setCidadeDevedor(String cidadeDevedor) {
		this.cidadeDevedor = cidadeDevedor;
	}

	public String getUfDevedor() {
		return ufDevedor;
	}

	public void setUfDevedor(String ufDevedor) {
		this.ufDevedor = ufDevedor;
	}

	public String getNumeroCartorio() {
		return numeroCartorio;
	}

	public void setNumeroCartorio(String numeroCartorio) {
		this.numeroCartorio = numeroCartorio;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public String getEmBranco458() {
		return emBranco458;
	}

	public void setEmBranco458(String emBranco458) {
		this.emBranco458 = emBranco458;
	}

	public String getDataCancelamentoProtesto() {
		return dataCancelamentoProtesto;
	}

	public void setDataCancelamentoProtesto(String dataCancelamentoProtesto) {
		this.dataCancelamentoProtesto = dataCancelamentoProtesto;
	}

	public String getEspecieProtesto() {
		return especieProtesto;
	}

	public void setEspecieProtesto(String especieProtesto) {
		this.especieProtesto = especieProtesto;
	}

	public String getCodigoErro3Posicoes() {
		return codigoErro3Posicoes;
	}

	public void setCodigoErro3Posicoes(String codigoErro3Posicoes) {
		this.codigoErro3Posicoes = codigoErro3Posicoes;
	}

	public String getSequenciaRegistro() {
		return sequenciaRegistro;
	}

	public void setSequenciaRegistro(String sequenciaRegistro) {
		this.sequenciaRegistro = sequenciaRegistro;
	}

	public String getEmBranco487() {
		return emBranco487;
	}

	public void setEmBranco487(String emBranco487) {
		this.emBranco487 = emBranco487;
	}

	@Override
	public String getIdentificacaoRegistro() {
		return TipoRegistro.TITULO.getConstante();
	}
}
