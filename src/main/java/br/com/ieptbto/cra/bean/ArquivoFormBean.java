package br.com.ieptbto.cra.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.enumeration.TipoVisualizacaoArquivos;

public class ArquivoFormBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	private String nomeArquivo;
	private Date dataInicio;
	private Date dataFim;
	private TipoRelatorio tipoRelatorio;
	private TipoArquivoEnum tipoArquivo;
	private List<TipoArquivoEnum> tiposArquivos;
	private List<SituacaoArquivo> situacoesArquivos;
	private TipoInstituicaoCRA tipoInstituicao;
	private Instituicao bancoConvenio;
	private Instituicao cartorio;
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

	public TipoRelatorio getTipoRelatorio() {
		return tipoRelatorio;
	}

	public TipoArquivoEnum getTipoArquivo() {
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

	public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public void setTipoArquivo(TipoArquivoEnum tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public List<TipoArquivoEnum> getTiposArquivos() {
		return tiposArquivos;
	}

	public void setTiposArquivos(List<TipoArquivoEnum> tiposArquivos) {
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

	public List<SituacaoArquivo> getSituacoesArquivos() {
		return situacoesArquivos;
	}

	public void setSituacoesArquivos(List<SituacaoArquivo> situacoesArquivos) {
		this.situacoesArquivos = situacoesArquivos;
	}
}