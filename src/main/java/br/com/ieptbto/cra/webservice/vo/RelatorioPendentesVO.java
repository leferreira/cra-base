package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "pendente")
@XmlAccessorType(XmlAccessType.FIELD)
public class RelatorioPendentesVO {

	@XmlElement(name = "remessa")
	private RelatorioRemessaPendenteVO remessas;

	@XmlElement(name = "desistencia")
	private RelatorioDesistenciaPendenteVO desistencias;

	@XmlElement(name = "cancelamento")
	private RelatorioCancelamentoPendenteVO cancelamentos;

	@XmlElement(name = "autorizaCancelamento")
	private RelatorioAutorizacaoPendenteVO autorizaCancelamentos;

	public RelatorioRemessaPendenteVO getRemessas() {
		return remessas;
	}

	public void setRemessas(RelatorioRemessaPendenteVO remessas) {
		this.remessas = remessas;
	}

	public RelatorioDesistenciaPendenteVO getDesistencias() {
		return desistencias;
	}

	public void setDesistencias(RelatorioDesistenciaPendenteVO desistencias) {
		this.desistencias = desistencias;
	}

	public RelatorioCancelamentoPendenteVO getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(RelatorioCancelamentoPendenteVO cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public RelatorioAutorizacaoPendenteVO getAutorizaCancelamentos() {
		return autorizaCancelamentos;
	}

	public void setAutorizaCancelamentos(RelatorioAutorizacaoPendenteVO autorizaCancelamentos) {
		this.autorizaCancelamentos = autorizaCancelamentos;
	}
}