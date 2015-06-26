package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.conversor.arquivo.TituloConversor;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.enumeration.TipoRegistro;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_TITULO", uniqueConstraints = { @UniqueConstraint(columnNames = { "CODIGO_PORTADOR", "NOSSO_NUMERO", "NUMERO_TITULO",
        "DATA_CADASTRO" }) })
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO")
public class TituloRemessa extends Titulo<TituloRemessa> {

	/*** */
	private static final long serialVersionUID = 1L;

	private int id;
	private List<Historico> historicos;
	private Confirmacao confirmacao;
	private Retorno retorno;

	private String nomeCedenteFavorecido;
	private String nomeSacadorVendedor;
	private String documentoSacador;
	private String enderecoSacadorVendedor;
	private String cepSacadorVendedor;
	private String cidadeSacadorVendedor;
	private String ufSacadorVendedor;
	private String especieTitulo;
	private LocalDate dataEmissaoTitulo; // As datas são passada assim: DDMMAAAA
	private LocalDate dataVencimentoTitulo;
	private String tipoMoeda;
	private BigDecimal valorTitulo;
	private String pracaProtesto;
	private String tipoEndoso;
	private String informacaoSobreAceite;
	private Integer numeroControleDevedor;
	private String nomeDevedor;
	private String tipoIdentificacaoDevedor;
	private String numeroIdentificacaoDevedor;
	private String documentoDevedor;
	private String enderecoDevedor;
	private String cepDevedor;
	private String cidadeDevedor;
	private Integer numeroOperacaoBanco;
	private Integer numeroContratoBanco;
	private Integer numeroParcelaContrato;
	private String tipoLetraCambio;
	private String protestoMotivoFalencia;
	private String instrumentoProtesto;
	private String complementoRegistro;
	private String situacaoTitulo;
	private Date dataCadastro;

	@Id
	@Column(name = "ID_TITULO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public int getId() {
		return this.id;
	}

	@OneToOne(optional = true, mappedBy = "titulo")
	public Confirmacao getConfirmacao() {
		return confirmacao;
	}

	@OneToOne(optional = true, mappedBy = "titulo", fetch = FetchType.LAZY)
	public Retorno getRetorno() {
		return retorno;
	}

	@OneToMany(mappedBy = "titulo", fetch = FetchType.LAZY)
	public List<Historico> getHistoricos() {
		return historicos;
	}

	@Column(name = "NOME_CEDENTE_FAVORECIDO")
	public String getNomeCedenteFavorecido() {
		return nomeCedenteFavorecido;
	}

	@Column(name = "NOME_SACADOR_VENDEDOR")
	public String getNomeSacadorVendedor() {
		return nomeSacadorVendedor;
	}

	@Column(name = "DOCUMENTO_SACADOR")
	public String getDocumentoSacador() {
		return documentoSacador;
	}

	@Column(name = "ENDERECO_SACADOR_DEVEDOR")
	public String getEnderecoSacadorVendedor() {
		return enderecoSacadorVendedor;
	}

	@Column(name = "CEP_SACADOR_DEVEDOR")
	public String getCepSacadorVendedor() {
		return cepSacadorVendedor;
	}

	@Column(name = "CIDADE_CIDADE")
	public String getCidadeSacadorVendedor() {
		return cidadeSacadorVendedor;
	}

	@Column(name = "UF_SACADOR_VENDEDOR")
	public String getUfSacadorVendedor() {
		return ufSacadorVendedor;
	}

	@Column(name = "ESPECIE_TITULO", length = 3)
	public String getEspecieTitulo() {
		return especieTitulo;
	}

	@Column(name = "DATA_EMISSAO")
	public LocalDate getDataEmissaoTitulo() {
		return dataEmissaoTitulo;
	}

	@Column(name = "DATA_VENCIMENTO")
	public LocalDate getDataVencimentoTitulo() {
		return dataVencimentoTitulo;
	}

	@Column(name = "TIPO_MOEDA")
	public String getTipoMoeda() {
		return tipoMoeda;
	}

	@Column(name = "VALOR_TITULO")
	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	@Column(name = "PRACA_PROTESTO")
	public String getPracaProtesto() {
		return pracaProtesto;
	}

	@Column(name = "TIPO_ENDOSO")
	public String getTipoEndoso() {
		return tipoEndoso;
	}

	@Column(name = "INFORMACAO_SOBRE_ACEITE")
	public String getInformacaoSobreAceite() {
		return informacaoSobreAceite;
	}

	@Column(name = "NUMERO_CONTROLE_DEVEDOR")
	public Integer getNumeroControleDevedor() {
		return numeroControleDevedor;
	}

