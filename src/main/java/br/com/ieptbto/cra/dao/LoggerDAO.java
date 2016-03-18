package br.com.ieptbto.cra.dao;

import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Aráujo
 *
 */
@Repository
public class LoggerDAO extends AbstractBaseDAO {

    public LogCra salvar(LogCra logCra) {
	Transaction transaction = getBeginTransation();
	LogCra novaLog = null;

	try {
	    novaLog = save(logCra);

	    transaction.commit();
	    logger.info(logCra.toString());
	} catch (Exception ex) {
	    transaction.rollback();
	    logger.info(ex.getMessage(), ex.getCause());
	    throw new InfraException("Não foi possível registrar o log da ação!");
	}
	return novaLog;
    }
}
