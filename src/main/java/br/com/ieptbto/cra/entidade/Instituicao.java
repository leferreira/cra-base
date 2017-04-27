package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Table(name = "TB_INSTITUICAO")
@org.hibernate.annotations.Table(appliesTo = "TB_INSTITUICAO")
public class Instituicao extends AbstractEntidade<Instituicao> {

	private static final long serialVersionUID = 1;  
	private int id;
	private String nomeFantasia;
	private String razaoSocial;
	private String tabeliao;
	private String cnpj;
	private String codigoCompensacao;
	private String email;
	private String contato;
	private String bairro;
	private String endereco;
	private String telefone;
	private String responsavel;
	private String agenciaCentralizadora;
	private String favorecido;
	private String bancoContaCorrente;
	private String agenciaContaCorrente;
	private String numeroContaCorrente;
	private String codigoCartorio;
	private BigDecimal valorConfirmacao;
	private boolean situacao;
	private Boolean seloDiferido;
	private Boolean taxaCra;
	private Boolean verificacaoManual;
	private Municipio municipio;
	private TipoCampo51 tipoCampo51;
	private boolean layoutRetornoRecebimentoEmpresa;
	private boolean oficioDesistenciaCancelamentoObrigatorio;
	private boolean setoresConvenio;
	private boolean administrarEmpresasFiliadas;
	private Boolean retornoCancelamento;
	private TipoBatimento tipoBatimento;
	private LayoutPadraoXML layoutPadraoXML;
	private TipoInstituicao tipoInstituicao;
	private Integer versao;

	@Id
	@Column(name = "ID_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_FANTASIA", nullable = false, length = 100)
	public String getNomeFantasia() {
		return nomeFantasia;
	}

	@Column(name = "CNPJ", nullable = false, length = 50)
	public String getCnpj() {
		if (cnpj == null) {
			cnpj = StringUtils.EMPTY;
		}
		return cnpj.replace(".", "").replace("-", "").replace("/", "").trim();
	}

	@Column(name = "RAZAO_SOCIAL", length = 100, nullable = false)
	public String getRazaoSocial() {
		return razaoSocial;
	}

	@Column(name = "CODIGO_COMPENSACAO", length = 3)
	public String getCodigoCompensacao() {
		if (codigoCompensacao == null) {
			codigoCompensacao = StringUtils.EMPTY;
		}
		return codigoCompensacao;
	}

	@Column(name = "EMAIL", length = 50)
	public String getEmail() {
		return email;
	}

	@Column(name = "CONTATO", length = 40)
	public String getContato() {
		return contato;
	}

	@Column(name = "VALOR_CONFIRMACAO", precision = 2, columnDefinition = "double precision")
	public BigDecimal getValorConfirmacao() {
		if (null == valorConfirmacao) {
			valorConfirmacao = BigDecimal.ZERO;
		}
		return valorConfirmacao;
	}

	@Column(name = "ENDERECO", length = 50)
	public String getEndereco() {
		if (endereco == null) {
			endereco = StringUtils.EMPTY;
		}
		return endereco;
	}

	@Column(name = "RESPOSAVEL", length = 100)
	public String getResponsavel() {
		return responsavel;
	}

	@Column(name = "AGENCIA_CENTRALIZADORA", length = 50)
	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	@Column(name = "SITUACAO")
	public boolean getSituacao() {
		return situacao;
	}

	@Column(name = "FAVORECIDO")
	public String getFavorecido() {
		return favorecido;
	}

	@Column(name = "BANCO_CONTA_CORRENTE")
	public String getBancoContaCorrente() {
		return bancoContaCorrente;
	}

	@Column(name = "AGENCIA_CONTA_CORRENTE")
	public String getAgenciaContaCorrente() {
		return agenciaContaCorrente;
	}