	@Column(name = "NOME_DEVEDOR")
	public String getNomeDevedor() {
		return nomeDevedor;
	}

	@Column(name = "TIPO_IDENTIFICACAO_DEVEDOR")
	public String getTipoIdentificacaoDevedor() {
		return tipoIdentificacaoDevedor;
	}

	@Column(name = "NUMERO_IDENTIFICACAO_DEVEDOR")
	public String getNumeroIdentificacaoDevedor() {
		return numeroIdentificacaoDevedor;
	}

	@Column(name = "DOCUMENTO_DEVEDOR")
	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	@Column(name = "ENDERECO_DEVEDOR")
	public String getEnderecoDevedor() {
		return enderecoDevedor;
	}

	@Column(name = "CEP_DEVEDOR")
	public String getCepDevedor() {
		return cepDevedor;
	}

	@Column(name = "CIDADE_DEVEDOR")
	public String getCidadeDevedor() {
		return cidadeDevedor;
	}

	@Column(name = "NUMERO_OPERACAO_BANCO")
	public Integer getNumeroOperacaoBanco() {
		return numeroOperacaoBanco;
	}

	@Column(name = "NUMERO_CONTRATO_BANCO")
	public Integer getNumeroContratoBanco() {
		return numeroContratoBanco;
	}

	@Column(name = "NUMERO_PARCELA_CONTRATO")
	public Integer getNumeroParcelaContrato() {
		return numeroParcelaContrato;
	}

	@Column(name = "TIPO_LETRA_CAMBIO")
	public String getTipoLetraCambio() {
		return tipoLetraCambio;
	}

	@Column(name = "PROTESTO_MOTIVO_FALENCIA")
	public String getProtestoMotivoFalencia() {
		return protestoMotivoFalencia;
	}

	@Column(name = "INSTRUMENTO_PROTESTO")
	public String getInstrumentoProtesto() {
		return instrumentoProtesto;
	}

