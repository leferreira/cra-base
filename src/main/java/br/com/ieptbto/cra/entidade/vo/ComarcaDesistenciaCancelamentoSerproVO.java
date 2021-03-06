package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "comarca")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComarcaDesistenciaCancelamentoSerproVO extends AbstractArquivoVO {

	@XmlElement(name = "CodMun")
	private String codigoMunicipio;

	@XmlElement(name = "cartorio")
	private CartorioDesistenciaCancelamentoSerproVO cartorioDesistenciaCancelamento;

	@Override
	public String getIdentificacaoRegistro() {
		return null;
	}

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public CartorioDesistenciaCancelamentoSerproVO getCartorioDesistenciaCancelamento() {
		return cartorioDesistenciaCancelamento;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public void setCartorioDesistenciaCancelamento(CartorioDesistenciaCancelamentoSerproVO cartorioDesistenciaCancelamento) {
		this.cartorioDesistenciaCancelamento = cartorioDesistenciaCancelamento;
	}
}