package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.enumeration.TipoRegistroCnp;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_REGISTRO_CNP", uniqueConstraints=
	@UniqueConstraint(columnNames={"tipo_registro_cnp", "cidade_devedor", "numero_protocolo_cartorio", 
		"numero_documento_devedor", "digito_controle_documento_devedor"}))
@org.hibernate.annotations.Table(appliesTo = "TB_REGISTRO_CNP")
public class RegistroCnp extends AbstractEntidade<RegistroCnp> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private LoteCnp loteCnp;
	private TipoRegistroCnp tipoRegistroCnp;
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
	private Date dataProtesto;
	private String tipoPessoaDevedor;
	private String tipoDocumentoDevedor;
	private String numeroCoResponsavel;
	private String nomeDevedor;
	private String numeroDocumentoDevedor;
	private String complementoDocumentoDevedor;
	private String digitoControleDocumentoDevedor;
	private String enderecoDevedor;
	private String cepDevedor;
	private String cidadeDevedor;
	private String ufDevedor;
	private String numeroCartorio;
	private String numeroProtocoloCartorio;
	private Date dataCancelamentoProtesto;
	private String especieProtesto;
	private String codigoErro3Posicoes;
	private String sequenciaRegistro;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_REGISTRO_CNP", columnDefinition = "serial")
	public int getId() {
		return this.id;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "LOTE_CNP_ID", columnDefinition = "integer null")
	public LoteCnp getLoteCnp() {
		return loteCnp;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_REGISTRO_CNP")
	public TipoRegistroCnp getTipoRegistroCnp() {
		return tipoRegistroCnp;
	}

	@Column(name = "CODIGO_REGISTRO", length = 1)
	public String getCodigoRegistro() {
		return codigoRegistro;
	}

	@Column(name = "TIPO_INFORMACAO", length = 1)
	public String getTipoInformacao() {
		return tipoInformacao;
	}

	@Column(name = "CODIGO_OPERACAO", length = 1)
	public String getCodigoOperacao() {
		return codigoOperacao;
	}

	@Column(name = "CODIGO_UNIDADE_FEDERATIVA", length = 2)
	public String getCodigoUnidadeFederativa() {
		return codigoUnidadeFederativa;
	}

	@Column(name = "CODIGO_PRACA_EMBRATEL", length = 4)
	public String getCodigoPracaEmbratel() {
		return codigoPracaEmbratel;
	}

	@Column(name = "NOME_CREDOR", length = 45)
	public String getNomeCredor() {
		return RemoverAcentosUtil.removeAcentos(nomeCredor);
	}

	@Column(name = "NUMERO_DOCUMENTO_CREDOR", length = 9)
	public String getNumeroDocumentoCredor() {
		return numeroDocumentoCredor;
	}

	@Column(name = "COMPLEMENTO_DOCUMENTO_CREDOR", length = 4)
	public String getComplementoDocumentoCredor() {
		return complementoDocumentoCredor;
	}

	@Column(name = "DIGITO_CONTROLE_DOCUMENTO_CREDOR", length = 2)
	public String getDigitoControleDocumentoCredor() {
		return digitoControleDocumentoCredor;
	}

	@Column(name = "ENDERECO_CREDOR", length = 45)
	public String getEnderecoCredor() {
		return RemoverAcentosUtil.removeAcentos(enderecoCredor);
	}

	@Column(name = "CEP_CREDOR", length = 8)
	public String getCepCredor() {
		return cepCredor;
	}

	@Column(name = "CIDADE_CREDOR", length = 20)
	public String getCidadeCredor() {
		return RemoverAcentosUtil.removeAcentos(cidadeCredor);
	}

	@Column(name = "UF_CREDOR", length = 2)
	public String getUfCredor() {
		return ufCredor;
	}

	@Column(name = "MUNICIPIO_ENDERECO_CREDOR", length = 25)
	public String getMunicipioEnderecoCredor() {
		return RemoverAcentosUtil.removeAcentos(municipioEnderecoCredor);
	}

	@Column(name = "TIPO_PESSOA_CREDOR", length = 1)
	public String getTipoPessoaCredor() {
		return tipoPessoaCredor;
	}

	@Column(name = "TIPO_DOCUMENTO_CREDOR", length = 1)
	public String getTipoDocumentoCredor() {
		return tipoDocumentoCredor;
	}

	@Column(name = "VALOR_PROTESTO")
	public BigDecimal getValorProtesto() {
		return valorProtesto;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_PROTESTO")
	public Date getDataProtesto() {
		return dataProtesto;
	}

	@Column(name = "TIPO_PESSOA_DEVEDOR", length = 1)
	public String getTipoPessoaDevedor() {
		return tipoPessoaDevedor;
	}

	@Column(name = "TIPO_DOCUMENTO_DEVEDOR", length = 1)
	public String getTipoDocumentoDevedor() {
		return tipoDocumentoDevedor;
	}

	@Column(name = "NUMERO_CO_RESPONSAVEL", length = 22)
	public String getNumeroCoResponsavel() {
		return numeroCoResponsavel;
	}

	@Column(name = "NOME_DEVEDOR", length = 45)
	public String getNomeDevedor() {
		return RemoverAcentosUtil.removeAcentos(nomeDevedor);
	}

	@Column(name = "NUMERO_DOCUMENTO_DEVEDOR", length = 9)
	public String getNumeroDocumentoDevedor() {
		return numeroDocumentoDevedor;
	}

	@Column(name = "COMPLEMENTO_DOCUMENTO_DEVEDOR", length = 4)
	public String getComplementoDocumentoDevedor() {
		return complementoDocumentoDevedor;
	}

	@Column(name = "DIGITO_CONTROLE_DOCUMENTO_DEVEDOR", length = 2)
	public String getDigitoControleDocumentoDevedor() {
		return digitoControleDocumentoDevedor;
	}

	@Column(name = "ENDERECO_DEVEDOR", length = 45)
	public String getEnderecoDevedor() {
		return RemoverAcentosUtil.removeAcentos(enderecoDevedor);
	}

	@Column(name = "CEP_DEVEDOR", length = 8)
	public String getCepDevedor() {
		return cepDevedor;
	}

	@Column(name = "CIDADE_DEVEDOR", length = 20)
	public String getCidadeDevedor() {
		return RemoverAcentosUtil.removeAcentos(cidadeDevedor);
	}

	@Column(name = "UF_DEVEDOR", length = 2)
	public String getUfDevedor() {
		return ufDevedor;
	}

	@Column(name = "NUMERO_CARTORIO", length = 2)
	public String getNumeroCartorio() {
		return numeroCartorio;
	}

	@Column(name = "NUMERO_PROTOCOLO_CARTORIO", length = 10)
	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_CANCELAMENTO")
	public Date getDataCancelamentoProtesto() {
		return dataCancelamentoProtesto;
	}

	@Column(name = "ESPECIE_PROTESTO", length = 1)
	public String getEspecieProtesto() {
		return especieProtesto;
	}

	@Column(name = "CODIGO_ERRO_3_POSICOES", length = 60)
	public String getCodigoErro3Posicoes() {
		return codigoErro3Posicoes;
	}

	@Column(name = "SEQUENCIA_REGISTRO", length = 7)
	public String getSequenciaRegistro() {
		return sequenciaRegistro;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLoteCnp(LoteCnp lote) {
		this.loteCnp = lote;
	}

	public void setTipoRegistroCnp(TipoRegistroCnp tipoRegistroCnp) {
		this.tipoRegistroCnp = tipoRegistroCnp;
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

	public void setDataProtesto(Date dataProtesto) {
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

	public void setComplementoDocumentoDevedor(String complementoDocumentoDevedor) {
		this.complementoDocumentoDevedor = complementoDocumentoDevedor;
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

	public void setDataCancelamentoProtesto(Date dataCancelamentoProtesto) {
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
	public int compareTo(RegistroCnp entidade) {
		return 0;
	}	
}