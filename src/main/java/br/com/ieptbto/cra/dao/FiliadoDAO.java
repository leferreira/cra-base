package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class FiliadoDAO extends AbstractBaseDAO {

	public Filiado salvar(Filiado filiado) {
		Filiado novo = new Filiado();
		Transaction transaction = getBeginTransation();
		
		try {
			novo = save(filiado);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novo;
	}

	@SuppressWarnings("unchecked")
	public List<Filiado> buscarListaFiliadosPorConvenio(Instituicao instituicao) {
		Criteria criteria = getCriteria(Filiado.class);
		criteria.createAlias("instituicaoConvenio", "instituicaoConvenio");
		criteria.add(Restrictions.eq("instituicaoConvenio", instituicao));
		return criteria.list();
	}


}
