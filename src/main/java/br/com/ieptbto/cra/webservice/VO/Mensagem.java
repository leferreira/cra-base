package br.com.ieptbto.cra.webservice.VO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Leandro
 * 
 */
@XmlRootElement(name = "mensagem")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mensagem extends MensagemCra {

	@XmlAttribute(name = "descricao")
	private String descricao;

	@XmlAttribute(name = "codigo")
	private String codigo;

	@XmlAttribute(name = "municipio")
	private String municipio;

	public String getDescricao() {
		return descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@XmlAttribute(name = "nosso_numero")
	private String nossoNumero;

	@XmlAttribute(name = "numero_sequencial_registro")
	private int numeroSequencialRegistro;

	public String getNossoNumero() {
		return nossoNumero;
	}

	public int getNumeroSequencialRegistro() {
		return numeroSequencialRegistro;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setNumeroSequencialRegistro(int numeroSequencialRegistro) {
		this.numeroSequencialRegistro = numeroSequencialRegistro;
	}
}
