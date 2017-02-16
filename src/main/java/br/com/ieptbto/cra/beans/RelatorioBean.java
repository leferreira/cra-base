package br.com.ieptbto.cra.beans;

import java.io.Serializable;
import java.util.Date;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;

public class RelatorioBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;
	private Date dataInicio;
	private Date dataFim;
	private String tipoExportacao;
	private TipoInstituicaoSistema tipoInstituicao;
	private Instituicao bancoConvenio;
	private Instituicao cartorio;
	private SituacaoTituloRelatorio situacaoTituloRelatorio;

	public Date getDataInicio() {
		return dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public String getTipoExportacao() {
		return tipoExportacao;
	}

	public Instituicao getBancoConvenio() {
		return bancoConvenio;
	}

	public Instituicao getCartorio() {
		return cartorio;
	}

	public SituacaoTituloRelatorio getSituacaoTituloRelatorio() {
		return situacaoTituloRelatorio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void setTipoExportacao(String tipoExportacao) {
		this.tipoExportacao = tipoExportacao;
	}

	public void setBancoConvenio(Instituicao bancoConvenio) {
		this.bancoConvenio = bancoConvenio;
	}

	public TipoInstituicaoSistema getTipoInstituicao() {
		return tipoInstituicao;
	}

	public void setTipoInstituicao(TipoInstituicaoSistema tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public void setCartorio(Instituicao cartorio) {
		this.cartorio = cartorio;
	}

	public void setSituacaoTituloRelatorio(SituacaoTituloRelatorio situacaoTituloRelatorio) {
		this.situacaoTituloRelatorio = situacaoTituloRelatorio;
	}
}