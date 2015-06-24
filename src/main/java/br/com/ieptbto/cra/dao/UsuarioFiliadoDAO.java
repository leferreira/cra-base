package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class UsuarioFiliadoDAO extends AbstractBaseDAO {

	public UsuarioFiliado salvar(UsuarioFiliado usuarioFiliado) {
		UsuarioFiliado novoFiliado = new UsuarioFiliado();
		Transaction transaction = getBeginTransation();

		try {
			usuarioFiliado.getUsuario().setSenha(Usuario.cryptPass(usuarioFiliado.getUsuario().getSenha()));
			novoFiliado = save(usuarioFiliado);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return novoFiliado;
	}

	public UsuarioFiliado alterar(UsuarioFiliado usuarioFiliado) {
		UsuarioFiliado alterado = new UsuarioFiliado();
		Transaction transaction = getBeginTransation();

		try {
			usuarioFiliado.getUsuario().setSenha(Usuario.cryptPass(usuarioFiliado.getUsuario().getSenha()));
			update(usuarioFiliado.getUsuario());
			alterado = update(usuarioFiliado);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return alterado;
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioFiliado> listarUsuariosDoConvenio(Usuario usuario) {
		Criteria criteria = getCriteria(UsuarioFiliado.class);
		criteria.createAlias("usuario", "usuario");
		criteria.add(Restrictions.eq("usuario.instituicao", usuario.getInstituicao()));

		return criteria.list();
	}

	public UsuarioFiliado buscarUsuarioFiliadoPorLogin(String login) {
		Criteria criteria = getCriteria(UsuarioFiliado.class);
		criteria.createAlias("usuario", "usuario");
		criteria.add(Restrictions.like("usuario.login", login, MatchMode.EXACT));

		return UsuarioFiliado.class.cast(criteria.uniqueResult());
	}

	public Filiado buscarEmpresaFiliadaDoUsuario(Usuario user) {
		Criteria criteria = getCriteria(UsuarioFiliado.class);
		criteria.createAlias("usuario", "usuario");
		criteria.createAlias("filiado", "filiado");
		criteria.add(Restrictions.eq("usuario", user));
		return UsuarioFiliado.class.cast(criteria.uniqueResult()).getFiliado();
	}
}
