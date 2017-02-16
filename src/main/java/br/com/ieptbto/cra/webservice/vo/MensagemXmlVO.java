package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Leandro
 * 
 */
@XmlRootElement(name = "relatorio")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "descricao", "detalhamento", "codigoFinal", "descricaoFinal" })
public class MensagemXmlVO extends AbstractMensagemVO {

	@XmlElement(name = "descricao")
	private DescricaoVO descricao;

	@XmlElement(name = "final")
	private String codigoFinal;

	@XmlElement(name = "descricao_final")
	private String descricaoFinal;

	@XmlElement(name = "detalhamento")
	private DetalhamentoVO detalhamento;

	public DescricaoVO getDescricao() {
		return descricao;
	}

	public String getCodigoFinal() {
		return codigoFinal;
	}

	public String getDescricaoFinal() {
		return descricaoFinal;
	}

	public DetalhamentoVO getDetalhamento() {
		return detalhamento;
	}

	public void setDescricao(DescricaoVO descricao) {
		this.descricao = descricao;
	}

	public void setCodigoFinal(String codigoFinal) {
		this.codigoFinal = codigoFinal;
	}

	public void setDescricaoFinal(String descricaoFinal) {
		this.descricaoFinal = descricaoFinal;
	}

	public void setDetalhamento(DetalhamentoVO detalhamento) {
		this.detalhamento = detalhamento;
	}
}