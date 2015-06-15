package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class UsuarioFiliadoDAO extends AbstractBaseDAO {

	public UsuarioFiliado salvar(UsuarioFiliado usuario) {
		UsuarioFiliado novo = new UsuarioFiliado();
		Transaction transaction = getBeginTransation();

		try {
			novo = save(usuario);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novo;
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioFiliado> listarUsuariosDoConvenio(Instituicao convenio) {
		Criteria criteria = getCriteria(UsuarioFiliado.class);
		return criteria.list();
	}

	public UsuarioFiliado buscarUsuarioFiliadoPorLogin(String login) {
		Criteria criteria = getCriteria(UsuarioFiliado.class);
		criteria.createAlias("usuario", "usuario");
		criteria.add(Restrictions.like("usuario.login", login, MatchMode.EXACT));

		return UsuarioFiliado.class.cast(criteria.uniqueResult());
	}
}
