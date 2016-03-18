package br.com.ieptbto.cra.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.LoggerDAO;
import br.com.ieptbto.cra.entidade.LogCra;

/**
 * @author Thasso Aráujo
 *
 */
@Service
public class LoggerMediator {

    @Autowired
    LoggerDAO loggerDAO;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public LogCra salvar(LogCra log) {
	return loggerDAO.salvar(log);
    }
}
