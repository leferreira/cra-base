package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * 
 * @author Lefer
 *
 */
public abstract class RegraValidacao {

	protected static final Logger logger = Logger.getLogger(RegraValidacao.class);

	protected abstract void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros);
}