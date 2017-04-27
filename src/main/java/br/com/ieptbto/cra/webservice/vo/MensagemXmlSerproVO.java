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
public class MensagemXmlSerproVO extends AbstractMensagemVO {

	@XmlElement(name = "nome_arquivo")
	private String nomeArquivo;

	@XmlElement(name = "comarca")
	private List<ComarcaDetalhamentoSerproVO> comarca;

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public List<ComarcaDetalhamentoSerproVO> getComarca() {
		return comarca;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setComarca(List<ComarcaDetalhamentoSerproVO> comarca) {
		this.comarca = comarca;
	}
}
