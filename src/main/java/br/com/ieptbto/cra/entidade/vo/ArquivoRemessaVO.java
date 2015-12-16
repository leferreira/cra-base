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
	private List<RemessaCnp> remessas;

	public List<RemessaCnp> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<RemessaCnp> remessas) {
		this.remessas = remessas;
	}

}
