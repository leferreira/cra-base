package br.com.ieptbto.cra.entidade.view;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

@Entity
@Table(name="VIEW_AUTORIZACAO_PENDENTE")
@NamedNativeQueries({
	@NamedNativeQuery(name="findAutorizacoesPendentes", 
			query="select * from view_autorizacao_pendente order by idautorizacao_autorizacaocancelamento asc", resultClass=AutorizacaoPendente.class),
	@NamedNativeQuery(name="findAutorizacoesPendentesCartorio", 
			query="select * from view_autorizacao_pendente where idinstituicao_cartorio= :id order by idautorizacao_autorizacaocancelamento asc", 
			resultClass=AutorizacaoPendente.class),
	@NamedNativeQuery(name="findAutorizacoesPendentesInstituicao", 
			query="select * from view_autorizacao_pendente where idinstituicao_instituicao= :id order by idautorizacao_autorizacaocancelamento asc", 
			resultClass=AutorizacaoPendente.class)
})
public class AutorizacaoPendente extends ViewArquivoPendente {

	private static final long serialVersionUID = 1L;
	
	private Integer idAutorizacao_AutorizacaoCancelamento;
	private String tipo_Arquivo;
	private Integer idArquivo_Arquivo;
	private String nomeArquivo_Arquivo;
	private Date dataRecebimento_Arquivo;
	private LocalDate dataEnvio_Arquivo;
	private LocalTime horaEnvio_Arquivo;
	private String statusDownload;
	private Integer idInstituicao_Instituicao;
	private String nomeFantasia_Instituicao;
	private String codigoCompensacao_Instituicao;
	private Integer idInstituicao_Cartorio;
	private String nomeFantasia_Cartorio;
	private String nomeMunicipio_Municipio;
	private String cod_ibge_Municipio;

	@Id
	public Integer getIdAutorizacao_AutorizacaoCancelamento() {
		return idAutorizacao_AutorizacaoCancelamento;
	}

	public String getTipo_Arquivo() {
		return tipo_Arquivo;
	}

	public Integer getIdArquivo_Arquivo() {
		return idArquivo_Arquivo;
	}

	public String getNomeArquivo_Arquivo() {
		return nomeArquivo_Arquivo;
	}

	public Date getDataRecebimento_Arquivo() {
		return dataRecebimento_Arquivo;
	}

	public LocalDate getDataEnvio_Arquivo() {
		return dataEnvio_Arquivo;
	}

	public LocalTime getHoraEnvio_Arquivo() {
		return horaEnvio_Arquivo;
	}

	public String getStatusDownload() {
		return statusDownload;
	}

	public Integer getIdInstituicao_Instituicao() {
		return idInstituicao_Instituicao;
	}

	public String getNomeFantasia_Instituicao() {
		return nomeFantasia_Instituicao;
	}

	public String getCodigoCompensacao_Instituicao() {
		return codigoCompensacao_Instituicao;
	}

	public Integer getIdInstituicao_Cartorio() {
		return idInstituicao_Cartorio;
	}

	public String getNomeFantasia_Cartorio() {
		return nomeFantasia_Cartorio;
	}

	public String getNomeMunicipio_Municipio() {
		return nomeMunicipio_Municipio;
	}

	public String getCod_ibge_Municipio() {
		return cod_ibge_Municipio;
	}

	public void setIdAutorizacao_AutorizacaoCancelamento(Integer idAutorizacao_AutorizacaoCancelamento) {
		this.idAutorizacao_AutorizacaoCancelamento = idAutorizacao_AutorizacaoCancelamento;
	}

	public void setTipo_Arquivo(String tipo_Arquivo) {
		this.tipo_Arquivo = tipo_Arquivo;
	}

	public void setIdArquivo_Arquivo(Integer idArquivo_Arquivo) {
		this.idArquivo_Arquivo = idArquivo_Arquivo;
	}

	public void setNomeArquivo_Arquivo(String nomeArquivo_Arquivo) {
		this.nomeArquivo_Arquivo = nomeArquivo_Arquivo;
	}

	public void setDataRecebimento_Arquivo(Date dataRecebimento_Arquivo) {
		this.dataRecebimento_Arquivo = dataRecebimento_Arquivo;
	}

	public void setDataEnvio_Arquivo(LocalDate dataEnvio_Arquivo) {
		this.dataEnvio_Arquivo = dataEnvio_Arquivo;
	}

	public void setHoraEnvio_Arquivo(LocalTime horaEnvio_Arquivo) {
		this.horaEnvio_Arquivo = horaEnvio_Arquivo;
	}

	public void setStatusDownload(String statusDownload) {
		this.statusDownload = statusDownload;
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

	public void setIdInstituicao_Cartorio(Integer idInstituicao_Cartorio) {
		this.idInstituicao_Cartorio = idInstituicao_Cartorio;
	}

	public void setNomeFantasia_Cartorio(String nomeFantasia_Cartorio) {
		this.nomeFantasia_Cartorio = nomeFantasia_Cartorio;
	}

	public void setNomeMunicipio_Municipio(String nomeMunicipio_Municipio) {
		this.nomeMunicipio_Municipio = nomeMunicipio_Municipio;
	}

	public void setCod_ibge_Municipio(String cod_ibge_Municipio) {
		this.cod_ibge_Municipio = cod_ibge_Municipio;
	}

}