package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Type;
import org.hibernate.bytecode.internal.javassist.FieldHandled;
import org.hibernate.bytecode.internal.javassist.FieldHandler;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Lefer
 * 
 */
@Entity
@Audited
@Table(name = "TB_TITULO")
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO")
public class TituloRemessa extends Titulo<TituloRemessa> implements FieldHandled {

	private static final long serialVersionUID = 1L;
	private int id;
	private Confirmacao confirmacao;
	private Retorno retorno;
	private RetornoCancelamento retornoCancelamento;
	private List<Anexo> anexos;
	private List<PedidoDesistencia> pedidosDesistencia;
	private List<PedidoCancelamento> pedidosCancelamento;
	private List<PedidoAutorizacaoCancelamento> pedidosAutorizacaoCancelamento;
	private String nomeCedenteFavorecido;
	private String nomeSacadorVendedor;
	private String documentoSacador;
	private String enderecoSacadorVendedor;
	private String cepSacadorVendedor;
	private String cidadeSacadorVendedor;
	private String ufSacadorVendedor;
	private String especieTitulo;
	private LocalDate dataEmissaoTitulo;
	private LocalDate dataVencimentoTitulo;
	private String tipoMoeda;
	private BigDecimal valorTitulo;
	private String pracaProtesto;
	private String tipoEndoso;
	private String informacaoSobreAceite;
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
	private Date dataCadastro;
	private FieldHandler handler;

	@Override
	@Id
	@Column(name = "ID_TITULO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne(optional = true, mappedBy = "titulo", fetch = FetchType.LAZY)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public Confirmacao getConfirmacao() {
		if (this.handler != null) {
			return (Confirmacao) this.handler.readObject(this, "confirmacao", confirmacao);
		}
		return confirmacao;
	}

	@OneToOne(optional = true, mappedBy = "titulo", fetch = FetchType.LAZY)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public Retorno getRetorno() {
		if (this.handler != null) {
			return (Retorno) this.handler.readObject(this, "retorno", retorno);
		}
		return retorno;
	}

