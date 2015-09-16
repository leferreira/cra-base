package br.com.ieptbto.cra.conversor.arquivo;

import java.io.File;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.vo.RemessaVO;

/**
 * 
 * @author Lefer
 *
 */
public abstract class Gerador {

	protected static final Logger logger = Logger.getLogger(AbstractFabricaDeArquivo.class);
	protected static final String NEW_LINE = "\r\n";
	protected RemessaVO remessaVO;

	public void setRemessaVO(RemessaVO remessaVO) {
		this.remessaVO = remessaVO;
	}

	public RemessaVO getRemessaVO() {
		return remessaVO;
	}

	abstract void gerar(RemessaVO remessaVO, File arquivoFisico);
}
