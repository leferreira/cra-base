package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoDeposito;
import br.com.ieptbto.cra.exception.InfraException;

@Repository
public class DepositoDAO extends AbstractBaseDAO {

	/**
	 * Salvar deposito
	 * @param deposito
	 * @return
	 */
	public Deposito salvarDeposito(Deposito deposito) {
		Transaction transaction = getBeginTransation();

		try {
			deposito = save(deposito);
			transaction.commit();
			
		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir os depósitos na base de dados! Favor entrar em contato com a CRA...");
		}
		return deposito;
	}

	/**
	 * Atualizar deposito
	 * @param deposito
	 * @return
	 */
	public Deposito atualizarDeposito(Deposito deposito) {
		Transaction transaction = getSession().beginTransaction();

		try {
			update(deposito);
			transaction.commit();

		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
			throw new InfraException("Não foi possível atualizar os depósitos na base de dados! Favor entrar em contato com a CRA...");
		}
		return deposito;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Deposito> buscarDepositosNaoIdentificados() {
//		Criteria criteria = getCriteria(Deposito.class);
//		criteria.addOrder(Order.asc("data"));
//		criteria.add(Restrictions.eq("situacaoDeposito", SituacaoDeposito.NAO_IDENTIFICADO));
//		return criteria.list();
		Query query = createQuery("select d from Deposito d where d.situacaoDeposito= :situacaoDeposito order by data asc");
		query.setParameter("situacaoDeposito", SituacaoDeposito.NAO_IDENTIFICADO);
		return query.list();
	}

	/**
	 * @param deposito
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Deposito> consultarDepositos(Deposito deposito, LocalDate dataInicio, LocalDate dataFim) {
		Criteria criteria = getCriteria(Deposito.class);

		if (dataInicio != null) {
			criteria.add(Restrictions.between("data", dataInicio, dataFim));
		}
		if (deposito.getNumeroDocumento() != null) {
			criteria.add(Restrictions.ilike("numeroDocumento", deposito.getNumeroDocumento(), MatchMode.ANYWHERE));
		}
		if (deposito.getValorCredito() != null) {
			criteria.add(Restrictions.eq("valorCredito", deposito.getValorCredito()));
		}
		if (deposito.getSituacaoDeposito() != null) {
			criteria.add(Restrictions.eq("situacaoDeposito", deposito.getSituacaoDeposito()));
		}
		if (!deposito.getTipoDeposito().equals(TipoDeposito.NAO_INFORMADO)) {
			criteria.add(Restrictions.eq("tipoDeposito", deposito.getTipoDeposito()));
		}
		criteria.addOrder(Order.asc("data"));
		return criteria.list();
	}
}
