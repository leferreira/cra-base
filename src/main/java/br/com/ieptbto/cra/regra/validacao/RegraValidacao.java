package br.com.ieptbto.cra.regra.validacao;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
public abstract class RegraValidacao {

	protected static final Logger logger = Logger.getLogger(RegraValidacao.class);

	protected abstract void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros);
}