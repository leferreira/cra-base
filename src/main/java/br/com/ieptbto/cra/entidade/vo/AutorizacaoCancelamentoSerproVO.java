package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "autoriza_cancelamento") 
@XmlAccessorType(XmlAccessType.NONE)
public class AutorizacaoCancelamentoSerproVO extends AbstractArquivoVO {
	
	@XmlElement(name = "comarca")
	private List<ComarcaDesistenciaCancelamentoSerproVO> comarcaDesistenciaCancelamento;
	
	@Override
	public String getIdentificacaoRegistro() {
		return null;
	}

	public List<ComarcaDesistenciaCancelamentoSerproVO> getComarcaDesistenciaCancelamento() {
		return comarcaDesistenciaCancelamento;
	}

	public void setComarcaDesistenciaCancelamento(List<ComarcaDesistenciaCancelamentoSerproVO> comarcaDesistenciaCancelamento) {
		this.comarcaDesistenciaCancelamento = comarcaDesistenciaCancelamento;
	}
}
