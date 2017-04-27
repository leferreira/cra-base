package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.TaxaCra;
import br.com.ieptbto.cra.exception.InfraException;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class TaxaCraDAO extends AbstractBaseDAO {

	public TaxaCra buscarTaxaCraVigente(LocalDate data) {
		Criteria criteria = getCriteria(TaxaCra.class);
		criteria.add(Restrictions.le("dataInicio", data.toDate()));
		criteria.add(Restrictions.ge("dataFim", data.toDate()));
		criteria.setMaxResults(1);
		criteria.addOrder(Order.desc("id"));
		return TaxaCra.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<TaxaCra> buscarTaxasCra() {
		Criteria criteria = getCriteria(TaxaCra.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	public TaxaCra salvarTaxa(TaxaCra taxaCra, TaxaCra taxaCraAtual) {
		Transaction transaction = getBeginTransation();
		try {
			update(taxaCraAtual);
			taxaCra = save(taxaCra);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível salvar a nova vigência de taxa cra na base de dados.");
		}
		return taxaCra;
	}

	@SuppressWarnings("unchecked")
	public List<TaxaCra> buscarTaxasComDataInicialNoPeriodo(TaxaCra taxa) {
		Criteria criteria = getCriteria(TaxaCra.class);
		criteria.add(Restrictions.ge("dataInicio", taxa.getDataInicio()));
		criteria.add(Restrictions.le("dataFim", taxa.getDataInicio()));
		if (taxa.getId() != 0) {
			criteria.add(Restrictions.ne("id", taxa.getId()));
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TaxaCra> buscarTaxasComDataFimNoPeriodo(Date dataFim) {
		Criteria criteria = getCriteria(TaxaCra.class);
		criteria.add(Restrictions.le("dataInicio", dataFim));
		return criteria.list();
	}
}