package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thasso Araújo
 *
 */

@XmlRootElement(name = "remessa")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemessaCnpVO {

	@XmlElement(name = "cabecalho")
	private CabecalhoCnpVO cabecalhoCnpVO;
	
	@XmlElement(name = "titulo")
	private List<TituloCnpVO> registrosCnpVO;
	
	@XmlElement(name = "rodape")
	private RodapeCnpVO rodapeCnpVO;

	public CabecalhoCnpVO getCabecalhoCnpVO() {
		return cabecalhoCnpVO;
	}

	public void setCabecalhoCnpVO(CabecalhoCnpVO cabecalhoCnpVO) {
		this.cabecalhoCnpVO = cabecalhoCnpVO;
	}

	public List<TituloCnpVO> getRegistrosCnpVO() {
		return registrosCnpVO;
	}

	public void setRegistrosCnpVO(List<TituloCnpVO> registrosCnpVO) {
		this.registrosCnpVO = registrosCnpVO;
	}

	public RodapeCnpVO getRodapeCnpVO() {
		return rodapeCnpVO;
	}

	public void setRodapeCnpVO(RodapeCnpVO rodapeCnpVO) {
		this.rodapeCnpVO = rodapeCnpVO;
	}
}
