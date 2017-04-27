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
@XmlAccessorType(XmlAccessType.NONE)
public class ArquivoRemessaVO {

	@XmlElement(name = "arquivo_comarca")
	private List<RemessaComarcaVO> remessasComarcas;

	public List<RemessaComarcaVO> getRemessasComarcas() {
		return remessasComarcas;
	}

	public void setRemessasComarcas(List<RemessaComarcaVO> remessasComarcas) {
		this.remessasComarcas = remessasComarcas;
	}

}
