package br.com.ieptbto.cra.entidade.view;

import org.joda.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Table(name="VIEW_TITULO")
@NamedNativeQueries({
	    @NamedNativeQuery(name="findTitulosPorIdRemessaRemessa",
			query="select * from view_titulo where id_remessa_remessa= :id order by nomeDevedor_TituloRemessa", resultClass=ViewTitulo.class),
	    @NamedNativeQuery(name="findTitulosPorIdRemessaConfirmacao",
			query="select * from view_titulo where id_remessa_confirmacao= :id order by nomeDevedor_TituloRemessa", resultClass=ViewTitulo.class),
	    @NamedNativeQuery(name="findTitulosPorIdRemessaRetorno",
			query="select * from view_titulo where id_remessa_retorno = :id union " +
                    "select * from view_titulo where id_remessa_cancelamento = :id_can order by nomeDevedor_TituloRemessa", resultClass=ViewTitulo.class),
})
public class ViewTitulo implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id_TituloRemessa;
	private String nossoNumero_TituloRemessa;
	private String numeroTitulo_TituloRemessa;
	private Integer numeroControleDevedor_TituloRemessa;
	private String nomeDevedor_TituloRemessa;
	private String numeroIdentificacaoDevedor_TituloRemessa;
	private String nomeSacadorVendedor_TituloRemessa;
	private String documentoSacador_TituloRemessa;
	private BigDecimal saldoTitulo_TituloRemessa;
	private String nomeArquivo_Arquivo_Remessa;
	private Date dataRecebimento_Arquivo_Remessa;
	private LocalDate dataEnvio_Arquivo_Remessa;
	private Integer id_Arquivo_Remessa;
	private Integer id_Remessa_Remessa;
	private Integer id_Instituicao_Instituicao;
	private String nomeFantasia_Instituicao;
	private String codigoCompensacao_Instituicao;
	private String tipoInstituicao_Instituicao;
	private Integer id_Confirmacao;
	private String numeroProtocoloCartorio_Confirmacao;
	private String nomeArquivo_Arquivo_Confirmacao;
	private Integer id_Arquivo_Confirmacao;
	private Date dataRecebimento_Arquivo_Confirmacao;
	private LocalDate dataEnvio_Arquivo_Confirmacao;
	private String nomeArquivo_Arquivo_Confirmacao_Instituicao;
	private Integer id_Arquivo_Confirmacao_Instituicao;
	private Date dataRecebimento_Arquivo_Confirmacao_Instituicao;
	private Integer id_Remessa_Confirmacao;
	private Integer id_Retorno;
	private String nomeArquivo_Arquivo_Retorno;
	private Integer id_Arquivo_Retorno;
	private Date dataRecebimento_Arquivo_Retorno;
	private LocalDate dataEnvio_Arquivo_Retorno;
	private String nomeArquivo_Arquivo_Retorno_Instituicao;
	private Integer id_Arquivo_Retorno_Instituicao;
	private Date dataRecebimento_Arquivo_Retorno_Instituicao;
	private Integer id_Remessa_Retorno;
	private LocalDate dataOcorrencia_ConfirmacaoRetorno;
    private Integer id_Titulo_Retorno_Cancelamento;
    private Integer id_Remessa_Cancelamento;
	private Integer id_Instituicao_Cartorio;
	private Integer id_Municipio;
	private String nomeMunicipio_Municipio;
	private String codigoIbge_Municipio;
	private String situacaoTitulo;

	@Id
	public Integer getId_TituloRemessa() {
		return id_TituloRemessa;
	}

	public String getNossoNumero_TituloRemessa() {
		return nossoNumero_TituloRemessa;
	}

	public String getNumeroTitulo_TituloRemessa() {
		return numeroTitulo_TituloRemessa;
	}

	public Integer getNumeroControleDevedor_TituloRemessa() {
		if (numeroControleDevedor_TituloRemessa == null) {
			this.numeroControleDevedor_TituloRemessa = 1;
		}
		return numeroControleDevedor_TituloRemessa;
	}

	public String getNomeDevedor_TituloRemessa() {
		return nomeDevedor_TituloRemessa;
	}

	public String getNumeroIdentificacaoDevedor_TituloRemessa() {
		return numeroIdentificacaoDevedor_TituloRemessa;
	}

	public String getNomeSacadorVendedor_TituloRemessa() {
		return nomeSacadorVendedor_TituloRemessa;
	}

	public String getDocumentoSacador_TituloRemessa() {
		return documentoSacador_TituloRemessa;
	}

	public BigDecimal getSaldoTitulo_TituloRemessa() {
		return saldoTitulo_TituloRemessa;
	}

	public String getNomeArquivo_Arquivo_Remessa() {
		return nomeArquivo_Arquivo_Remessa;
	}

	public Date getDataRecebimento_Arquivo_Remessa() {
		return dataRecebimento_Arquivo_Remessa;
	}

	public LocalDate getDataEnvio_Arquivo_Remessa() {
		return dataEnvio_Arquivo_Remessa;
	}

	public Integer getId_Arquivo_Remessa() {
		return id_Arquivo_Remessa;
	}

	public Integer getId_Remessa_Remessa() {
		return id_Remessa_Remessa;
	}

	public Integer getId_Instituicao_Instituicao() {
		return id_Instituicao_Instituicao;
	}

	public String getNomeFantasia_Instituicao() {
		return nomeFantasia_Instituicao;
	}

	public String getCodigoCompensacao_Instituicao() {
		return codigoCompensacao_Instituicao;
	}

	public String getTipoInstituicao_Instituicao() {
		return tipoInstituicao_Instituicao;
	}

	public Integer getId_Confirmacao() {
		return id_Confirmacao;
	}

	public String getNumeroProtocoloCartorio_Confirmacao() {
		return numeroProtocoloCartorio_Confirmacao;
	}

	public String getNomeArquivo_Arquivo_Confirmacao() {
		return nomeArquivo_Arquivo_Confirmacao;
	}

	public Integer getId_Arquivo_Confirmacao() {
		return id_Arquivo_Confirmacao;
	}

	public Date getDataRecebimento_Arquivo_Confirmacao() {
		return dataRecebimento_Arquivo_Confirmacao;
	}

	public LocalDate getDataEnvio_Arquivo_Confirmacao() {
		return dataEnvio_Arquivo_Confirmacao;
	}

	public String getNomeArquivo_Arquivo_Confirmacao_Instituicao() {
		return nomeArquivo_Arquivo_Confirmacao_Instituicao;
	}

	public Integer getId_Arquivo_Confirmacao_Instituicao() {
		return id_Arquivo_Confirmacao_Instituicao;
	}

	public Date getDataRecebimento_Arquivo_Confirmacao_Instituicao() {
		return dataRecebimento_Arquivo_Confirmacao_Instituicao;
	}

	public Integer getId_Remessa_Confirmacao() {
		return id_Remessa_Confirmacao;
	}

	public Integer getId_Retorno() {
		return id_Retorno;
	}

	public String getNomeArquivo_Arquivo_Retorno() {
		return nomeArquivo_Arquivo_Retorno;
	}

	public Integer getId_Arquivo_Retorno() {
		return id_Arquivo_Retorno;
	}

	public Date getDataRecebimento_Arquivo_Retorno() {
		return dataRecebimento_Arquivo_Retorno;
	}

	public LocalDate getDataEnvio_Arquivo_Retorno() {
		return dataEnvio_Arquivo_Retorno;
	}

	public String getNomeArquivo_Arquivo_Retorno_Instituicao() {
		return nomeArquivo_Arquivo_Retorno_Instituicao;
	}

	public Integer getId_Arquivo_Retorno_Instituicao() {
		return id_Arquivo_Retorno_Instituicao;
	}

	public Date getDataRecebimento_Arquivo_Retorno_Instituicao() {
		return dataRecebimento_Arquivo_Retorno_Instituicao;
	}

	public Integer getId_Remessa_Retorno() {
		return id_Remessa_Retorno;
	}

	public LocalDate getDataOcorrencia_ConfirmacaoRetorno() {
		return dataOcorrencia_ConfirmacaoRetorno;
	}

	public Integer getId_Instituicao_Cartorio() {
		return id_Instituicao_Cartorio;
	}

	public Integer getId_Municipio() {
		return id_Municipio;
	}

	public String getNomeMunicipio_Municipio() {
		return nomeMunicipio_Municipio;
	}

	public String getCodigoIbge_Municipio() {
		return codigoIbge_Municipio;
	}

	public String getSituacaoTitulo() {
		return situacaoTitulo;
	}

	public void setId_TituloRemessa(Integer id_TituloRemessa) {
		this.id_TituloRemessa = id_TituloRemessa;
	}

	public void setNossoNumero_TituloRemessa(String nossoNumero_TituloRemessa) {
		this.nossoNumero_TituloRemessa = nossoNumero_TituloRemessa;
	}

	public void setNumeroTitulo_TituloRemessa(String numeroTitulo_TituloRemessa) {
		this.numeroTitulo_TituloRemessa = numeroTitulo_TituloRemessa;
	}

	public void setNumeroControleDevedor_TituloRemessa(Integer numeroControleDevedor_TituloRemessa) {
		this.numeroControleDevedor_TituloRemessa = numeroControleDevedor_TituloRemessa;
	}

	public void setNomeDevedor_TituloRemessa(String nomeDevedor_TituloRemessa) {
		this.nomeDevedor_TituloRemessa = nomeDevedor_TituloRemessa;
	}

	public void setNumeroIdentificacaoDevedor_TituloRemessa(String numeroIdentificacaoDevedor_TituloRemessa) {
		this.numeroIdentificacaoDevedor_TituloRemessa = numeroIdentificacaoDevedor_TituloRemessa;
	}

	public void setNomeSacadorVendedor_TituloRemessa(String nomeSacadorVendedor_TituloRemessa) {
		this.nomeSacadorVendedor_TituloRemessa = nomeSacadorVendedor_TituloRemessa;
	}

	public void setDocumentoSacador_TituloRemessa(String documentoSacador_TituloRemessa) {
		this.documentoSacador_TituloRemessa = documentoSacador_TituloRemessa;
	}

	public void setSaldoTitulo_TituloRemessa(BigDecimal saldoTitulo_TituloRemessa) {
		this.saldoTitulo_TituloRemessa = saldoTitulo_TituloRemessa;
	}

	public void setNomeArquivo_Arquivo_Remessa(String nomeArquivo_Arquivo_Remessa) {
		this.nomeArquivo_Arquivo_Remessa = nomeArquivo_Arquivo_Remessa;
	}

	public void setDataRecebimento_Arquivo_Remessa(Date dataRecebimento_Arquivo_Remessa) {
		this.dataRecebimento_Arquivo_Remessa = dataRecebimento_Arquivo_Remessa;
	}

	public void setDataEnvio_Arquivo_Remessa(LocalDate dataEnvio_Arquivo_Remessa) {
		this.dataEnvio_Arquivo_Remessa = dataEnvio_Arquivo_Remessa;
	}

	public void setId_Arquivo_Remessa(Integer id_Arquivo_Remessa) {
		this.id_Arquivo_Remessa = id_Arquivo_Remessa;
	}

	public void setId_Remessa_Remessa(Integer id_Remessa_Remessa) {
		this.id_Remessa_Remessa = id_Remessa_Remessa;
	}

	public void setId_Instituicao_Instituicao(Integer id_Instituicao_Instituicao) {
		this.id_Instituicao_Instituicao = id_Instituicao_Instituicao;
	}

	public void setNomeFantasia_Instituicao(String nomeFantasia_Instituicao) {
		this.nomeFantasia_Instituicao = nomeFantasia_Instituicao;
	}

	public void setCodigoCompensacao_Instituicao(String codigoCompensacao_Instituicao) {
		this.codigoCompensacao_Instituicao = codigoCompensacao_Instituicao;
	}

	public void setTipoInstituicao_Instituicao(String tipoInstituicao_Instituicao) {
		this.tipoInstituicao_Instituicao = tipoInstituicao_Instituicao;
	}

	public void setId_Confirmacao(Integer id_Confirmacao) {
		this.id_Confirmacao = id_Confirmacao;
	}

	public void setNumeroProtocoloCartorio_Confirmacao(String numeroProtocoloCartorio_Confirmacao) {
		this.numeroProtocoloCartorio_Confirmacao = numeroProtocoloCartorio_Confirmacao;
	}

	public void setNomeArquivo_Arquivo_Confirmacao(String nomeArquivo_Arquivo_Confirmacao) {
		this.nomeArquivo_Arquivo_Confirmacao = nomeArquivo_Arquivo_Confirmacao;
	}

	public void setId_Arquivo_Confirmacao(Integer id_Arquivo_Confirmacao) {
		this.id_Arquivo_Confirmacao = id_Arquivo_Confirmacao;
	}

	public void setDataRecebimento_Arquivo_Confirmacao(Date dataRecebimento_Arquivo_Confirmacao) {
		this.dataRecebimento_Arquivo_Confirmacao = dataRecebimento_Arquivo_Confirmacao;
	}

	public void setDataEnvio_Arquivo_Confirmacao(LocalDate dataEnvio_Arquivo_Confirmacao) {
		this.dataEnvio_Arquivo_Confirmacao = dataEnvio_Arquivo_Confirmacao;
	}

	public void setNomeArquivo_Arquivo_Confirmacao_Instituicao(String nomeArquivo_Arquivo_Confirmacao_Instituicao) {
		this.nomeArquivo_Arquivo_Confirmacao_Instituicao = nomeArquivo_Arquivo_Confirmacao_Instituicao;
	}

	public void setId_Arquivo_Confirmacao_Instituicao(Integer id_Arquivo_Confirmacao_Instituicao) {
		this.id_Arquivo_Confirmacao_Instituicao = id_Arquivo_Confirmacao_Instituicao;
	}

	public void setDataRecebimento_Arquivo_Confirmacao_Instituicao(Date dataRecebimento_Arquivo_Confirmacao_Instituicao) {
		this.dataRecebimento_Arquivo_Confirmacao_Instituicao = dataRecebimento_Arquivo_Confirmacao_Instituicao;
	}

	public void setId_Remessa_Confirmacao(Integer id_Remessa_Confirmacao) {
		this.id_Remessa_Confirmacao = id_Remessa_Confirmacao;
	}

	public void setId_Retorno(Integer id_Retorno) {
		this.id_Retorno = id_Retorno;
	}

	public void setNomeArquivo_Arquivo_Retorno(String nomeArquivo_Arquivo_Retorno) {
		this.nomeArquivo_Arquivo_Retorno = nomeArquivo_Arquivo_Retorno;
	}

	public void setId_Arquivo_Retorno(Integer id_Arquivo_Retorno) {
		this.id_Arquivo_Retorno = id_Arquivo_Retorno;
	}

	public void setDataRecebimento_Arquivo_Retorno(Date dataRecebimento_Arquivo_Retorno) {
		this.dataRecebimento_Arquivo_Retorno = dataRecebimento_Arquivo_Retorno;
	}

	public void setDataEnvio_Arquivo_Retorno(LocalDate dataEnvio_Arquivo_Retorno) {
		this.dataEnvio_Arquivo_Retorno = dataEnvio_Arquivo_Retorno;
	}

	public void setNomeArquivo_Arquivo_Retorno_Instituicao(String nomeArquivo_Arquivo_Retorno_Instituicao) {
		this.nomeArquivo_Arquivo_Retorno_Instituicao = nomeArquivo_Arquivo_Retorno_Instituicao;
	}

	public void setId_Arquivo_Retorno_Instituicao(Integer id_Arquivo_Retorno_Instituicao) {
		this.id_Arquivo_Retorno_Instituicao = id_Arquivo_Retorno_Instituicao;
	}

	public void setDataRecebimento_Arquivo_Retorno_Instituicao(Date dataRecebimento_Arquivo_Retorno_Instituicao) {
		this.dataRecebimento_Arquivo_Retorno_Instituicao = dataRecebimento_Arquivo_Retorno_Instituicao;
	}

	public void setId_Remessa_Retorno(Integer id_Remessa_Retorno) {
		this.id_Remessa_Retorno = id_Remessa_Retorno;
	}

	public void setDataOcorrencia_ConfirmacaoRetorno(LocalDate dataOcorrencia_ConfirmacaoRetorno) {
		this.dataOcorrencia_ConfirmacaoRetorno = dataOcorrencia_ConfirmacaoRetorno;
	}

	public void setId_Instituicao_Cartorio(Integer id_Instituicao_Cartorio) {
		this.id_Instituicao_Cartorio = id_Instituicao_Cartorio;
	}

	public void setId_Municipio(Integer id_Municipio) {
		this.id_Municipio = id_Municipio;
	}

	public void setNomeMunicipio_Municipio(String nomeMunicipio_Municipio) {
		this.nomeMunicipio_Municipio = nomeMunicipio_Municipio;
	}

	public void setCodigoIbge_Municipio(String codigoIbge_Municipio) {
		this.codigoIbge_Municipio = codigoIbge_Municipio;
	}

	public void setSituacaoTitulo(String situacaoTitulo) {
		this.situacaoTitulo = situacaoTitulo;
	}

    public Integer getId_Titulo_Retorno_Cancelamento() {
        return id_Titulo_Retorno_Cancelamento;
    }

    public void setId_Titulo_Retorno_Cancelamento(Integer id_Titulo_Retorno_Cancelamento) {
        this.id_Titulo_Retorno_Cancelamento = id_Titulo_Retorno_Cancelamento;
    }

    public Integer getId_Remessa_Cancelamento() {
        return id_Remessa_Cancelamento;
    }

    public void setId_Remessa_Cancelamento(Integer id_Remessa_Cancelamento) {
        this.id_Remessa_Cancelamento = id_Remessa_Cancelamento;
    }
}