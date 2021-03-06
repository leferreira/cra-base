package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.enumeration.TipoLog;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Thasso Aráujo
 *
 */
@Repository
public class LoggerDAO extends AbstractBaseDAO {

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public LogCra salvar(LogCra logCra) {
		Transaction transaction = getBeginTransation();
		LogCra novaLog = null;

		try {
			novaLog = save(logCra);

			transaction.commit();
			logger.info(logCra.toString());
		} catch (Exception ex) {
			transaction.rollback();
			logger.info(ex.getMessage(), ex);
		}
		return novaLog;
	}

	@SuppressWarnings("unchecked")
	public List<LogCra> buscarAcoes(LocalDate dataInicio, LocalDate dataFim, Instituicao instituicao) {
		Criteria criteria = getCriteria(LogCra.class);
		criteria.add(Restrictions.between("data", dataInicio, dataFim));

		if (instituicao != null) {
			criteria.add(Restrictions.ilike("instituicao", instituicao.getNomeFantasia(), MatchMode.ANYWHERE));
		} else {
			criteria.add(Restrictions.ne("tipoLog", TipoLog.ALERTA));
		}
		criteria.addOrder(Order.desc("id")).addOrder(Order.desc("data"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<LogCra> buscarUltimosLogDeErros() {
		Criteria criteria = getCriteria(LogCra.class);
		criteria.add(Restrictions.eq("tipoLog", TipoLog.OCORRENCIA_ERRO));
		criteria.addOrder(Order.desc("id"));
		criteria.setMaxResults(4);
		return criteria.list();
	}
}
