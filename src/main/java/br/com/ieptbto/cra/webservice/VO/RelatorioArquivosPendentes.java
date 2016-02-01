package br.com.ieptbto.cra.webservice.VO;

import java.util.List;

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
public class RelatorioArquivosPendentes {

	@XmlElement(name = "remessa")
	private List<RemessaPendente> remessas;
	
	@XmlElement(name = "desistencia")
	private List<DesistenciaPendente> desistencias;
	
	@XmlElement(name = "cancelamento")
	private List<CancelamentoPendente> cancelamentos;
	
	@XmlElement(name = "autorizaCancelamento")
	private List<AutorizaCancelamentoPendente> autorizaCancelamentos;

	public List<RemessaPendente> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<RemessaPendente> remessas) {
		this.remessas = remessas;
	}

	public List<DesistenciaPendente> getDesistencias() {
		return desistencias;
	}

	public void setDesistencias(List<DesistenciaPendente> desistencias) {
		this.desistencias = desistencias;
	}

	public List<CancelamentoPendente> getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(List<CancelamentoPendente> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public List<AutorizaCancelamentoPendente> getAutorizaCancelamentos() {
		return autorizaCancelamentos;
	}

	public void setAutorizaCancelamentos(List<AutorizaCancelamentoPendente> autorizaCancelamentos) {
		this.autorizaCancelamentos = autorizaCancelamentos;
	}
}