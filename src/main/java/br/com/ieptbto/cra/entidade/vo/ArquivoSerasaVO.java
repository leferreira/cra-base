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
public class ArquivoSerasaVO {

	@XmlElement(name = "registro_header")
	private CabecalhoSerasaVO cabecalhoSerasaVO;
	
	@XmlElement(name = "registro_detalhe")
	private List<RegistrosSerasaVO> registrosSerasaVO;
	
	@XmlElement(name = "registro_trailler")
	private RodapeSerasaVO rodapeSerasaVO;

	public CabecalhoSerasaVO getCabecalhoSerasaVO() {
		return cabecalhoSerasaVO;
	}

	public void setCabecalhoSerasaVO(CabecalhoSerasaVO cabecalhoSerasaVO) {
		this.cabecalhoSerasaVO = cabecalhoSerasaVO;
	}

	public List<RegistrosSerasaVO> getRegistrosSerasaVO() {
		return registrosSerasaVO;
	}

	public void setRegistrosSerasaVO(List<RegistrosSerasaVO> registrosSerasaVO) {
		this.registrosSerasaVO = registrosSerasaVO;
	}

	public RodapeSerasaVO getRodapeSerasaVO() {
		return rodapeSerasaVO;
	}

	public void setRodapeSerasaVO(RodapeSerasaVO rodapeSerasaVO) {
		this.rodapeSerasaVO = rodapeSerasaVO;
	}
}
