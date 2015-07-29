package br.com.ieptbto.cra.ireport;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class EtiquetasJRDataSource implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;
	
	private String razaoSocialPortador;
	private String municipioPortador;
	private String ufPortador;
	private String dataOcorrencia;
	private String nomeCedente;
	private String nomeSacador;
	private String documentoSacador;
	private String nossoNumero;
	private String numeroTitulo;
	private String dataVencimento;
	private BigDecimal valorSaldoTitulo;
	private String numeroProtocolo;
	private String pracaProtesto;

	private String agenciaCentralizadora; //
	private String codigoCedente; //
	private String codigoAgencia; //
	private String nomeAgencia; //

	public void parseToTituloRemessa(TituloRemessa titulo){
		this.setRazaoSocialPortador(titulo.getRemessa().getInstituicaoOrigem().getRazaoSocial());
		this.setMunicipioPortador(titulo.getRemessa().getInstituicaoOrigem().getMunicipio().getNomeMunicipio());
		this.setUfPortador(titulo.getRemessa().getInstituicaoOrigem().getMunicipio().getUf());
		this.setDataOcorrencia(DataUtil.localDateToString(titulo.getRetorno().getDataOcorrencia()));
		this.setNomeCedente(titulo.getNomeCedenteFavorecido());
		this.setNomeSacador(titulo.getNomeSacadorVendedor());
		this.setDocumentoSacador(titulo.getDocumentoSacador());
		this.setNossoNumero(titulo.getNossoNumero());
		this.setNumeroTitulo(titulo.getNumeroTitulo());
		this.setDataVencimento(DataUtil.localDateToString(titulo.getDataVencimentoTitulo()));
		this.setValorSaldoTitulo(titulo.getSaldoTitulo());
		this.setNumeroProtocolo(titulo.getNumeroProtocoloCartorio());
		this.setPracaProtesto(titulo.getPracaProtesto());
	}
	
	public String getRazaoSocialPortador() {
		return razaoSocialPortador;
	}

	public String getMunicipioPortador() {
		return municipioPortador;
	}

	public String getUfPortador() {
		return ufPortador;
	}

	public String getCodigoAgencia() {
		return codigoAgencia;
	}

	public String getNomeAgencia() {
		return nomeAgencia;
	}

	public String getDataOcorrencia() {
		return dataOcorrencia;
	}

	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	public String getCodigoCedente() {
		return codigoCedente;
	}

	public String getNomeCedente() {
		return nomeCedente;
	}

	public String getNomeSacador() {
		return nomeSacador;
	}

	public String getDocumentoSacador() {
		return documentoSacador;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public String getDataVencimento() {
		return dataVencimento;
	}

	public BigDecimal getValorSaldoTitulo() {
		return valorSaldoTitulo;
	}

	public String getNumeroProtocolo() {
		return numeroProtocolo;
	}

	public String getPracaProtesto() {
		return pracaProtesto;
	}

	public void setRazaoSocialPortador(String razaoSocialPortador) {
		this.razaoSocialPortador = razaoSocialPortador;
	}

	public void setMunicipioPortador(String municipioPortador) {
		this.municipioPortador = municipioPortador;
	}

	public void setUfPortador(String ufPortador) {
		this.ufPortador = ufPortador;
	}

	public void setCodigoAgencia(String codigoAgencia) {
		this.codigoAgencia = codigoAgencia;
	}

	public void setNomeAgencia(String nomeAgencia) {
		this.nomeAgencia = nomeAgencia;
	}

	public void setDataOcorrencia(String dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
	}

	public void setCodigoCedente(String codigoCedente) {
		this.codigoCedente = codigoCedente;
	}

	public void setNomeCedente(String nomeCedente) {
		this.nomeCedente = nomeCedente;
	}

	public void setNomeSacador(String nomeSacador) {
		this.nomeSacador = nomeSacador;
	}

	public void setDocumentoSacador(String documentoSacador) {
		this.documentoSacador = documentoSacador;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setDataVencimento(String vencimento) {
		this.dataVencimento = vencimento;
	}

	public void setValorSaldoTitulo(BigDecimal valorSaldoTitulo) {
		this.valorSaldoTitulo = valorSaldoTitulo;
	}

	public void setNumeroProtocolo(String numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}

	public void setPracaProtesto(String pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}
}