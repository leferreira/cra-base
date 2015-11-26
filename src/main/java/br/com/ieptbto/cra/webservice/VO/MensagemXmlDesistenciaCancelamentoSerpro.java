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
public class MensagemXmlDesistenciaCancelamentoSerpro {

	@XmlElement(name = "nome_arquivo")
	private String nomeArquivo;
	
	@XmlElement(name = "titulo")
	private List<TituloDetalhamentoSerpro> titulosDetalhamento;

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public List<TituloDetalhamentoSerpro> getTitulosDetalhamento() {
		return titulosDetalhamento;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setTitulosDetalhamento(List<TituloDetalhamentoSerpro> titulosDetalhamento) {
		this.titulosDetalhamento = titulosDetalhamento;
	}
}
