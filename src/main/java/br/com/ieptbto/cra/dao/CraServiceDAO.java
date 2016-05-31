package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.CraServiceConfig;

@Repository
public class CraServiceDAO extends AbstractBaseDAO {

	public CraServiceConfig atualizarStatusServico(CraServiceConfig service) {
		Transaction transaction = getBeginTransation();
		try {
			service = update(service);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return service;
	}

	@SuppressWarnings("unchecked")
	public List<CraServiceConfig> carregarServicos() {
		Criteria criteria = getCriteria(CraServiceConfig.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public void salvarServicos(List<CraServiceConfig> services) {
		Transaction transaction = getBeginTransation();
		try {
			for (CraServiceConfig service : services) {
				update(service);
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
	}
}
