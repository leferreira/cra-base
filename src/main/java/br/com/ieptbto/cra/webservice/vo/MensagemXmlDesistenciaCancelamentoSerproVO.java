package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "relatorio")
@XmlAccessorType(XmlAccessType.FIELD)
public class MensagemXmlDesistenciaCancelamentoSerproVO {

	@XmlElement(name = "nome_arquivo")
	private String nomeArquivo;
	
	@XmlElement(name = "titulo")
	private List<TituloDetalhamentoSerproVO> titulosDetalhamento;

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public List<TituloDetalhamentoSerproVO> getTitulosDetalhamento() {
		return titulosDetalhamento;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setTitulosDetalhamento(List<TituloDetalhamentoSerproVO> titulosDetalhamento) {
		this.titulosDetalhamento = titulosDetalhamento;
	}
}
