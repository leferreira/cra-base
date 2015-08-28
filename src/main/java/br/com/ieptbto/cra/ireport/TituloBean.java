package br.com.ieptbto.cra.ireport;

import java.math.BigDecimal;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class TituloBean {

	private String nossoNumero;
	private String nomeDevedor;
	private String documentoDevedor;
	private String nomeSacadorVendedor;
	private String documentoSacador;
	private String numeroTitulo;
	private BigDecimal valorTitulo;
	private BigDecimal saldoTitulo;
	private String pracaProtesto;
	private String situacaoTitulo;
	private String numeroProtocoloCartorio;
	private String dataOcorrencia;

	public void parseToTituloRemessa (TituloRemessa titulo) {
		this.setNossoNumero(titulo.getNossoNumero());
		this.setNomeDevedor(titulo.getNomeDevedor());
		this.setDocumentoDevedor(titulo.getDocumentoDevedor());
		this.setNomeSacadorVendedor(titulo.getNomeSacadorVendedor());
		this.setDocumentoSacador(titulo.getDocumentoSacador());
		this.setNumeroTitulo(titulo.getNumeroTitulo());
		this.setValorTitulo(titulo.getValorTitulo());
		this.setSaldoTitulo(titulo.getSaldoTitulo());
		this.setPracaProtesto(titulo.getPracaProtesto());
		this.setSituacaoTitulo(titulo.getSituacaoTitulo());
		
		this.setNumeroProtocoloCartorio("");
		this.setDataOcorrencia("");
		if (titulo.getConfirmacao() != null) {
			this.setNumeroProtocoloCartorio(titulo.getConfirmacao().getNumeroProtocoloCartorio());
			this.setDataOcorrencia(DataUtil.localDateToString(titulo.getConfirmacao().getDataProtocolo()));
		} else if (titulo.getRetorno() != null) {
			this.setNumeroProtocoloCartorio(titulo.getRetorno().getNumeroProtocoloCartorio());
			this.setDataOcorrencia(DataUtil.localDateToString(titulo.getRetorno().getDataOcorrencia()));
		}
	}
	
	public String getNossoNumero() {
		return nossoNumero;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	public String getNomeSacadorVendedor() {
		return nomeSacadorVendedor;
	}

	public String getDocumentoSacador() {
		return documentoSacador;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	public BigDecimal getSaldoTitulo() {
		return saldoTitulo;
	}

	public String getPracaProtesto() {
		return pracaProtesto;
	}

	public String getSituacaoTitulo() {
		return situacaoTitulo;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public String getDataOcorrencia() {
		return dataOcorrencia;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
	}

	public void setNomeSacadorVendedor(String nomeSacadorVendedor) {
		this.nomeSacadorVendedor = nomeSacadorVendedor;
	}

	public void setDocumentoSacador(String documentoSacador) {
		this.documentoSacador = documentoSacador;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setValorTitulo(BigDecimal valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setSaldoTitulo(BigDecimal saldoTitulo) {
		this.saldoTitulo = saldoTitulo;
	}

	public void setPracaProtesto(String pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}

	public void setSituacaoTitulo(String situacaoTitulo) {
		this.situacaoTitulo = situacaoTitulo;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setDataOcorrencia(String dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}
}
