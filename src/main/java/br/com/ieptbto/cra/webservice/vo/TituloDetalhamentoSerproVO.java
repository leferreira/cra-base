package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "titulo")
@XmlAccessorType(XmlAccessType.FIELD)
public class TituloDetalhamentoSerproVO {

	@XmlElement(name = "datahora")
	private String dataHora;
	
	@XmlElement(name = "numero_cartorio")
	private String codigoCartorio;
	
	@XmlElement(name = "numero_titulo")
	private String numeroTitulo;
	
	@XmlElement(name = "numero_protocolo")
	private String numeroProtocoloCartorio;
	
	@XmlElement(name = "data_protocolo")
	private String dataProtocolo;
	
	@XmlElement(name = "codigo")
	private String codigo;
	
	@XmlElement(name = "ocorrencia")
	private String ocorrencia;

	public String getDataHora() {
		return dataHora;
	}

	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public String getDataProtocolo() {
		return dataProtocolo;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getOcorrencia() {
		return ocorrencia;
	}

	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}

	public void setCodigoCartorio(String codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setDataProtocolo(String dataProtocolo) {
		this.dataProtocolo = dataProtocolo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
}
