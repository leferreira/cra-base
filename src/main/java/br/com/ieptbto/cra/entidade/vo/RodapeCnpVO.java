package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "trailler")
@XmlAccessorType(XmlAccessType.NONE)
public class RodapeCnpVO {

	@XmlAttribute
	private String codigoRegistro;
	
	@XmlAttribute
	private String emBranco2;
	
	@XmlAttribute
	private String sequenciaRegistro;

	public String getCodigoRegistro() {
		return codigoRegistro;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public String getEmBranco2() {
		return emBranco2;
	}

	public void setEmBranco2(String emBranco2) {
		this.emBranco2 = emBranco2;
	}

	public String getSequenciaRegistro() {
		return sequenciaRegistro;
	}

	public void setSequenciaRegistro(String sequenciaRegistro) {
		this.sequenciaRegistro = sequenciaRegistro;
	}

}