    @OneToOne(optional = true, mappedBy = "titulo", fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    public RetornoCancelamento getRetornoCancelamento() {
        if (this.handler != null) {
            return (RetornoCancelamento) this.handler.readObject(this, "retornoCancelamento", retornoCancelamento);
        }
        return retornoCancelamento;
    }

    @OneToMany(mappedBy = "titulo", fetch = FetchType.LAZY)
	public List<Anexo> getAnexos() {
		return anexos;
	}

	@OneToMany(mappedBy = "titulo", fetch = FetchType.LAZY)
	public List<PedidoDesistencia> getPedidosDesistencia() {
		return pedidosDesistencia;
	}

	@OneToMany(mappedBy = "titulo", fetch = FetchType.LAZY)
	public List<PedidoCancelamento> getPedidosCancelamento() {
		return pedidosCancelamento;
	}

	@OneToMany(mappedBy = "titulo", fetch = FetchType.LAZY)
	public List<PedidoAutorizacaoCancelamento> getPedidosAutorizacaoCancelamento() {
		return pedidosAutorizacaoCancelamento;
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
		if (tipoEndoso == null || StringUtils.isBlank(tipoEndoso.trim())) {
			tipoEndoso = "M";
		}
		return tipoEndoso;
	}

	@Column(name = "INFORMACAO_SOBRE_ACEITE")
	public String getInformacaoSobreAceite() {
		return informacaoSobreAceite;
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

	@Column(name = "COMPLEMENTO_REGISTRO", length = 255)
	public String getComplementoRegistro() {
		if (complementoRegistro != null) {
			if (complementoRegistro.length() > 19) {
				complementoRegistro = StringUtils.EMPTY;
			}
		}
		return complementoRegistro;
	}

	@Column(name = "DATA_CADASTRO")
	@Type(type = "date")
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setConfirmacao(Confirmacao confirmacao) {
		if (this.handler != null) {
			this.confirmacao = (Confirmacao) this.handler.writeObject(this, "confirmacao", this.confirmacao, confirmacao);
		}
		this.confirmacao = confirmacao;
	}

	public void setPedidosDesistencia(List<PedidoDesistencia> pedidosDesistencia) {
		this.pedidosDesistencia = pedidosDesistencia;
	}

	public void setPedidosCancelamento(List<PedidoCancelamento> pedidosCancelamento) {
		this.pedidosCancelamento = pedidosCancelamento;
	}

	public void setPedidosAutorizacaoCancelamento(List<PedidoAutorizacaoCancelamento> pedidosAutorizacaoCancelamento) {
		this.pedidosAutorizacaoCancelamento = pedidosAutorizacaoCancelamento;
	}

	public void setNomeCedenteFavorecido(String nomeCedenteFavorecido) {
		this.nomeCedenteFavorecido = nomeCedenteFavorecido;
	}

	public void setNomeSacadorVendedor(String nomeSacadorVendedor) {
		this.nomeSacadorVendedor = nomeSacadorVendedor;
	}

	public void setRetorno(Retorno retorno) {
		if (this.handler != null) {
			this.retorno = (Retorno) this.handler.writeObject(this, "retorno", this.retorno, retorno);
		}
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

    public void setAnexos(List<Anexo> anexos) {
        this.anexos = anexos;
    }

    public void setRetornoCancelamento(RetornoCancelamento retornoCancelamento) {
        this.retornoCancelamento = retornoCancelamento;
    }

	@Transient
	public String getSituacaoTitulo() {
        String situacaoTitulo = "ABERTO";

		if (this.confirmacao == null) {
            situacaoTitulo = "S/CONFIRMAÇÃO";
		} else if (this.retorno == null) {
			if (this.confirmacao.getTipoOcorrencia() != null) {
				if (StringUtils.isBlank(this.confirmacao.getTipoOcorrencia().trim())) {
                    situacaoTitulo = "ABERTO";
				}
				if (this.confirmacao.getNumeroProtocoloCartorio() != null) {
					if (StringUtils.isNotBlank(this.confirmacao.getNumeroProtocoloCartorio().trim())) {
						Integer protocolo = Integer.valueOf(this.confirmacao.getNumeroProtocoloCartorio().trim());
						if (protocolo == 0) {
                            situacaoTitulo = TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getLabel();
						}
					} else if (StringUtils.isEmpty(this.confirmacao.getNumeroProtocoloCartorio().trim())
							|| StringUtils.isBlank(this.confirmacao.getNumeroProtocoloCartorio().trim())) {
						return situacaoTitulo = TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getLabel();
					}
				}
			} else {
                situacaoTitulo = TipoOcorrencia.get(this.confirmacao.getTipoOcorrencia()).getLabel();
			}
		}
		if (this.retorno != null) {
            situacaoTitulo = TipoOcorrencia.get(this.retorno.getTipoOcorrencia()).getLabel();
		}
        if (this.retornoCancelamento != null) {
            situacaoTitulo = TipoOcorrencia.PROTESTO_DO_BANCO_CANCELADO.getLabel();
        }
		return situacaoTitulo;
	}

	public void parseTituloFiliado(TituloFiliado tituloFiliado) {
		this.setIdentificacaoRegistro(TipoIdentificacaoRegistro.TITULO);
		this.setAgenciaCodigoCedente(tituloFiliado.getFiliado().getCodigoFiliado());
		this.setCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		this.setNomeCedenteFavorecido(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getRazaoSocial()));
		this.setNomeSacadorVendedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getRazaoSocial()));
		this.setDocumentoSacador(tituloFiliado.getFiliado().getCnpjCpf());
		this.setEnderecoSacadorVendedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getEndereco()));
		this.setCepSacadorVendedor(tituloFiliado.getFiliado().getCep());
		this.setCidadeSacadorVendedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getMunicipio().getNomeMunicipio().toUpperCase()));
		this.setUfSacadorVendedor(tituloFiliado.getFiliado().getUf());
		this.setNossoNumero(gerarNossoNumero(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao(), tituloFiliado.getId()));
		this.setEspecieTitulo(tituloFiliado.getEspecieTitulo().getConstante());
		this.setNumeroTitulo(tituloFiliado.getNumeroTitulo());
		this.setDataEmissaoTitulo(new LocalDate(tituloFiliado.getDataEmissao()));
		this.setDataVencimentoTitulo(new LocalDate(tituloFiliado.getDataVencimento()));
		this.setTipoMoeda("001");
		this.setValorTitulo(tituloFiliado.getValorTitulo());
		this.setSaldoTitulo(tituloFiliado.getValorSaldoTitulo());
		this.setPracaProtesto(RemoverAcentosUtil.removeAcentos(tituloFiliado.getPracaProtesto().getNomeMunicipio().toUpperCase()));
		this.setTipoEndoso("M");
		this.setInformacaoSobreAceite("N");
		this.setNumeroControleDevedor(1);
		this.setNomeDevedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getNomeDevedor()));
		this.setDocumentoDevedor(tituloFiliado.getDocumentoDevedor());
		this.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getEnderecoDevedor()));
		this.setTipoIdentificacaoDevedor(getTIpoIdentificacao(tituloFiliado.getCpfCnpj()));
		this.setNumeroIdentificacaoDevedor(StringUtils.leftPad(tituloFiliado.getCpfCnpj(), 14, "0"));
		this.setCepDevedor(tituloFiliado.getCepDevedor());
		this.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getCidadeDevedor()));
		this.setBairroDevedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getBairroDevedor()));
		this.setUfDevedor(tituloFiliado.getUfDevedor());
		this.setComplementoRegistro(buscarAlineaCheque(tituloFiliado));
	}

	public void parseAvalista(TituloFiliado tituloFiliado, Avalista avalista) {
		this.setIdentificacaoRegistro(TipoIdentificacaoRegistro.TITULO);
		this.setAgenciaCodigoCedente(tituloFiliado.getFiliado().getCodigoFiliado());
		this.setCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		this.setNomeCedenteFavorecido(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getRazaoSocial()));
		this.setNomeSacadorVendedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getRazaoSocial()));
		this.setDocumentoSacador(tituloFiliado.getFiliado().getCnpjCpf());
		this.setEnderecoSacadorVendedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getEndereco()));
		this.setCepSacadorVendedor(tituloFiliado.getFiliado().getCep());
		this.setCidadeSacadorVendedor(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getMunicipio().getNomeMunicipio().toUpperCase()));
		this.setUfSacadorVendedor(tituloFiliado.getFiliado().getUf());
		this.setNossoNumero(gerarNossoNumero(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao(), tituloFiliado.getId()));
		this.setEspecieTitulo(tituloFiliado.getEspecieTitulo().getConstante());
		this.setNumeroTitulo(tituloFiliado.getNumeroTitulo());
		this.setDataEmissaoTitulo(new LocalDate(tituloFiliado.getDataEmissao()));
		this.setDataVencimentoTitulo(new LocalDate(tituloFiliado.getDataVencimento()));
		this.setTipoMoeda("001");
		this.setValorTitulo(tituloFiliado.getValorTitulo());
		this.setSaldoTitulo(tituloFiliado.getValorSaldoTitulo());
		this.setPracaProtesto(RemoverAcentosUtil.removeAcentos(tituloFiliado.getPracaProtesto().getNomeMunicipio().toUpperCase()));
		this.setTipoEndoso("M");
		this.setInformacaoSobreAceite("N");
		this.setNomeDevedor(RemoverAcentosUtil.removeAcentos(avalista.getNome()));
		this.setDocumentoDevedor(tituloFiliado.getDocumentoDevedor());
		this.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(avalista.getEndereco()));
		this.setTipoIdentificacaoDevedor(getTIpoIdentificacao(avalista.getDocumento()));
		this.setNumeroIdentificacaoDevedor(StringUtils.leftPad(avalista.getDocumento(), 14, "0"));
		this.setCepDevedor(avalista.getCep());
		this.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(avalista.getCidade()));
		this.setBairroDevedor(RemoverAcentosUtil.removeAcentos(avalista.getBairro()));
		this.setUfDevedor(avalista.getUf());
		this.setComplementoRegistro(buscarAlineaCheque(tituloFiliado));
	}

	private String gerarNossoNumero(String codigoPortador, int idTituloFiliado) {
		return codigoPortador + StringUtils.leftPad(Integer.toString(idTituloFiliado), 12, "0");
	}

	private String buscarAlineaCheque(TituloFiliado tituloFiliado) {
		if (tituloFiliado.getAlinea() != null) {
			return tituloFiliado.getAlinea().getConstante();
		}
		return StringUtils.EMPTY;
	}

    @Override
    public int compareTo(TituloRemessa entidade) {
        CompareToBuilder compareToBuilder = new CompareToBuilder();
        compareToBuilder.append(this.getCodigoPortador(), entidade.getCodigoPortador());
        compareToBuilder.append(this.getNossoNumero(), entidade.getNossoNumero());
        compareToBuilder.append(this.getNumeroTitulo(), entidade.getNumeroTitulo());
        return compareToBuilder.toComparison();
    }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TituloRemessa) {
			TituloRemessa modalidade = TituloRemessa.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getNomeDevedor(), modalidade.getNomeDevedor());
			equalsBuilder.append(this.getNossoNumero(), modalidade.getNossoNumero());
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

    /**
     * Utilitario para verificação do tipo de identificação, irá retornar 001 para CNPJ e 002 para CPF
     *
     * @param documento
     * @return String
     */
    @Transient
    public static String getTIpoIdentificacao(String documento) {
        if (documento.length() <= 11) {
            return ConfiguracaoBase.TIPO_CPF;
        }
        return ConfiguracaoBase.TIPO_CNPJ;
    }

    /**
     * Método para verificar se o título é de um devedor principal ou de outro
     * devedor.
     * @return boolean
     */
    @Transient
	public boolean isDevedorPrincipal() {
		Integer numeroDevedor = 0;
		if (this.getNumeroControleDevedor() != null) {
			if (StringUtils.isNotBlank(Integer.toString(this.getNumeroControleDevedor()))) {
				numeroDevedor = this.getNumeroControleDevedor();
			}
		}
		if (numeroDevedor > 1) {
			return false;
		}
		return true;
	}

	@Override
	public void setFieldHandler(FieldHandler handler) {
		this.handler = handler;
	}

	@Override
	public FieldHandler getFieldHandler() {
		return this.handler;
	}
}