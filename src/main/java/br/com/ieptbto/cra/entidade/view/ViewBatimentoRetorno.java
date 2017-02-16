package br.com.ieptbto.cra.entidade.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.entidade.Deposito;

@Entity
@Table(name="VIEW_BATIMENTO_RETORNO")
@NamedNativeQueries({
		@NamedNativeQuery(name = "findAllRetornoNaoConfirmados", 
					query = "select * from view_batimento_retorno where situacaobatimento_remessa='NAO_CONFIRMADO'", resultClass = ViewBatimentoRetorno.class),
		@NamedNativeQuery(name = "findAllRetornoAguardandoLiberacao", 
					query = "select * from view_batimento_retorno where situacaobatimento_remessa='AGUARDANDO_LIBERACAO'", resultClass = ViewBatimentoRetorno.class),
		@NamedNativeQuery(name = "findAllRetornoConfirmados", 
					query = "select * from view_batimento_retorno where situacaobatimento_remessa='CONFIRMADO'", resultClass = ViewBatimentoRetorno.class)})
public class ViewBatimentoRetorno implements Serializable {
	
	/***/
	private static final long serialVersionUID = 1L;
	@Id
	private Integer idArquivo_Arquivo;
	private String nomeArquivo_Arquivo;
	private LocalDate dataEnvio_Arquivo;
	private LocalTime horaEnvio_Arquivo;
	private Date dataRecebimento_Arquivo;
	private Integer idRemessa_Remessa;
	private String situacaoBatimento_Remessa;
	private Integer idInstituicao_Cartorio;
	private String nomeFantasia_Cartorio;
	private Integer idMunicipio_Municipio;
	private String nomeMunicipio_Municipio;
	private String cod_ibge_Municipio;
	private Integer idInstituicao_Instituicao;
	private String tipoBatimento_Instituicao;
	private String nomeFantasia_Instituicao;
	private String codigoCompensacao_Instituicao;
	private BigDecimal totalValorlPagos;
	private BigDecimal totalCustasCartorio;
	private BigDecimal totalDemaisDespesas;

	public Integer getIdArquivo_Arquivo() {
		return idArquivo_Arquivo;
	}

	public String getNomeArquivo_Arquivo() {
		return nomeArquivo_Arquivo;
	}

	public LocalDate getDataEnvio_Arquivo() {
		return dataEnvio_Arquivo;
	}

	public LocalTime getHoraEnvio_Arquivo() {
		return horaEnvio_Arquivo;
	}

	public Date getDataRecebimento_Arquivo() {
		return dataRecebimento_Arquivo;
	}

	public Integer getIdRemessa_Remessa() {
		return idRemessa_Remessa;
	}

	public Integer getIdInstituicao_Cartorio() {
		return idInstituicao_Cartorio;
	}

	public String getNomeFantasia_Cartorio() {
		return nomeFantasia_Cartorio;
	}

	public Integer getIdMunicipio_Municipio() {
		return idMunicipio_Municipio;
	}

	public String getNomeMunicipio_Municipio() {
		return nomeMunicipio_Municipio;
	}

	public String getCod_ibge_Municipio() {
		return cod_ibge_Municipio;
	}

	public Integer getIdInstituicao_Instituicao() {
		return idInstituicao_Instituicao;
	}

	public String getNomeFantasia_Instituicao() {
		return nomeFantasia_Instituicao;
	}
	
	public String getTipoBatimento_Instituicao() {
		return tipoBatimento_Instituicao;
	}

	public String getCodigoCompensacao_Instituicao() {
		return codigoCompensacao_Instituicao;
	}
	
	public String getSituacaoBatimento_Remessa() {
		return situacaoBatimento_Remessa;
	}

	public BigDecimal getTotalValorlPagos() {
		return totalValorlPagos;
	}

	public BigDecimal getTotalCustasCartorio() {
		return totalCustasCartorio;
	}

	public BigDecimal getTotalDemaisDespesas() {
		return totalDemaisDespesas;
	}

	public void setIdArquivo_Arquivo(Integer idArquivo_Arquivo) {
		this.idArquivo_Arquivo = idArquivo_Arquivo;
	}

	public void setNomeArquivo_Arquivo(String nomeArquivo_Arquivo) {
		this.nomeArquivo_Arquivo = nomeArquivo_Arquivo;
	}

	public void setDataEnvio_Arquivo(LocalDate dataEnvio_Arquivo) {
		this.dataEnvio_Arquivo = dataEnvio_Arquivo;
	}

	public void setHoraEnvio_Arquivo(LocalTime horaEnvio_Arquivo) {
		this.horaEnvio_Arquivo = horaEnvio_Arquivo;
	}

	public void setDataRecebimento_Arquivo(Date dataRecebimento_Arquivo) {
		this.dataRecebimento_Arquivo = dataRecebimento_Arquivo;
	}
	
	public void setSituacaoBatimento_Remessa(String situacaoBatimento_Remessa) {
		this.situacaoBatimento_Remessa = situacaoBatimento_Remessa;
	}

	public void setIdRemessa_Remessa(Integer idRemessa_Remessa) {
		this.idRemessa_Remessa = idRemessa_Remessa;
	}

	public void setIdInstituicao_Cartorio(Integer idInstituicao_Cartorio) {
		this.idInstituicao_Cartorio = idInstituicao_Cartorio;
	}

	public void setNomeFantasia_Cartorio(String nomeFantasia_Cartorio) {
		this.nomeFantasia_Cartorio = nomeFantasia_Cartorio;
	}

	public void setIdMunicipio_Municipio(Integer idMunicipio_Municipio) {
		this.idMunicipio_Municipio = idMunicipio_Municipio;
	}

	public void setNomeMunicipio_Municipio(String nomeMunicipio_Municipio) {
		this.nomeMunicipio_Municipio = nomeMunicipio_Municipio;
	}

	public void setCod_ibge_Municipio(String cod_ibge_Municipio) {
		this.cod_ibge_Municipio = cod_ibge_Municipio;
	}

	public void setIdInstituicao_Instituicao(Integer idInstituicao_Instituicao) {
		this.idInstituicao_Instituicao = idInstituicao_Instituicao;
	}

	public void setNomeFantasia_Instituicao(String nomeFantasia_Instituicao) {
		this.nomeFantasia_Instituicao = nomeFantasia_Instituicao;
	}

	public void setCodigoCompensacao_Instituicao(String codigoCompensacao_Instituicao) {
		this.codigoCompensacao_Instituicao = codigoCompensacao_Instituicao;
	}

	public void setTipoBatimento_Instituicao(String tipoBatimento_Instituicao) {
		this.tipoBatimento_Instituicao = tipoBatimento_Instituicao;
	}
	
	public void setTotalValorlPagos(BigDecimal totalValorlPagos) {
		this.totalValorlPagos = totalValorlPagos;
	}

	public void setTotalCustasCartorio(BigDecimal totalCustasCartorio) {
		this.totalCustasCartorio = totalCustasCartorio;
	}

	public void setTotalDemaisDespesas(BigDecimal totalDemaisDespesas) {
		this.totalDemaisDespesas = totalDemaisDespesas;
	}

	@Transient
	private ArrayList<Deposito> listaDepositos;

	public ArrayList<Deposito> getListaDepositos() {
		return listaDepositos;
	}

	public void setListaDepositos(ArrayList<Deposito> listaDepositos) {
		this.listaDepositos = listaDepositos;
	}
}