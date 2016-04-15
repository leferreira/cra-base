package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.enumeration.TipoLog;
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

	@SuppressWarnings("unchecked")
	public List<LogCra> buscarAcoes(LocalDate dataInicio, LocalDate dataFim, Instituicao instituicao, TipoLog tipoLog) {
		Criteria criteria = getCriteria(LogCra.class);
		criteria.add(Restrictions.between("data", dataInicio, dataFim));

		if (instituicao != null) {
			criteria.add(Restrictions.ilike("instituicao", instituicao.getNomeFantasia(), MatchMode.ANYWHERE));
		}
		if (tipoLog != null) {
			criteria.add(Restrictions.eq("tipoLog", tipoLog));
		}
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}
}
