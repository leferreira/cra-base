package br.com.ieptbto.cra.slip.regra;

import br.com.ieptbto.cra.entidade.TituloRemessa;

/**
 * @author Thasso Araújo
 *
 */
public class RegraBancoDoBrasilAgencia extends RegraAgenciaBanco {

	@Override
	public String aplicarRegraEspecifica(TituloRemessa titulo) {
		String registroTransacao = "1001" + titulo.getAgenciaCodigoCedente();
		return registroTransacao.substring(5, 14);
	}
}
