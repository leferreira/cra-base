package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Thasso Araújo
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ArquivoCnpVO {

	@XmlElement(name = "remessa")
	private List<RemessaCnpVO> remessasCnpVO;

	@XmlAttribute(name = "dataEnvio")
	private String dataEnvio;

	@XmlAttribute(name = "instituicaoEnvio")
	private String instituicaoEnvio;

	public List<RemessaCnpVO> getRemessasCnpVO() {
		return remessasCnpVO;
	}

	public String getDataEnvio() {
		return dataEnvio;
	}

	public String getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	public void setDataEnvio(String dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setInstituicaoEnvio(String instituicaoEnvio) {
		this.instituicaoEnvio = instituicaoEnvio;
	}

	public void setRemessasCnpVO(List<RemessaCnpVO> remessasCnpVO) {
		this.remessasCnpVO = remessasCnpVO;
	}
}
