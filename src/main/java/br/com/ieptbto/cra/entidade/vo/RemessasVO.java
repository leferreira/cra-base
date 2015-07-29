package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Lefer
 *
 */
@XmlRootElement(name = "remessa")
@XmlAccessorType(XmlAccessType.NONE)
public class RemessasVO {

	private List<RemessaVO> remessaVO;

	public List<RemessaVO> getRemessaVO() {
		return remessaVO;
	}

	public void setRemessaVO(List<RemessaVO> remessaVO) {
		this.remessaVO = remessaVO;
	}

}
