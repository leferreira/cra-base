package br.com.ieptbto.cra.webservice.VO;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Thasso Araújo
 *
 */
public class MensagemErroCartorio extends Mensagem {

	@XmlElement(name = "nosso_numero")
	private String nossoNumero;

	@XmlElement(name = "numero_sequencial_registro")
	private int numeroSequencialRegistro;

	public String getNossoNumero() {
		return nossoNumero;
	}

	public int getNumeroSequencialRegistro() {
		return numeroSequencialRegistro;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setNumeroSequencialRegistro(int numeroSequencialRegistro) {
		this.numeroSequencialRegistro = numeroSequencialRegistro;
	}
}