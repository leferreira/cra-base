package br.com.ieptbto.cra.webservice.receiver;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.logger.LoggerCra;
import br.com.ieptbto.cra.webservice.vo.AbstractMensagemVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Interface de arquivos a serem recebidos pelo ws
 * 
 * @author Thasso Araújo
 *
 */
public abstract class AbstractArquivoReceiver {

	protected static final Logger logger = Logger.getLogger(RemessaReceiver.class);

	@Autowired
	protected LoggerCra loggerCra;

	public abstract AbstractMensagemVO receber(Usuario usuario, String nomeArquivo, String dados);
}
