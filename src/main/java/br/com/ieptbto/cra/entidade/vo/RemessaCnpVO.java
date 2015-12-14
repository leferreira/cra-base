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
@XmlAccessorType(XmlAccessType.NONE)
public class RemessaCnpVO {

	@XmlElement(name = "header")
	private CabecalhoCnpVO cabecalhoSerasaVO;
	
	@XmlElement(name = "detalhe")
	private List<RegistrosCnpVO> registrosSerasaVO;
	
	@XmlElement(name = "trailler")
	private RodapeCnpVO rodapeSerasaVO;

	public CabecalhoCnpVO getCabecalhoSerasaVO() {
		return cabecalhoSerasaVO;
	}

	public void setCabecalhoSerasaVO(CabecalhoCnpVO cabecalhoSerasaVO) {
		this.cabecalhoSerasaVO = cabecalhoSerasaVO;
	}

	public List<RegistrosCnpVO> getRegistrosSerasaVO() {
		return registrosSerasaVO;
	}

	public void setRegistrosSerasaVO(List<RegistrosCnpVO> registrosSerasaVO) {
		this.registrosSerasaVO = registrosSerasaVO;
	}

	public RodapeCnpVO getRodapeSerasaVO() {
		return rodapeSerasaVO;
	}

	public void setRodapeSerasaVO(RodapeCnpVO rodapeSerasaVO) {
		this.rodapeSerasaVO = rodapeSerasaVO;
	}
}
