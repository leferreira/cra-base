package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.ieptbto.cra.enumeration.TipoRegistro;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "rodape")
@XmlAccessorType(XmlAccessType.NONE)
public class RodapeCnpVO extends AbstractArquivoVO {

	/***/
	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private String codigoRegistro;

	@XmlAttribute(required = true)
	private String emBranco2;

	@XmlAttribute(required = true)
	private String sequenciaRegistro;

	public String getCodigoRegistro() {
		return codigoRegistro;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public String getEmBranco2() {
		return emBranco2;
	}

	public void setEmBranco2(String emBranco2) {
		this.emBranco2 = emBranco2;
	}

	public String getSequenciaRegistro() {
		return sequenciaRegistro;
	}

	public void setSequenciaRegistro(String sequenciaRegistro) {
		this.sequenciaRegistro = sequenciaRegistro;
	}

	@Override
	public String getIdentificacaoRegistro() {
		return TipoRegistro.RODAPE.getConstante();
	}
}
