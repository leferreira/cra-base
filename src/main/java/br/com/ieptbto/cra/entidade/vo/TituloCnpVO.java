package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
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
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "", obrigatoriedade = true, tipo = Integer.class)
	private String codigoRegistro;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 1, descricao = "", obrigatoriedade = false)
	private String tipoInformacao;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 3, posicao = 3, tamanho = 1, descricao = "", obrigatoriedade = false)
	private String codigoOperacao;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 4, posicao = 4, tamanho = 2, descricao = "", obrigatoriedade = false)
	private String codigoUnidadeFederativa;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 5, posicao = 6, tamanho = 7, descricao = "", obrigatoriedade = false)
	private String codigoPracaEmbratel;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 6, posicao = 10, tamanho = 55, descricao = "", obrigatoriedade = false)
	private String emBranco10;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 7, posicao = 65, tamanho = 45, descricao = "", obrigatoriedade = false)
	private String nomeCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 8, posicao = 110, tamanho = 9, descricao = "", obrigatoriedade = false)
	private String numeroDocumentoCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 9, posicao = 119, tamanho = 4, descricao = "", obrigatoriedade = false)
	private String complementoDocumentoCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 10, posicao = 123, tamanho = 2, descricao = "", obrigatoriedade = false)
	private String digitoControleDocumentoCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 11, posicao = 125, tamanho = 44, descricao = "", obrigatoriedade = false)
	private String enderecoCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 12, posicao = 169, tamanho = 8, descricao = "", obrigatoriedade = false)
	private String cepCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 13, posicao = 177, tamanho = 20, descricao = "", obrigatoriedade = false)
	private String cidadeCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 14, posicao = 197, tamanho = 2, descricao = "", obrigatoriedade = false)
	private String ufCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 15, posicao = 199, tamanho = 25, descricao = "", obrigatoriedade = false)
	private String municipioEnderecoCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 16, posicao = 224, tamanho = 1, descricao = "", obrigatoriedade = false)
	private String tipoPessoaCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 17, posicao = 225, tamanho = 1, descricao = "", obrigatoriedade = false)
	private String tipoDocumentoCredor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 18, posicao = 226, tamanho = 1, descricao = "", obrigatoriedade = false)
	private String emBranco226;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 19, posicao = 247, tamanho = 14, descricao = "", obrigatoriedade = false)
	private String valorProtesto;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 20, posicao = 261, tamanho = 8, descricao = "", obrigatoriedade = false)
	private String dataProtesto;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 21, posicao = 269, tamanho = 1, descricao = "", obrigatoriedade = false)
	private String tipoPessoaDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 22, posicao = 270, tamanho = 1, descricao = "", obrigatoriedade = false)
	private String tipoDocumentoDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 23, posicao = 271, tamanho = 2, descricao = "", obrigatoriedade = false)
	private String numeroCoResponsavel;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 24, posicao = 273, tamanho = 25, descricao = "", obrigatoriedade = false)
	private String emBranco273;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 25, posicao = 298, tamanho = 45, descricao = "", obrigatoriedade = false)
	private String nomeDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 26, posicao = 343, tamanho = 3, descricao = "", obrigatoriedade = false)
	private String emBranco343;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 27, posicao = 346, tamanho = 9, descricao = "", obrigatoriedade = false)
	private String numeroDocumentoDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 28, posicao = 355, tamanho = 4, descricao = "", obrigatoriedade = false)
	private String complementoDocumentoDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 29, posicao = 359, tamanho = 2, descricao = "", obrigatoriedade = false)
	private String digitoControleDocumentoDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 30, posicao = 361, tamanho = 10, descricao = "", obrigatoriedade = false)
	private String emBranco361;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 31, posicao = 371, tamanho = 45, descricao = "", obrigatoriedade = false)
	private String enderecoDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 32, posicao = 416, tamanho = 8, descricao = "", obrigatoriedade = false)
	private String cepDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 33, posicao = 424, tamanho = 20, descricao = "", obrigatoriedade = false)
	private String cidadeDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 34, posicao = 444, tamanho = 2, descricao = "", obrigatoriedade = false)
	private String ufDevedor;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 35, posicao = 446, tamanho = 2, descricao = "", obrigatoriedade = false)
	private String numeroCartorio;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 36, posicao = 448, tamanho = 10, descricao = "", obrigatoriedade = false)
	private String numeroProtocoloCartorio;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 37, posicao = 458, tamanho = 20, descricao = "", obrigatoriedade = false)
	private String emBranco458;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 38, posicao = 478, tamanho = 8, descricao = "", obrigatoriedade = false)
	private String dataCancelamentoProtesto;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 39, posicao = 486, tamanho = 1, descricao = "", obrigatoriedade = false)
	private String especieProtesto;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 40, posicao = 487, tamanho = 47, descricao = "", obrigatoriedade = false)
	private String emBranco487;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 41, posicao = 534, tamanho = 60, descricao = "", obrigatoriedade = false)
	private String codigoErro3Posicoes;

	@XmlAttribute(required = true)
	@IAtributoArquivo(ordem = 42, posicao = 594, tamanho = 7, descricao = "", obrigatoriedade = false)
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
