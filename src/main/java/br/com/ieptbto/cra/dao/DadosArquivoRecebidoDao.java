package br.com.ieptbto.cra.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.DadosArquivoRecebido;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Leandro
 *
 */
@Repository
public class DadosArquivoRecebidoDao extends AbstractBaseDAO {

    /**
     * Método responsável por persistir os dadosRecebidos pelo WS na base de
     * dados
     * 
     * @param dadosArquivoRecebido
     * @return
     */
    @Transactional
    public DadosArquivoRecebido salvar(DadosArquivoRecebido dadosArquivoRecebido) {
        try {

            return save(dadosArquivoRecebido);

        } catch (Exception ex) {
            new InfraException("Não foi possível inserir os dados recebidos pelo WS na base de dados");
            logger.error(ex.getMessage(), ex);
        }

        return null;

    }

}
