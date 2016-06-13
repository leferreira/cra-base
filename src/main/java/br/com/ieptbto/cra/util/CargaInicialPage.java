package br.com.ieptbto.cra.util;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;

/**
 * 
 * 
 */
public class CargaInicialPage extends WebPage {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	CentralNacionalProtestoMediator centralNacionalProtestoMediator;

	public CargaInicialPage() {
		String municipioParametro = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("codigoMunicipio").toString();

		if (municipioParametro != null) {
			if (!municipioParametro.trim().isEmpty()) {
				centralNacionalProtestoMediator.gerarArquivo5AnosPorMunicipio(municipioParametro);
			}
		} else {
			centralNacionalProtestoMediator.gerarArquivo5AnosTocantins();
		}
	}
}