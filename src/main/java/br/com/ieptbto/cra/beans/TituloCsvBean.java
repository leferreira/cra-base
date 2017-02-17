package br.com.ieptbto.cra.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.view.ViewTitulo;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 *         Bean criado para criacao de arquivos csv apartir de uma datatable de
 *         titulo remessa
 */
public class TituloCsvBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	private String apresentante;
	private String nossoNumero;
	private String numeroTitulo;
	private String numeroProtocoloCartorio;
	private String municipio;
	private Integer numeroControleDevedor;
	private String nomeDevedor;
	private String saldoTitulo;
	private String remessa;
	private String dataConfirmacao;
	private String retorno;
	private String dataOcorrencia;
//	private String desistencia;
	private String situacaoTitulo;

	public void parseToViewTitulo(ViewTitulo titulo) {
		this.apresentante = titulo.getNomeFantasia_Instituicao();
		this.nossoNumero = titulo.getNossoNumero_TituloRemessa();
		this.numeroTitulo = titulo.getNumeroTitulo_TituloRemessa();
		this.nomeDevedor = titulo.getNomeDevedor_TituloRemessa();
		this.numeroControleDevedor = titulo.getNumeroControleDevedor_TituloRemessa();
		this.saldoTitulo = "R$" + titulo.getSaldoTitulo_TituloRemessa().toString();
		this.remessa = titulo.getNomeArquivo_Arquivo_Remessa();
		this.numeroProtocoloCartorio = titulo.getNumeroProtocoloCartorio_Confirmacao();
		this.dataConfirmacao = DataUtil.localDateToString(titulo.getDataEnvio_Arquivo_Confirmacao());
		this.municipio = titulo.getNomeMunicipio_Municipio().toUpperCase();
		this.retorno = titulo.getNomeArquivo_Arquivo_Retorno();
		this.dataOcorrencia = DataUtil.localDateToString(titulo.getDataOcorrencia_ConfirmacaoRetorno());
		this.situacaoTitulo = titulo.getSituacaoTitulo();
	}
	
	public static List<TituloCsvBean> parseToListViewTitulo(List<ViewTitulo> titulos) {
		List<TituloCsvBean> titulosBean = new ArrayList<TituloCsvBean>();

		for (ViewTitulo tituloRemessa : titulos) {
			TituloCsvBean titulo = new TituloCsvBean();
			titulo.parseToViewTitulo(tituloRemessa);
			titulosBean.add(titulo);
		}
		return titulosBean;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public String getMunicipio() {
		if (municipio.length() > 19) {
			this.municipio = municipio.substring(0, 19);
		}
		return municipio;
	}

	public String getNomeDevedor() {
		if (nomeDevedor.length() > 24) {
			this.nomeDevedor = nomeDevedor.substring(0, 24);
		}
		return nomeDevedor;
	}

	public String getSaldoTitulo() {
		return saldoTitulo;
	}

	public String getRemessa() {
		return remessa;
	}

	public String getDataConfirmacao() {
		return dataConfirmacao;
	}

	public String getRetorno() {
		return retorno;
	}

	public String getDataOcorrencia() {
		return dataOcorrencia;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setSaldoTitulo(String saldoTitulo) {
		this.saldoTitulo = saldoTitulo;
	}

	public void setRemessa(String remessa) {
		this.remessa = remessa;
	}

	public void setDataConfirmacao(String dataConfirmacao) {
		this.dataConfirmacao = dataConfirmacao;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public void setDataOcorrencia(String dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public String getSituacaoTitulo() {
		return situacaoTitulo;
	}

	public void setSituacaoTitulo(String situacaoTitulo) {
		this.situacaoTitulo = situacaoTitulo;
	}

	public String getApresentante() {
		if (apresentante.length() > 40) {
			this.apresentante = apresentante.substring(0, 39);
		}
		return apresentante;
	}

	public void setApresentante(String apresentante) {
		this.apresentante = apresentante;
	}

	public Integer getNumeroControleDevedor() {
		if (numeroControleDevedor == null) {
			this.numeroControleDevedor = 1;
		}
		return numeroControleDevedor;
	}

	public void setNumeroControleDevedor(Integer numeroControleDevedor) {
		this.numeroControleDevedor = numeroControleDevedor;
	}
}