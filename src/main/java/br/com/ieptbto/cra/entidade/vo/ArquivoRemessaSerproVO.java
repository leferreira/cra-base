package br.com.ieptbto.cra.entidade.vo;

import java.util.ArrayList;
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
public class ArquivoRemessaSerproVO {

	@XmlElement(name = "comarca")
	private List<RemessaComarcaVO> remessasComarcas;

	public List<RemessaComarcaVO> getRemessasComarcas() {
		return remessasComarcas;
	}

	public void setRemessasComarcas(List<RemessaComarcaVO> remessasComarcas) {
		this.remessasComarcas = remessasComarcas;
	}

	public static ArquivoRemessaVO parseToArquivoRemessaVO(ArquivoRemessaSerproVO arquivoRemessaSerpro) {
		ArquivoRemessaVO arquivoRemessaVO = new ArquivoRemessaVO();
		arquivoRemessaVO.setRemessasComarcas(new ArrayList<RemessaComarcaVO>());
			arquivoRemessaVO.getRemessasComarcas().addAll(arquivoRemessaSerpro.getRemessasComarcas());
		return arquivoRemessaVO;
	}
}
