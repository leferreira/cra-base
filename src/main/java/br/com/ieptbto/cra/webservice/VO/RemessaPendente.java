package br.com.ieptbto.cra.webservice.VO;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "remessa")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemessaPendente {
	
	@XmlElement(name = "nome_arquivo")
	private List<String> arquivos;

	public List<String> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<String> arquivos) {
		this.arquivos = arquivos;
	}
}
