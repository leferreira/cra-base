package br.com.ieptbto.cra.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 *         Bean criado para criacao de arquivos csv apartir de uma datatable de
 *         titulo remessa
 */
public class TituloBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	private String apresentante;
	private String nossoNumero;
	private String numeroTitulo;
	private String numeroProtocoloCartorio;
	private String municipio;
	private String nomeDevedor;
	private String saldoTitulo;
	private String remessa;
	private String dataConfirmacao;
	private String retorno;
	private String dataOcorrencia;
	private String desistencia;
	private String situacaoTitulo;

	public void parseToTituloRemessa(TituloRemessa tituloRemessa) {
		this.apresentante = tituloRemessa.getRemessa().getInstituicaoOrigem().getNomeFantasia().toUpperCase();
		this.nossoNumero = tituloRemessa.getNossoNumero();
		this.numeroTitulo = tituloRemessa.getNumeroTitulo();
		if (tituloRemessa.getNomeDevedor().length() > 24) {
			this.nomeDevedor = tituloRemessa.getNomeDevedor().substring(0, 24);
		} else {
			this.nomeDevedor = tituloRemessa.getNomeDevedor();
		}
		this.saldoTitulo = "R$ " + tituloRemessa.getSaldoTitulo().toString();
		this.remessa = tituloRemessa.getRemessa().getArquivo().getNomeArquivo();
		if (tituloRemessa.getConfirmacao() != null) {
			this.numeroProtocoloCartorio = tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio();
			this.dataConfirmacao = DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getArquivo().getDataEnvio());
		}
		this.municipio = tituloRemessa.getRemessa().getInstituicaoDestino().getMunicipio().getNomeMunicipio().toUpperCase();
		if (tituloRemessa.getRetorno() != null) {
			this.retorno = tituloRemessa.getRetorno().getRemessa().getArquivo().getNomeArquivo();
		}
		if (tituloRemessa.getRetorno() != null) {
			this.dataOcorrencia = DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia());
		} else if (tituloRemessa.getConfirmacao() != null) {
			this.dataOcorrencia = DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataOcorrencia());
		}
		if (!tituloRemessa.getPedidosDesistencia().isEmpty()) {
			this.desistencia = "SIM";
		} else {
			this.desistencia = "NÃO";
		}
		this.situacaoTitulo = tituloRemessa.getSituacaoTitulo();
	}

	public static List<TituloBean> parseToListTituloRemessa(List<TituloRemessa> titulos) {
		List<TituloBean> titulosBean = new ArrayList<TituloBean>();

		for (TituloRemessa tituloRemessa : titulos) {
			TituloBean titulo = new TituloBean();
			titulo.parseToTituloRemessa(tituloRemessa);
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
		return municipio;
	}

	public String getNomeDevedor() {
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
		return apresentante;
	}

	public void setApresentante(String apresentante) {
		this.apresentante = apresentante;
	}

	public String getDesistencia() {
		return desistencia;
	}

	public void setDesistencia(String desistencia) {
		this.desistencia = desistencia;
	}
}
