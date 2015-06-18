package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

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

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.enumeration.TipoEspecieTitulo;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_TITULO_FILIADO")
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO_FILIADO")
public class TituloFiliado extends AbstractEntidade<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String numeroTitulo;
	private LocalDate dataEmissao;
	private LocalDate dataVencimento;
	private BigDecimal valorTitulo;
	private BigDecimal valorSaldoTitulo;
	private Municipio pracaProtesto;
	private String nomeDevedor;
	private String tipoDocumentoDevedor;
	private String documentoDevedor;
	private String enderecoDevedor;
	private String cidadeDevedor;
	private String cepDevedor;
	private String ufDevedor;
	private Filiado filiado;
	private SituacaoTituloConvenio situacaoTituloConvenio;
	private LocalDate dataEnvioCRA;
	private TipoEspecieTitulo especieTitulo;
	private String CpfCnpj;

	@Override
	@Id
	@Column(name = "ID_TITULO_FILIADO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NUMERO_TITULO", length = 11, nullable = false)
	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	@Column(name = "VALOR_TITULO", precision = 19, scale = 2)
	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	@Column(name = "VALOR_SALDO_TITULO", precision = 19, scale = 2)
	public BigDecimal getValorSaldoTitulo() {
		return valorSaldoTitulo;
	}

	@Column(name = "DATA_VENCIMENTO", nullable = false)
	public LocalDate getDataVencimento() {
		return dataVencimento;
	}

	@Column(name = "DATA_EMISSAO", nullable = false)
	public LocalDate getDataEmissao() {
		return dataEmissao;
	}

	@Column(name = "NOME_DEVEDOR", length = 45)
	public String getNomeDevedor() {
		return nomeDevedor;
	}

	@Column(name = "TIPO_DOCUMENTO_DEVEDOR", length = 3)
	public String getTipoDocumentoDevedor() {
		return tipoDocumentoDevedor;
	}

	@Column(name = "DOCUMENTO_DEVEDOR", length = 11)
	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	@Column(name = "ENDERECO_DEVEDOR", length = 45)
	public String getEnderecoDevedor() {
		return enderecoDevedor;
	}

	@Column(name = "CIDADE_DEVEDOR", length = 20)
	public String getCidadeDevedor() {
		return cidadeDevedor;
	}

	@Column(name = "CEP_DEVEDOR", length = 8)
	public String getCepDevedor() {
		return cepDevedor;
	}

	@Column(name = "UF_DEVEDOR", length = 2)
	public String getUfDevedor() {
		return ufDevedor;
	}

	@ManyToOne
	@JoinColumn(name = "MUNICIPIO_ID")
	public Municipio getPracaProtesto() {
		return pracaProtesto;
	}

	@ManyToOne
	@JoinColumn(name = "FILIADO_ID")
	public Filiado getFiliado() {
		return filiado;
	}

	@Column(name = "SITUACAO_TITULO_CONVENIO")
	@Enumerated(EnumType.STRING)
	public SituacaoTituloConvenio getSituacaoTituloConvenio() {
		return situacaoTituloConvenio;
	}

	@Column(name = "ESPECIE_TITULO")
	@Enumerated(EnumType.STRING)
	public TipoEspecieTitulo getEspecieTitulo() {
		return especieTitulo;
	}

	@Column(name = "DATA_ENVIO_CRA")
	public LocalDate getDataEnvioCRA() {
		return dataEnvioCRA;
	}

	@Column(name = "CPF_CNPJ_DEVEDOR", length = 11)
	public String getCpfCnpj() {
		return CpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		CpfCnpj = cpfCnpj;
	}

	public void setDataEnvioCRA(LocalDate dataEnvioCRA) {
		this.dataEnvioCRA = dataEnvioCRA;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setValorTitulo(BigDecimal valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setValorSaldoTitulo(BigDecimal valorSaldoTitulo) {
		this.valorSaldoTitulo = valorSaldoTitulo;
	}

	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public void setDataEmissao(LocalDate dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
	}

	public void setTipoDocumentoDevedor(String tipoDocumentoDevedor) {
		this.tipoDocumentoDevedor = tipoDocumentoDevedor;
	}

	public void setEnderecoDevedor(String enderecoDevedor) {
		this.enderecoDevedor = enderecoDevedor;
	}

	public void setCidadeDevedor(String cidadeDevedor) {
		this.cidadeDevedor = cidadeDevedor;
	}

	public void setEspecieTitulo(TipoEspecieTitulo especieTitulo) {
		this.especieTitulo = especieTitulo;
	}

	public void setCepDevedor(String cepDevedor) {
		this.cepDevedor = cepDevedor;
	}

	public void setUfDevedor(String ufDevedor) {
		this.ufDevedor = ufDevedor;
	}

	public void setPracaProtesto(Municipio pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}

	public void setSituacaoTituloConvenio(SituacaoTituloConvenio situacaoTituloConvenio) {
		this.situacaoTituloConvenio = situacaoTituloConvenio;
	}

	@Override
	public int compareTo(TituloFiliado entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