	@Column(name = "NUMERO_CONTA_CORRENTE")
	public String getNumeroContaCorrente() {
		return numeroContaCorrente;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TIPO_INSTITUICAO_ID")
	public TipoInstituicao getTipoInstituicao() {
		return tipoInstituicao;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MUNICIPIO_ID")
	public Municipio getMunicipio() {
		return municipio;
	}

	@Column(name = "CODIGO_CARTORIO", length = 2, nullable = true)
	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	@Column(name = "SETORES_CONVENIO")
	public boolean getSetoresConvenio() {
		return setoresConvenio;
	}

	@Column(name = "ADMINISTRAR_EMPRESAS_FILIADAS")
	public boolean getAdministrarEmpresasFiliadas() {
		return administrarEmpresasFiliadas;
	}
	
	/**
     * Responsável por dizer se a instituição recebe ou não retorno de
     * cancelamento de títulos
     * 
     * @return Boolean retornoCancelamento
     */
    @Column(name = "RETORNO_CANCELAMENTO")
    public Boolean getRetornoCancelamento() {
        return retornoCancelamento;
    }

	@Column(name = "TELEFONE", length = 20)
	public String getTelefone() {
		return telefone;
	}

	@Column(name = "VERSAO")
	public Integer getVersao() {
		return versao;
	}

	@Column(name = "LAYOUT_RETORNO_RECEBIMENTO_EMPRESA")
	public boolean getLayoutRetornoRecebimentoEmpresa() {
		return layoutRetornoRecebimentoEmpresa;
	}

	@Column(name = "OFICIO_DESISTENCIA_CANCELAMENTO_OBRIGATORIO")
	public boolean getOficioDesistenciaCancelamentoObrigatorio() {
		return oficioDesistenciaCancelamentoObrigatorio;
	}

	@Transient
	public String getStatus() {
		if (getSituacao()) {
			return "Ativo";
		}
		return "Não Ativo";
	}

	@Column(name = "LAYOUT_PADRAO_XML")
	@Enumerated(EnumType.STRING)
	public LayoutPadraoXML getLayoutPadraoXML() {
		if (layoutPadraoXML == null) {
			layoutPadraoXML = LayoutPadraoXML.CRA_NACIONAL;
		}
		return layoutPadraoXML;
	}

	@Column(name = "TIPO_BATIMENTO")
	@Enumerated(EnumType.STRING)
	public TipoBatimento getTipoBatimento() {
		if (tipoBatimento == null) {
			tipoBatimento = TipoBatimento.BATIMENTO_REALIZADO_PELA_CRA;
		}
		return tipoBatimento;
	}

	@Column(name = "BAIRRO", length = 150)
	public String getBairro() {
		return bairro;
	}

	@Column(name = "TABELIAO", length = 150)
	public String getTabeliao() {
		if (tabeliao == null) {
			tabeliao = StringUtils.EMPTY;
		}
		return tabeliao;
	}

	@Transient
	public String getTelefoneSemDDD() {
		if (telefone != null) {
			return telefone.replaceAll("\\((10)|([1-9][1-9])\\)", "").replace("(", "").replace("-", "");
		}
		return StringUtils.EMPTY;
	}

	@Column(name = "TIPO_CAMPO_51")
	@Enumerated(EnumType.STRING)
	public TipoCampo51 getTipoCampo51() {
		if (tipoCampo51 == null) {
			tipoCampo51 = TipoCampo51.ALFANUMERICO;
		}
		return tipoCampo51;
	}

	@Column(name = "SELO_DIFERIDO")
	public Boolean getSeloDiferido() {
		return seloDiferido;
	}

	@Column(name = "TAXA_CRA")
	public Boolean getTaxaCra() {
		return taxaCra;
	}

	@Column(name = "VERIFICACAO_MANUAL")
	public Boolean getVerificacaoManual() {
		return verificacaoManual;
	}
	
	public void setId(int id) {
		this.id = id;
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

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCodigoCompensacao(String codigoCompensacao) {
		this.codigoCompensacao = codigoCompensacao;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
	}

	public void setFavorecido(String favorecido) {
		this.favorecido = favorecido;
	}

	public void setBancoContaCorrente(String bancoContaCorrente) {
		this.bancoContaCorrente = bancoContaCorrente;
	}

	public void setAgenciaContaCorrente(String agenciaContaCorrente) {
		this.agenciaContaCorrente = agenciaContaCorrente;
	}

	public void setNumeroContaCorrente(String numeroContaCorrente) {
		this.numeroContaCorrente = numeroContaCorrente;
	}

	public void setCodigoCartorio(String codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}

	public void setValorConfirmacao(BigDecimal valorConfirmacao) {
		this.valorConfirmacao = valorConfirmacao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	public void setSeloDiferido(Boolean seloDiferido) {
		this.seloDiferido = seloDiferido;
	}

	public void setTaxaCra(Boolean taxaCra) {
		this.taxaCra = taxaCra;
	}

	public void setVerificacaoManual(Boolean verificacaoManual) {
		this.verificacaoManual = verificacaoManual;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public void setTipoCampo51(TipoCampo51 tipoCampo51) {
		this.tipoCampo51 = tipoCampo51;
	}

	public void setLayoutRetornoRecebimentoEmpresa(boolean layoutRetornoRecebimentoEmpresa) {
		this.layoutRetornoRecebimentoEmpresa = layoutRetornoRecebimentoEmpresa;
	}

	public void setOficioDesistenciaCancelamentoObrigatorio(boolean oficioDesistenciaCancelamentoObrigatorio) {
		this.oficioDesistenciaCancelamentoObrigatorio = oficioDesistenciaCancelamentoObrigatorio;
	}

	public void setSetoresConvenio(boolean setoresConvenio) {
		this.setoresConvenio = setoresConvenio;
	}

	public void setAdministrarEmpresasFiliadas(boolean administrarEmpresasFiliadas) {
		this.administrarEmpresasFiliadas = administrarEmpresasFiliadas;
	}
	
	public void setRetornoCancelamento(Boolean retornoCancelamento) {
        this.retornoCancelamento = retornoCancelamento;
    }

	public void setTipoBatimento(TipoBatimento tipoBatimento) {
		this.tipoBatimento = tipoBatimento;
	}

	public void setLayoutPadraoXML(LayoutPadraoXML layoutPadraoXML) {
		this.layoutPadraoXML = layoutPadraoXML;
	}

	public void setTipoInstituicao(TipoInstituicao tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public void setVersao(Integer versao) {
		this.versao = versao;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Instituicao) {
			Instituicao modalidade = Instituicao.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getRazaoSocial(), modalidade.getRazaoSocial());
			equalsBuilder.append(this.getCnpj(), modalidade.getCnpj());
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
	public int compareTo(Instituicao entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		compareTo.append(this.getId(), entidade.getId());
		return compareTo.toComparison();
	}

	public void setStatus(String status) {
		if (status.equals("Ativo")) {
			setSituacao(true);
		} else {
			setSituacao(false);
		}
	}
}