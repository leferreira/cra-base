package br.com.ieptbto.cra.webservice.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "desistencia")
@XmlAccessorType(XmlAccessType.FIELD)
public class RelatorioDesistenciaPendenteVO {
	
	@XmlElement(name = "nome_arquivo")
	private List<String> arquivos;

	public List<String> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<String> arquivos) {
		this.arquivos = arquivos;
	}
}
