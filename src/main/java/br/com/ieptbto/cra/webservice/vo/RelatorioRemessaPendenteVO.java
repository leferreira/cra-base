package br.com.ieptbto.cra.webservice.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "remessa")
@XmlAccessorType(XmlAccessType.FIELD)
public class RelatorioRemessaPendenteVO {

	@XmlElement(name = "nome_arquivo")
	private List<String> nomeArquivos;

	public void setNomeArquivos(List<String> nomeArquivo) {
		this.nomeArquivos = nomeArquivo;
	}

	public List<String> getNomeArquivos() {
		return nomeArquivos;
	}
}