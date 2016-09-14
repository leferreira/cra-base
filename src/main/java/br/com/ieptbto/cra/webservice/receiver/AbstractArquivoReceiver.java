package br.com.ieptbto.cra.webservice.receiver;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;

/**
 * Interface de arquivos a serem recebidos pelo ws
 * 
 * @author Thasso Araújo
 *
 */
public abstract class AbstractArquivoReceiver {

	protected static final Logger logger = Logger.getLogger(RemessaReceiver.class);

	public abstract MensagemCra receber(Usuario usuario, String nomeArquivo, String dados);
}
