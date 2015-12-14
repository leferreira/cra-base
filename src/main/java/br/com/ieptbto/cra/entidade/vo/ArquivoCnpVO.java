package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Thasso Araújo
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ArquivoCnpVO {

	@XmlElement(name="remessa")
	private List<RemessaCnpVO> remessasCnpVO;

	public List<RemessaCnpVO> getRemessasCnpVO() {
		return remessasCnpVO;
	}

	public void setRemessasCnpVO(List<RemessaCnpVO> remessasCnpVO) {
		this.remessasCnpVO = remessasCnpVO;
	}
}
