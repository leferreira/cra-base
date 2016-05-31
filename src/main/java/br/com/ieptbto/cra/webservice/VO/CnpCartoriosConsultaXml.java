package br.com.ieptbto.cra.webservice.VO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "consulta")
@XmlAccessorType(XmlAccessType.FIELD)
public class CnpCartoriosConsultaXml {

	@XmlElement(name = "conteudo")
	private CnpCartoriosConteudo conteudo;

	public CnpCartoriosConteudo getConteudo() {
		return conteudo;
	}

	public void setConteudo(CnpCartoriosConteudo conteudo) {
		this.conteudo = conteudo;
	}
}
