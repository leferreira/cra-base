package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.LoggerDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LogCra;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Thasso Aráujo
 *
 */
@Service
public class LoggerMediator extends BaseMediator {

	@Autowired
	private LoggerDAO loggerDAO;

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly=false)
	public LogCra salvar(LogCra log) {
		return loggerDAO.salvar(log);
	}

	public List<LogCra> buscarAcoes(LocalDate dataInicio, LocalDate dataFim, Instituicao instituicao) {
		return loggerDAO.buscarAcoes(dataInicio, dataFim, instituicao);
	}

	public List<LogCra> buscarUltimosLogDeErros() {
		return loggerDAO.buscarUltimosLogDeErros();
	}
}
