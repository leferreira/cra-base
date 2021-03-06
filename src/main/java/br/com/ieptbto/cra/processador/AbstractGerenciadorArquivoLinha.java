package br.com.ieptbto.cra.processador;

import br.com.ieptbto.cra.conversor.AbstractConversor;
import br.com.ieptbto.cra.conversor.AtributoArquivoUtil;
import br.com.ieptbto.cra.conversor.CampoArquivo;
import br.com.ieptbto.cra.conversor.arquivo.FabricaConversor;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;

import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
public abstract class AbstractGerenciadorArquivoLinha {

	protected AbstractArquivoVO arquivo;

	/**
	 * @return os campos anotados do registro.
	 */
	protected List<CampoArquivo> getAnnotatedFields() {
		return AtributoArquivoUtil.getAnnotatedFields(arquivo);
	}

	/**
	 * Retorna o conversor pelo tipo do campo.
	 * 
	 * @param campoRegistro
	 * @return
	 */
	protected AbstractConversor<?> getConversor(CampoArquivo campoRegistro) {
		return FabricaConversor.getConversor(campoRegistro, arquivo);
	}

}
