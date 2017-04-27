package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class CnpCartoriosConteudoVO {

	@XmlElement(name = "cartorio", required = true)
	private List<CnpCartorioVO> cartorios;

	public List<CnpCartorioVO> getCartorios() {
		return cartorios;
	}

	public void setCartorios(List<CnpCartorioVO> cartorios) {
		this.cartorios = cartorios;
	}
}