	@Column(name = "COMPLEMENTO_REGISTRO")
	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	@Column(name = "DATA_CADASTRO")
	@Type(type = "date")
	public Date getDataCadastro() {
		if (dataCadastro == null) {
			dataCadastro = new Date();
		}
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setConfirmacao(Confirmacao confirmacao) {
		this.confirmacao = confirmacao;
	}

	public void setHistoricos(List<Historico> historicos) {
		this.historicos = historicos;
	}

	public void setNomeCedenteFavorecido(String nomeCedenteFavorecido) {
		this.nomeCedenteFavorecido = nomeCedenteFavorecido;
	}

	public void setNomeSacadorVendedor(String nomeSacadorVendedor) {
		this.nomeSacadorVendedor = nomeSacadorVendedor;
	}

	public void setRetorno(Retorno retorno) {
		this.retorno = retorno;
	}

	public void setDocumentoSacador(String documentoSacador) {
		this.documentoSacador = documentoSacador;
	}

	public void setEnderecoSacadorVendedor(String enderecoSacadorVendedor) {
		this.enderecoSacadorVendedor = enderecoSacadorVendedor;
	}

	public void setCepSacadorVendedor(String cepSacadorVendedor) {
		this.cepSacadorVendedor = cepSacadorVendedor;
	}

	public void setCidadeSacadorVendedor(String cidadeSacadorVendedor) {
		this.cidadeSacadorVendedor = cidadeSacadorVendedor;
	}

	public void setUfSacadorVendedor(String ufSacadorVendedor) {
		this.ufSacadorVendedor = ufSacadorVendedor;
	}

	public void setEspecieTitulo(String especieTitulo) {
		this.especieTitulo = especieTitulo;
	}

	public void setDataEmissaoTitulo(LocalDate dataEmissaoTitulo) {
		this.dataEmissaoTitulo = dataEmissaoTitulo;
	}

	public void setDataVencimentoTitulo(LocalDate dataVencimentoTitulo) {
		this.dataVencimentoTitulo = dataVencimentoTitulo;
	}

	public void setTipoMoeda(String tipoMoeda) {
		this.tipoMoeda = tipoMoeda;
	}

	public void setValorTitulo(BigDecimal valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setPracaProtesto(String pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}

	public void setTipoEndoso(String tipoEndoso) {
		this.tipoEndoso = tipoEndoso;
	}

	public void setInformacaoSobreAceite(String informacaoSobreAceite) {
		this.informacaoSobreAceite = informacaoSobreAceite;
	}

	public void setNumeroControleDevedor(Integer numeroControleDevedor) {
		this.numeroControleDevedor = numeroControleDevedor;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setTipoIdentificacaoDevedor(String tipoIdentificacaoDevedor) {
		this.tipoIdentificacaoDevedor = tipoIdentificacaoDevedor;
	}

	public void setNumeroIdentificacaoDevedor(String numeroIdentificacaoDevedor) {
		this.numeroIdentificacaoDevedor = numeroIdentificacaoDevedor;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
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

	public void setNumeroOperacaoBanco(Integer numeroOperacaoBanco) {
		this.numeroOperacaoBanco = numeroOperacaoBanco;
	}

	public void setNumeroContratoBanco(Integer numeroContratoBanco) {
		this.numeroContratoBanco = numeroContratoBanco;
	}

	public void setNumeroParcelaContrato(Integer numeroParcelaContrato) {
		this.numeroParcelaContrato = numeroParcelaContrato;
	}

	public void setTipoLetraCambio(String tipoLetraCambio) {
		this.tipoLetraCambio = tipoLetraCambio;
	}

	public void setProtestoMotivoFalencia(String protestoMotivoFalencia) {
		this.protestoMotivoFalencia = protestoMotivoFalencia;
	}

	public void setInstrumentoProtesto(String instrumentoProtesto) {
		this.instrumentoProtesto = instrumentoProtesto;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	@Transient
	public String getChaveTitulo() {
		return this.getCodigoPortador() + getNossoNumero() + getNumeroTitulo();
	}

	@Override
	public int compareTo(TituloRemessa entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getCodigoPortador(), entidade.getCodigoPortador());
		compareToBuilder.append(this.getNossoNumero(), entidade.getNossoNumero());
		compareToBuilder.append(this.getNumeroTitulo(), entidade.getNumeroTitulo());

		return compareToBuilder.toComparison();
	}

	@Transient
	public String getSituacaoTitulo() {
		if (this.confirmacao == null) {
			this.situacaoTitulo = "S/CONFIRMAÇÃO";
		} else if (this.retorno == null) {
			this.situacaoTitulo = "EM ABERTO";
		} else {
			this.situacaoTitulo = TipoOcorrencia.getTipoOcorrencia(this.retorno.getTipoOcorrencia()).getLabel();
		}
		return situacaoTitulo;
	}

	public static TituloRemessa parseTituloVO(TituloVO tituloVO) {
		TituloRemessa titulo = new TituloConversor().converter(TituloRemessa.class, tituloVO);
		return titulo;
	}

	public void parseTituloFiliado(TituloFiliado tituloFiliado) {
		this.setAgenciaCodigoCedente(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao() + "/"
		        + tituloFiliado.getFiliado().getCodigoFiliado());
		this.setIdentificacaoRegistro(TipoRegistro.TITULO);
		this.setCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		this.setNomeCedenteFavorecido(tituloFiliado.getFiliado().getRazaoSocial());
		this.setNomeSacadorVendedor(tituloFiliado.getFiliado().getRazaoSocial());
		this.setDocumentoSacador(tituloFiliado.getFiliado().getCnpjCpf());
		this.setEnderecoSacadorVendedor(tituloFiliado.getFiliado().getEndereco());
		this.setCepSacadorVendedor(tituloFiliado.getFiliado().getCep());
		this.setUfSacadorVendedor(tituloFiliado.getFiliado().getUf());
		this.setNossoNumero(tituloFiliado.getFiliado().getCodigoFiliado() + tituloFiliado.getId());
		this.setEspecieTitulo(tituloFiliado.getEspecieTitulo().getConstante());
		this.setNumeroTitulo(tituloFiliado.getNumeroTitulo());
		this.setDataEmissaoTitulo(tituloFiliado.getDataEmissao());
		this.setDataVencimentoTitulo(tituloFiliado.getDataVencimento());
		this.setTipoMoeda("001");
		this.setValorTitulo(tituloFiliado.getValorTitulo());
		this.setSaldoTitulo(tituloFiliado.getValorSaldoTitulo());
		this.setPracaProtesto(tituloFiliado.getPracaProtesto().getNomeMunicipio());
		this.setNomeDevedor(tituloFiliado.getNomeDevedor());
		this.setDocumentoDevedor(tituloFiliado.getDocumentoDevedor());
		this.setEnderecoDevedor(tituloFiliado.getEnderecoDevedor());
		this.setTipoIdentificacaoDevedor(verificarTipoIdentificacaoDevedor(tituloFiliado.getDocumentoDevedor()));
		this.setNumeroIdentificacaoDevedor(tituloFiliado.getCpfCnpj());
		this.setCepDevedor(tituloFiliado.getCepDevedor());
		this.setCidadeDevedor(tituloFiliado.getCidadeDevedor());
		this.setUfDevedor(tituloFiliado.getUfDevedor());

	}

	private String verificarTipoIdentificacaoDevedor(String documentoDevedor2) {
		// TODO Auto-generated method stub
		return null;
	}
}
