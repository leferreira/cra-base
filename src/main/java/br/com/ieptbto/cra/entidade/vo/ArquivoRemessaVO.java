package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Lefer
 *
 */
@XmlRootElement(name = "remessa")
@XmlAccessorType(XmlAccessType.NONE)
public class ArquivoRemessaVO {

	@XmlElement(name = "remessa")
	private List<RemessaVO> remessas;

	public List<RemessaVO> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<RemessaVO> remessas) {
		this.remessas = remessas;
	}

}
