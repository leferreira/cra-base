package br.com.ieptbto.cra.webservice.VO;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "relatorio")
@XmlAccessorType(XmlAccessType.FIELD)
public class MensagemXmlSerpro {

	@XmlElement(name = "nome_arquivo")
	private String nomeArquivo;
	
	@XmlElement(name = "comarca")
	private List<ComarcaDetalhamentoSerpro> comarca;

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public List<ComarcaDetalhamentoSerpro> getComarca() {
		return comarca;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setComarca(List<ComarcaDetalhamentoSerpro> comarca) {
		this.comarca = comarca;
	}
}
