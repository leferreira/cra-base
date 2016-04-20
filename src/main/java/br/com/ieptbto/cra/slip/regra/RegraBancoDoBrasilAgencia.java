package br.com.ieptbto.cra.slip.regra;

import br.com.ieptbto.cra.entidade.TituloRemessa;

/**
 * @author Thasso Araújo
 *
 */
public class RegraBancoDoBrasilAgencia extends RegraAgenciaBanco {

	@Override
	public String aplicarRegraEspecifica(TituloRemessa titulo) {
		return titulo.getAgenciaCodigoCedente().substring(2, 10);
	}
}
