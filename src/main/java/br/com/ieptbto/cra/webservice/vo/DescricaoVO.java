package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.*;

/**
 * 
 * @author Leandro
 * 
 */
@XmlRootElement(name = "descricao")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "dataEnvio", "tipoArquivo", "nomeArquivo", "dataMovimento", "portador", "usuario" })
public class DescricaoVO {

	public static final String XML_UPLOAD_REMESSA = "XML_UPLOAD_REMESSA";
	public static final String XML_UPLOAD_SUSTACAO = "XML_UPLOAD_SUSTACAO";
	public static final String XML_UPLOAD_CONFIRMACAO = "XML_UPLOAD_CONFIRMACAO";
	public static final String XML_UPLOAD_RETORNO = "XML_UPLOAD_RETORNO";

	@XmlElement(name = "dataEnvio")
	private String dataEnvio;

	@XmlElement(name = "tipoArquivo")
	private String tipoArquivo;

	@XmlElement(name = "nomeArquivo")
	private String nomeArquivo;

	@XmlElement(name = "dataMovimento")
	private String dataMovimento;

	@XmlElement(name = "portador")
	private String portador;

	@XmlElement(name = "usuario")
	private String usuario;

	public String getDataEnvio() {
		return dataEnvio;
	}

	public String getTipoArquivo() {
		return tipoArquivo;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}

	public String getPortador() {
		return portador;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setDataEnvio(String dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setTipoArquivo(String tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setPortador(String portador) {
		this.portador = portador;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
