package br.com.ieptbto.cra.slip.regra;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.AgenciasEspeciaisItau;

/**
 * @author Thasso Araújo
 *
 */
public class RegraItauAgencia extends RegraAgenciaBanco {

	@Override
	public String aplicarRegraEspecifica(TituloRemessa titulo) {
		
		return aplicarRegraAgencia0933(titulo);
	}

	private String aplicarRegraAgencia0933(TituloRemessa titulo) {
		String agenciaItauUnibanco = titulo.getAgenciaCodigoCedente().substring(0, 3);
		if (agenciaItauUnibanco.equals(AgenciasEspeciaisItau.AGENCIA_0933.getAgencia())) {
			setAgencia(titulo.getNumeroTitulo().substring(0,3));
			return getAgencia();
		}
		return null;
	}
}
