package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 
 * @author Leandro
 * 
 */
@XmlRootElement(name = "detalhamento")
@XmlAccessorType(XmlAccessType.FIELD)
public class DetalhamentoVO {

	@XmlElement(name = "mensagem")
	private List<MensagemVO> mensagem;

	@XmlElement(name = "erro")
	private List<ErroVO> erro;

	public List<MensagemVO> getMensagem() {
		return mensagem;
	}

	public List<ErroVO> getErro() {
		return erro;
	}

	public void setMensagem(List<MensagemVO> mensagem) {
		this.mensagem = mensagem;
	}

	public void setErro(List<ErroVO> erro) {
		this.erro = erro;
	}

}
