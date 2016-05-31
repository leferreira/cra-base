package br.com.ieptbto.cra.webservice.VO;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class CnpCartoriosConteudo {

	@XmlElement(name = "cartorio", required = true)
	private List<CnpCartorio> cartorios;

	public List<CnpCartorio> getCartorios() {
		return cartorios;
	}

	public void setCartorios(List<CnpCartorio> cartorios) {
		this.cartorios = cartorios;
	}
}
