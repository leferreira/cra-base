package br.com.ieptbto.cra.ireport;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class SlipEtiquetaBean implements Serializable, Comparable<SlipEtiquetaBean> {

	/***/
	private static final long serialVersionUID = 1L;
	
	private String razaoSocialPortador;
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
	
	private String codigoAgencia; //
	private String municipioAgencia; //
	private String ufAgencia; //

	public void parseToTituloRemessa(TituloRemessa titulo){
		this.setRazaoSocialPortador(titulo.getRemessa().getInstituicaoOrigem().getRazaoSocial());

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
		this.setAgenciaCentralizadora(titulo.getRemessa().getCabecalho().getAgenciaCentralizadora());
	}
	
	public String getRazaoSocialPortador() {
		return razaoSocialPortador;
	}

	public String getCodigoAgencia() {
		return codigoAgencia;
	}

	public String getDataOcorrencia() {
		return dataOcorrencia;
	}

	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
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

	public void setCodigoAgencia(String codigoAgencia) {
		this.codigoAgencia = codigoAgencia;
	}

	public void setDataOcorrencia(String dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
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
	
	public String getMunicipioAgencia() {
		return municipioAgencia;
	}

	public void setMunicipioAgencia(String municipioAgencia) {
		this.municipioAgencia = municipioAgencia;
	}

	public String getUfAgencia() {
		return ufAgencia;
	}

	public void setUfAgencia(String ufAgencia) {
		this.ufAgencia = ufAgencia;
	}

	@Override
	public int compareTo(SlipEtiquetaBean outraEtiqueta) {
		int comparatePortador = this.razaoSocialPortador.compareToIgnoreCase(outraEtiqueta.getRazaoSocialPortador());
		int comparateMunicipio = this.pracaProtesto.compareToIgnoreCase(outraEtiqueta.getPracaProtesto());
		
		if (comparatePortador < -1) {
            return -1;
        } else if (comparatePortador > 1) {
            return 1;
        } else if (!this.getRazaoSocialPortador().equalsIgnoreCase(outraEtiqueta.getRazaoSocialPortador())) {
        	String[] apresentantes = new String[]{this.getRazaoSocialPortador(), outraEtiqueta.getRazaoSocialPortador()};
        	Arrays.sort(apresentantes);
        	List<String> bancos = Arrays.asList(apresentantes);
        	if(this.getRazaoSocialPortador().equals(bancos.get(0))) {
        		return -1;
        	}
        	return 1;
        } else if (comparateMunicipio < -1) {
            return -1;
        } else if (comparateMunicipio > 1) {
            return 1;
        } else if (!this.getPracaProtesto().equalsIgnoreCase(outraEtiqueta.getPracaProtesto())) {
        	String[] municipios = new String[]{this.getPracaProtesto(), outraEtiqueta.getPracaProtesto()};
        	Arrays.sort(municipios);
        	List<String> cidades = Arrays.asList(municipios);
        	if(this.getPracaProtesto().equals(cidades.get(0))) {
        		return -1;
        	}
        	return 1;
        } else if (Integer.parseInt(numeroProtocolo) < Integer.parseInt(outraEtiqueta.getNumeroProtocolo())) {
            return -1;
        } else if (Integer.parseInt(numeroProtocolo) > Integer.parseInt(outraEtiqueta.getNumeroProtocolo())) {
            return 1;
        }
		return 0;
	}
}