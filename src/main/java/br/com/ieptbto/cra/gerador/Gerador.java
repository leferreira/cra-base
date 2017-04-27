package br.com.ieptbto.cra.gerador;

import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * 
 * @author Lefer
 *
 */
public abstract class Gerador {

	protected static final Logger logger = Logger.getLogger(Gerador.class);
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
