package br.com.ieptbto.cra.bean;

import java.io.Serializable;
import java.util.Date;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;

/**
 * @author Thasso Araújo
 *
 */
public class TituloFormBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	private Date dataInicio;
	private Date dataFim;
	private String nossoNumero;
	private String numeroTitulo;
	private String numeroProtocoloCartorio;
	private String nomeDevedor;
	private String numeroIdentificacaoDevedor;
	private String nomeCredor;
	private String documentoCredor;
	private TipoInstituicaoCRA tipoInstituicao;
	private Instituicao bancoConvenio;
	private Instituicao cartorio;

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

	public String getNossoNumero() {
		return nossoNumero;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public String getNumeroIdentificacaoDevedor() {
		return numeroIdentificacaoDevedor;
	}

	public void setNumeroIdentificacaoDevedor(String numeroIdentificacaoDevedor) {
		this.numeroIdentificacaoDevedor = numeroIdentificacaoDevedor;
	}

	public String getNomeCredor() {
		return nomeCredor;
	}

	public void setNomeCredor(String nomeCredor) {
		this.nomeCredor = nomeCredor;
	}

	public String getDocumentoCredor() {
		return documentoCredor;
	}

	public void setDocumentoCredor(String documentoCredor) {
		this.documentoCredor = documentoCredor;
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

	public TituloRemessa getTituloRemessa() {
		TituloRemessa tituloRemessa = new TituloRemessa();
		tituloRemessa.setNossoNumero(nossoNumero);
		tituloRemessa.setNumeroTitulo(numeroTitulo);
		tituloRemessa.setNumeroProtocoloCartorio(numeroProtocoloCartorio);
		tituloRemessa.setNomeDevedor(nomeDevedor);
		tituloRemessa.setNumeroIdentificacaoDevedor(numeroIdentificacaoDevedor);
		tituloRemessa.setNomeSacadorVendedor(nomeCredor);
		tituloRemessa.setNumeroIdentificacaoDevedor(documentoCredor);
		return tituloRemessa;
	}
}