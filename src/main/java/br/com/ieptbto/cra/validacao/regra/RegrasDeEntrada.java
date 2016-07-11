package br.com.ieptbto.cra.validacao.regra;

import java.util.List;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * 
 * @author Lefer
 *
 */
public abstract class RegrasDeEntrada extends Regra {

	protected Usuario usuario;

	protected Arquivo arquivo;

	protected abstract void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros);

}
