package br.com.ieptbto.cra.webservice.VO;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "instituicao")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstituicaoPendente {

	@XmlElement(name = "codigo_apresentante")
	private List<String> codigosApresentantes;

	public List<String> getCodigosApresentantes() {
		return codigosApresentantes;
	}

	public void setCodigosApresentantes(List<String> codigosApresentantes) {
		this.codigosApresentantes = codigosApresentantes;
	}
}
