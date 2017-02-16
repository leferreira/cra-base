package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;
import br.com.ieptbto.cra.exception.InfraException;

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
			tipoInstituicao.setId(Integer.parseInt(TipoInstituicaoSistema.get(tipo).getConstante()));
			tipoInstituicao.setTipoInstituicao(TipoInstituicaoSistema.get(tipo));
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
		criteria.add(Restrictions.ne("tipoInstituicao", TipoInstituicaoSistema.CARTORIO));
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TipoInstituicao> listarTodos() {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}

	public TipoInstituicao buscarTipoInstituicao(TipoInstituicaoSistema tipoInstituicao) {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.add(Restrictions.eq("tipoInstituicao", tipoInstituicao));
		return TipoInstituicao.class.cast(criteria.uniqueResult());
	}
}