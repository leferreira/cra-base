package br.com.ieptbto.cra.webservice.VO;

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
	private RemessaPendente remessas;

	@XmlElement(name = "desistencia")
	private DesistenciaPendente desistencias;

	@XmlElement(name = "cancelamento")
	private CancelamentoPendente cancelamentos;

	@XmlElement(name = "autorizaCancelamento")
	private AutorizaCancelamentoPendente autorizaCancelamentos;

	public RemessaPendente getRemessas() {
		return remessas;
	}

	public void setRemessas(RemessaPendente remessas) {
		this.remessas = remessas;
	}

	public DesistenciaPendente getDesistencias() {
		return desistencias;
	}

	public void setDesistencias(DesistenciaPendente desistencias) {
		this.desistencias = desistencias;
	}

	public CancelamentoPendente getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(CancelamentoPendente cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public AutorizaCancelamentoPendente getAutorizaCancelamentos() {
		return autorizaCancelamentos;
	}

	public void setAutorizaCancelamentos(AutorizaCancelamentoPendente autorizaCancelamentos) {
		this.autorizaCancelamentos = autorizaCancelamentos;
	}
}