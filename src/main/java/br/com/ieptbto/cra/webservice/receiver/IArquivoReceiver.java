package br.com.ieptbto.cra.webservice.receiver;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;

/**
 * Interface de arquivos a serem recebidos pelo ws
 * 
 * @author Thasso Araújo
 *
 */
public interface IArquivoReceiver {

	public MensagemCra receber(Usuario usuario, String nomeArquivo, String dados);
}
