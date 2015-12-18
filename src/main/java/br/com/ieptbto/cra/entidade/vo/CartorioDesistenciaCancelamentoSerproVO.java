package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "cartorio")
@XmlAccessorType(XmlAccessType.FIELD)
public class CartorioDesistenciaCancelamentoSerproVO extends AbstractArquivoVO {

	@XmlElement(name = "numero_cartorio")
	private String codigoCartorio;
	
	@XmlElement(name = "titulo")
	private List<TituloDesistenciaCancelamentoSerproVO> tituloDesistenciaCancelamento;
	
	@Override
	public String getIdentificacaoRegistro() {
		return null;
	}

	public List<TituloDesistenciaCancelamentoSerproVO> getTituloDesistenciaCancelamento() {
		return tituloDesistenciaCancelamento;
	}

	public void setTituloDesistenciaCancelamento(
			List<TituloDesistenciaCancelamentoSerproVO> tituloDesistenciaCancelamento) {
		this.tituloDesistenciaCancelamento = tituloDesistenciaCancelamento;
	}

	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	public void setCodigoCartorio(String codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}
}
