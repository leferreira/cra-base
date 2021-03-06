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
public class ArquivoRemessaConvenioVO {

	@XmlElement(name = "titulo")
	private List<TituloConvenioVO> titulosConvenio;

	public List<TituloConvenioVO> getTitulosConvenio() {
		return titulosConvenio;
	}

	public void setTitulosConvenio(List<TituloConvenioVO> titulosConvenio) {
		this.titulosConvenio = titulosConvenio;
	}
}