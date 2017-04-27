package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class TipoInstituicaoDAO extends AbstractBaseDAO {

	public TipoInstituicao alterar(TipoInstituicao tipoInstituicao) {
		Transaction transaction = getBeginTransation();

		try {
			update(tipoInstituicao);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			throw new InfraException("Não foi possível alterar o tipo de instituição. Favor entrar em contato com a CRA!");
		}
		return tipoInstituicao;
	}

	public void inserirTipoInstituicaoInicial(String tipo) {
		Transaction transaction = getBeginTransation();
		try {
			TipoInstituicao tipoInstituicao = new TipoInstituicao();
			tipoInstituicao.setId(Integer.parseInt(TipoInstituicaoCRA.get(tipo).getConstante()));
			tipoInstituicao.setTipoInstituicao(TipoInstituicaoCRA.get(tipo));
			save(tipoInstituicao);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<TipoInstituicao> buscarListaTipoInstituicao() {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.add(Restrictions.ne("tipoInstituicao", TipoInstituicaoCRA.CARTORIO));
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TipoInstituicao> listarTodos() {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}

	public TipoInstituicao buscarTipoInstituicao(TipoInstituicaoCRA tipoInstituicao) {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.add(Restrictions.eq("tipoInstituicao", tipoInstituicao));
		return TipoInstituicao.class.cast(criteria.uniqueResult());
	}
}