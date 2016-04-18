package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.LoggerDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.enumeration.TipoLog;

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

	public List<LogCra> buscarAcoes(LocalDate dataInicio, LocalDate dataFim, Instituicao instituicao, TipoLog tipoLog) {
		return loggerDAO.buscarAcoes(dataInicio, dataFim, instituicao, tipoLog);
	}
}
