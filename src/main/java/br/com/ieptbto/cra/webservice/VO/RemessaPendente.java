package br.com.ieptbto.cra.webservice.VO;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "remessa")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemessaPendente {

	@XmlElement(name = "arquivo")
	private List<ArquivoRemessa> arquivoRemessa;

	public List<ArquivoRemessa> getArquivoRemessa() {
		return arquivoRemessa;
	}

	public void setArquivoRemessa(List<ArquivoRemessa> arquivoRemessa) {
		this.arquivoRemessa = arquivoRemessa;
	}
}