package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_TITULO_CNP")
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO_CNP")
public class TituloCnp extends AbstractEntidade<TituloCnp> {

	private int id;
	private RemessaCnp remessa;
	private String codigoRegistro;
	private String tipoInformacao;
	private String codigoOperacao;
	private String codigoUnidadeFederativa;
	private String codigoPracaEmbratel;
	private String nomeCredor;
	private String numeroDocumentoCredor;
	private String complementoDocumentoCredor;
	private String digitoControleDocumentoCredor;
	private String enderecoCredor;
	private String cepCredor;
	private String cidadeCredor;
	private String ufCredor;
	private String municipioEnderecoCredor;
	private String tipoPessoaCredor;
	private String tipoDocumentoCredor;
	private BigDecimal valorProtesto;
	private LocalDate dataProtesto;
	private String tipoPessoaDevedor;
	private String tipoDocumentoDevedor;
	private String numeroCoResponsavel;
	private String nomeDevedor;
	private String numeroDocumentoDevedor;
	private String complementoDomentoDevedor;
	private String digitoControleDocumentoDevedor;
	private String enderecoDevedor;
	private String cepDevedor;
	private String cidadeDevedor;
	private String ufDevedor;
	private String numeroCartorio;
	private String numeroProtocoloCartorio;
	private LocalDate dataCancelamentoProtesto;
	private String especieProtesto;
	private String codigoErro3Posicoes;
	private String sequenciaRegistro;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_TITULO_CNP", columnDefinition = "serial")
	public int getId() {
		return this.id;
	}

	@ManyToOne
	@JoinColumn(name = "REMESSA_ID", columnDefinition = "integer not null")
	public RemessaCnp getRemessa() {
		return remessa;
	}

	public String getCodigoRegistro() {
		return codigoRegistro;
	}

	public String getTipoInformacao() {
		return tipoInformacao;
	}

	public String getCodigoOperacao() {
		return codigoOperacao;
	}

	public String getCodigoUnidadeFederativa() {
		return codigoUnidadeFederativa;
	}

	public String getCodigoPracaEmbratel() {
		return codigoPracaEmbratel;
	}

	public String getNomeCredor() {
		return nomeCredor;
	}

	public String getNumeroDocumentoCredor() {
		return numeroDocumentoCredor;
	}

	public String getComplementoDocumentoCredor() {
		return complementoDocumentoCredor;
	}

	public String getDigitoControleDocumentoCredor() {
		return digitoControleDocumentoCredor;
	}

	public String getEnderecoCredor() {
		return enderecoCredor;
	}

	public String getCepCredor() {
		return cepCredor;
	}

	public String getCidadeCredor() {
		return cidadeCredor;
	}

	public String getUfCredor() {
		return ufCredor;
	}

	public String getMunicipioEnderecoCredor() {
		return municipioEnderecoCredor;
	}

	public String getTipoPessoaCredor() {
		return tipoPessoaCredor;
	}

	public String getTipoDocumentoCredor() {
		return tipoDocumentoCredor;
	}

	public BigDecimal getValorProtesto() {
		return valorProtesto;
	}

	public LocalDate getDataProtesto() {
		return dataProtesto;
	}

	public String getTipoPessoaDevedor() {
		return tipoPessoaDevedor;
	}

	public String getTipoDocumentoDevedor() {
		return tipoDocumentoDevedor;
	}

