package br.com.ieptbto.cra.beans;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.NivelDetalhamentoRelatorio;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoVisualizacaoArquivos;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ArquivoBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;
	private String nomeArquivo;
	private Date dataInicio;
	private Date dataFim;
	private NivelDetalhamentoRelatorio tipoRelatorio;
	private TipoArquivoFebraban tipoArquivo;
	private List<TipoArquivoFebraban> tiposArquivos;
	private List<StatusDownload> situacoesArquivos;
	private TipoInstituicaoCRA tipoInstituicao;
	private Instituicao bancoConvenio;
	private Instituicao cartorio;
	private Instituicao instituicao;
	private Municipio municipio;
	private TipoVisualizacaoArquivos tipoVisualizacaoArquivos;

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public NivelDetalhamentoRelatorio getTipoRelatorio() {
		return tipoRelatorio;
	}

	public TipoArquivoFebraban getTipoArquivo() {
		return tipoArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void setTipoRelatorio(NivelDetalhamentoRelatorio tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public void setTipoArquivo(TipoArquivoFebraban tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public List<TipoArquivoFebraban> getTiposArquivos() {
		return tiposArquivos;
	}

	public void setTiposArquivos(List<TipoArquivoFebraban> tiposArquivos) {
		this.tiposArquivos = tiposArquivos;
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

	public Instituicao getCartorio() {
		return cartorio;
	}

	public void setCartorio(Instituicao cartorio) {
		this.cartorio = cartorio;
	}

	public TipoVisualizacaoArquivos getTipoVisualizacaoArquivos() {
		return tipoVisualizacaoArquivos;
	}

	public void setTipoVisualizacaoArquivos(TipoVisualizacaoArquivos tipoVisualizacaoArquivos) {
		this.tipoVisualizacaoArquivos = tipoVisualizacaoArquivos;
	}

	public List<StatusDownload> getSituacoesArquivos() {
		return situacoesArquivos;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public void setSituacoesArquivos(List<StatusDownload> situacoesArquivos) {
		this.situacoesArquivos = situacoesArquivos;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}
}