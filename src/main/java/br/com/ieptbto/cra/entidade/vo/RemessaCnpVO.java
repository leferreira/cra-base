package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

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
	private List<TituloCnpVO> titulosCnpVO;
	
	@XmlElement(name = "rodape")
	private RodapeCnpVO rodapeCnpVO;

	public CabecalhoCnpVO getCabecalhoCnpVO() {
		return cabecalhoCnpVO;
	}

	public void setCabecalhoCnpVO(CabecalhoCnpVO cabecalhoCnpVO) {
		this.cabecalhoCnpVO = cabecalhoCnpVO;
	}

	public List<TituloCnpVO> getTitulosCnpVO() {
		return titulosCnpVO;
	}

	public void setTitulosCnpVO(List<TituloCnpVO> tituloCnpVO) {
		this.titulosCnpVO = tituloCnpVO;
	}

	public RodapeCnpVO getRodapeCnpVO() {
		return rodapeCnpVO;
	}

	public void setRodapeCnpVO(RodapeCnpVO rodapeCnpVO) {
		this.rodapeCnpVO = rodapeCnpVO;
	}
}
