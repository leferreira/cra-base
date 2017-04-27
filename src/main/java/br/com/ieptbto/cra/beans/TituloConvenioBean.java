package br.com.ieptbto.cra.beans;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Thasso Araujo
 *
 */
public class TituloConvenioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date dataInicio;
	private Date dataFim;
	private String numeroTitulo;
	private String nomeDevedor;
	private String documentoDevedor;
	private String numeroProtocoloCartorio;
	private String tipoExportacao;
	private TipoInstituicaoCRA tipoInstituicao;
	private Instituicao bancoConvenio;
	private Instituicao cartorio;
	private Municipio municipio;
	private Filiado filiado;
	private SituacaoTituloRelatorio situacaoTitulosRelatorio;

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public String getDocumentoDevedor() {
		return documentoDevedor;
	}
	
	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
	}

	public String getTipoExportacao() {
		return tipoExportacao;
	}

	public void setTipoExportacao(String tipoExportacao) {
		this.tipoExportacao = tipoExportacao;
	}

	public TipoInstituicaoCRA getTipoInstituicao() {
		return tipoInstituicao;
	}

	public void setTipoInstituicao(TipoInstituicaoCRA tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public Instituicao getBancoConvenio() {
		return bancoConvenio;
	}

	public void setBancoConvenio(Instituicao bancoConvenio) {
		this.bancoConvenio = bancoConvenio;
	}
	
	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public Instituicao getCartorio() {
		return cartorio;
	}

	public void setCartorio(Instituicao cartorio) {
		this.cartorio = cartorio;
	}

	public SituacaoTituloRelatorio getSituacaoTitulosRelatorio() {
		return situacaoTitulosRelatorio;
	}

	public void setSituacaoTitulosRelatorio(SituacaoTituloRelatorio situacaoTitulosRelatorio) {
		this.situacaoTitulosRelatorio = situacaoTitulosRelatorio;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public Filiado getFiliado() {
		return filiado;
	}

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}
}