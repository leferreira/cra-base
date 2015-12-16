package br.com.ieptbto.cra.conversor.arquivo;

import java.io.File;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.vo.RemessaCnp;

/**
 * 
 * @author Lefer
 *
 */
public abstract class Gerador {

	protected static final Logger logger = Logger.getLogger(AbstractFabricaDeArquivo.class);
	protected static final String NEW_LINE = "\r\n";
	protected RemessaCnp remessaVO;

	public void setRemessaVO(RemessaCnp remessaVO) {
		this.remessaVO = remessaVO;
	}

	public RemessaCnp getRemessaVO() {
		return remessaVO;
	}

	abstract void gerar(RemessaCnp remessaVO, File arquivoFisico);
}
