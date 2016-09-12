package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.enumeration.TipoAlineaCheque;
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

	private Usuario usuarioEntradaManual;
	private SetorFiliado setor;
	private Filiado filiado;
	private byte[] anexo;

	private String numeroTitulo;
	private Date dataEmissao;
	private Date dataVencimento;
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
	private String bairroDevedor;
	private List<Avalista> avalistas;
	private TipoAlineaCheque alinea;
	private SituacaoTituloConvenio situacaoTituloConvenio;
	private Date dataEnvioCRA;
	private Date dataEntrada;
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
	@Temporal(TemporalType.DATE)
	public Date getDataVencimento() {
		return dataVencimento;
	}

	@Column(name = "DATA_EMISSAO", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getDataEmissao() {
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
		if (enderecoDevedor != null) {
			return enderecoDevedor.replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "");
		}
		return enderecoDevedor;
	}

	@Column(name = "CIDADE_DEVEDOR", length = 20)
	public String getCidadeDevedor() {
		return cidadeDevedor;
	}

	@Column(name = "CEP_DEVEDOR", length = 10)
	public String getCepDevedor() {
		if (cepDevedor == null) {
			cepDevedor = StringUtils.EMPTY;
		}
		return cepDevedor.replace(".", "").replace("-", "").trim();
	}

	@ManyToOne
	@JoinColumn(name = "GRUPO_USUARIO_ID")
	public Usuario getUsuarioEntradaManual() {
		return usuarioEntradaManual;
	}

	@Column(name = "UF_DEVEDOR", length = 2)
	public String getUfDevedor() {
		return ufDevedor;
	}

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "SETOR_ID", nullable = false)
	public SetorFiliado getSetor() {
		return setor;
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
	@Temporal(TemporalType.DATE)
	public Date getDataEnvioCRA() {
		return dataEnvioCRA;
	}

	@Column(name = "DATA_ENTRADA")
	@Temporal(TemporalType.DATE)
	public Date getDataEntrada() {
		if (dataEntrada == null) {
			dataEntrada = new LocalDate().toDate();
		}
		return dataEntrada;
	}

	@Column(name = "CPF_CNPJ_DEVEDOR", length = 18)
	public String getCpfCnpj() {
		if (CpfCnpj == null) {
			CpfCnpj = StringUtils.EMPTY;
		}
		return CpfCnpj.replace(".", "").replace("-", "").replace("/", "").trim();
	}

	@Column(name = "ANEXO")
	public byte[] getAnexo() {
		return anexo;
	}

	@Transient
	public String getAnexoAsString() {
		return new String(this.anexo);
	}

	public void setCpfCnpj(String cpfCnpj) {
		CpfCnpj = cpfCnpj;
	}

	public void setDataEnvioCRA(Date dataEnvioCRA) {
		this.dataEnvioCRA = dataEnvioCRA;
	}

	public void setUsuarioEntradaManual(Usuario usuarioEntradaManual) {
		this.usuarioEntradaManual = usuarioEntradaManual;
	}

	public void setSetor(SetorFiliado setor) {
		this.setor = setor;
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

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public void setValorSaldoTitulo(BigDecimal valorSaldoTitulo) {
		this.valorSaldoTitulo = valorSaldoTitulo;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public void setDataEmissao(Date dataEmissao) {
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

	public void setAnexo(byte[] anexo) {
		this.anexo = anexo;
	}

	public void setSituacaoTituloConvenio(SituacaoTituloConvenio situacaoTituloConvenio) {
		this.situacaoTituloConvenio = situacaoTituloConvenio;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TituloFiliado) {
			TituloFiliado modalidade = TituloFiliado.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getFiliado(), modalidade.getFiliado());
			equalsBuilder.append(this.getNomeDevedor(), modalidade.getNomeDevedor());
			equalsBuilder.append(this.getNumeroTitulo(), modalidade.getNumeroTitulo());
			return equalsBuilder.isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (getId() == 0) {
			return 0;
		}
		return getId();
	}

	@Override
	public int compareTo(TituloFiliado tituloFiliado) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}

	@Column(name = "ALINEA")
	@Enumerated(EnumType.STRING)
	public TipoAlineaCheque getAlinea() {
		return alinea;
	}

	public void setAlinea(TipoAlineaCheque alinea) {
		this.alinea = alinea;
	}

	@Column(name = "BAIRRO_DEVEDOR", length = 20)
	public String getBairroDevedor() {
		return bairroDevedor;
	}

	public void setBairroDevedor(String bairroDevedor) {
		this.bairroDevedor = bairroDevedor;
	}

	@OneToMany(mappedBy = "tituloFiliado")
	public List<Avalista> getAvalistas() {
		return avalistas;
	}

	public void setAvalistas(List<Avalista> avalistas) {
		this.avalistas = avalistas;
	}
}
