package br.com.ieptbto.cra.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.DadosArquivoRecebidoDao;
import br.com.ieptbto.cra.entidade.DadosArquivoRecebido;

/**
 * 
 * @author Leandro
 *
 */
@Service
public class DadosArquivoRecebidoMediator {

	@Autowired
	private DadosArquivoRecebidoDao dadosArquivoRecebidoDao;

	/**
	 * Método responsável por salvar os dados recebido pelo WS
	 * 
	 * @param dadosArquivoRecebido
	 */

	public DadosArquivoRecebido salvarDados(DadosArquivoRecebido dadosArquivoRecebido) {
		return dadosArquivoRecebidoDao.salvar(dadosArquivoRecebido);
	}
}