	public String getNumeroCoResponsavel() {
		return numeroCoResponsavel;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public String getNumeroDocumentoDevedor() {
		return numeroDocumentoDevedor;
	}

	public String getComplementoDomentoDevedor() {
		return complementoDomentoDevedor;
	}

	public String getDigitoControleDocumentoDevedor() {
		return digitoControleDocumentoDevedor;
	}

	public String getEnderecoDevedor() {
		return enderecoDevedor;
	}

	public String getCepDevedor() {
		return cepDevedor;
	}

	public String getCidadeDevedor() {
		return cidadeDevedor;
	}

	public String getUfDevedor() {
		return ufDevedor;
	}

	public String getNumeroCartorio() {
		return numeroCartorio;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public LocalDate getDataCancelamentoProtesto() {
		return dataCancelamentoProtesto;
	}

	public String getEspecieProtesto() {
		return especieProtesto;
	}

	public String getCodigoErro3Posicoes() {
		return codigoErro3Posicoes;
	}

	public String getSequenciaRegistro() {
		return sequenciaRegistro;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(RemessaCnp remessa) {
		this.remessa = remessa;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public void setTipoInformacao(String tipoInformacao) {
		this.tipoInformacao = tipoInformacao;
	}

	public void setCodigoOperacao(String codigoOperacao) {
		this.codigoOperacao = codigoOperacao;
	}

	public void setCodigoUnidadeFederativa(String codigoUnidadeFederativa) {
		this.codigoUnidadeFederativa = codigoUnidadeFederativa;
	}

	public void setCodigoPracaEmbratel(String codigoPracaEmbratel) {
		this.codigoPracaEmbratel = codigoPracaEmbratel;
	}

	public void setNomeCredor(String nomeCredor) {
		this.nomeCredor = nomeCredor;
	}

	public void setNumeroDocumentoCredor(String numeroDocumentoCredor) {
		this.numeroDocumentoCredor = numeroDocumentoCredor;
	}

	public void setComplementoDocumentoCredor(String complementoDocumentoCredor) {
		this.complementoDocumentoCredor = complementoDocumentoCredor;
	}

	public void setDigitoControleDocumentoCredor(String digitoControleDocumentoCredor) {
		this.digitoControleDocumentoCredor = digitoControleDocumentoCredor;
	}

	public void setEnderecoCredor(String enderecoCredor) {
		this.enderecoCredor = enderecoCredor;
	}

	public void setCepCredor(String cepCredor) {
		this.cepCredor = cepCredor;
	}

	public void setCidadeCredor(String cidadeCredor) {
		this.cidadeCredor = cidadeCredor;
	}

	public void setUfCredor(String ufCredor) {
		this.ufCredor = ufCredor;
	}

	public void setMunicipioEnderecoCredor(String municipioEnderecoCredor) {
		this.municipioEnderecoCredor = municipioEnderecoCredor;
	}

	public void setTipoPessoaCredor(String tipoPessoaCredor) {
		this.tipoPessoaCredor = tipoPessoaCredor;
	}

	public void setTipoDocumentoCredor(String tipoDocumentoCredor) {
		this.tipoDocumentoCredor = tipoDocumentoCredor;
	}

	public void setValorProtesto(BigDecimal valorProtesto) {
		this.valorProtesto = valorProtesto;
	}

	public void setDataProtesto(LocalDate dataProtesto) {
		this.dataProtesto = dataProtesto;
	}

	public void setTipoPessoaDevedor(String tipoPessoaDevedor) {
		this.tipoPessoaDevedor = tipoPessoaDevedor;
	}

	public void setTipoDocumentoDevedor(String tipoDocumentoDevedor) {
		this.tipoDocumentoDevedor = tipoDocumentoDevedor;
	}

	public void setNumeroCoResponsavel(String numeroCoResponsavel) {
		this.numeroCoResponsavel = numeroCoResponsavel;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setNumeroDocumentoDevedor(String numeroDocumentoDevedor) {
		this.numeroDocumentoDevedor = numeroDocumentoDevedor;
	}

	public void setComplementoDomentoDevedor(String complementoDomentoDevedor) {
		this.complementoDomentoDevedor = complementoDomentoDevedor;
	}

	public void setDigitoControleDocumentoDevedor(String digitoControleDocumentoDevedor) {
		this.digitoControleDocumentoDevedor = digitoControleDocumentoDevedor;
	}

	public void setEnderecoDevedor(String enderecoDevedor) {
		this.enderecoDevedor = enderecoDevedor;
	}

	public void setCepDevedor(String cepDevedor) {
		this.cepDevedor = cepDevedor;
	}

	public void setCidadeDevedor(String cidadeDevedor) {
		this.cidadeDevedor = cidadeDevedor;
	}

	public void setUfDevedor(String ufDevedor) {
		this.ufDevedor = ufDevedor;
	}

	public void setNumeroCartorio(String numeroCartorio) {
		this.numeroCartorio = numeroCartorio;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setDataCancelamentoProtesto(LocalDate dataCancelamentoProtesto) {
		this.dataCancelamentoProtesto = dataCancelamentoProtesto;
	}

	public void setEspecieProtesto(String especieProtesto) {
		this.especieProtesto = especieProtesto;
	}

	public void setCodigoErro3Posicoes(String codigoErro3Posicoes) {
		this.codigoErro3Posicoes = codigoErro3Posicoes;
	}

	public void setSequenciaRegistro(String sequenciaRegistro) {
		this.sequenciaRegistro = sequenciaRegistro;
	}

	@Override
	public int compareTo(TituloCnp entidade) {
		return 0;
	}

	@Transient
	public String getSituacao() {
		if (this.getTipoInformacao().equals("P")) {
			return "PROTESTADO";
		}
		if (this.getTipoInformacao().equals("C")) {
			return "CANCELADO";
		}
		return "PROTESTADO";
	}

}
