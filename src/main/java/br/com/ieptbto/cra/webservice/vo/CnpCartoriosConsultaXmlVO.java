package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "consulta")
@XmlAccessorType(XmlAccessType.FIELD)
public class CnpCartoriosConsultaXmlVO {

	@XmlElement(name = "conteudo")
	private CnpCartoriosConteudoVO conteudo;

	public CnpCartoriosConteudoVO getConteudo() {
		return conteudo;
	}

	public void setConteudo(CnpCartoriosConteudoVO conteudo) {
		this.conteudo = conteudo;
	}
}
