package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class UsuarioFiliadoDAO extends AbstractBaseDAO {
	
	public UsuarioFiliado salvar(UsuarioFiliado usuarioFiliado) {
		Usuario user = usuarioFiliado.getUsuario();
		UsuarioFiliado novoFiliado = new UsuarioFiliado();
		Transaction transaction = getBeginTransation();

		try {
			user.setSenha(Usuario.cryptPass(usuarioFiliado.getUsuario().getSenha()));
			Usuario usuarioSalvo = save(user);
			usuarioFiliado.setUsuario(usuarioSalvo);
			novoFiliado = save(usuarioFiliado);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível salvar os dados ! Entre em contato com o IEPTB !");
		}
		return novoFiliado;
	}

	public UsuarioFiliado alterar(UsuarioFiliado usuarioFiliado) {
		UsuarioFiliado alterado = new UsuarioFiliado();
		Transaction transaction = getBeginTransation();

		try {
//			usuarioFiliado.getUsuario().setSenha(Usuario.cryptPass(usuarioFiliado.getUsuario().getSenha()));
			update(usuarioFiliado.getUsuario());
			alterado = update(usuarioFiliado);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível alterar os dados ! Entre em contato com o IEPTB !");
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

	public UsuarioFiliado buscarUsuarioFiliado(Usuario usuario) {
		Criteria criteria = getCriteria(UsuarioFiliado.class);
		criteria.add(Restrictions.eq("usuario", usuario));
		return UsuarioFiliado.class.cast(criteria.uniqueResult());
	}

	public UsuarioFiliado confirmarAceiteTermosContrato(UsuarioFiliado usuarioFiliado) {
		UsuarioFiliado usuarioAlterado = new UsuarioFiliado();
		Transaction transaction = getBeginTransation();
		
		try {
			usuarioFiliado.setTermosContratoAceite(true);
			usuarioFiliado.setDataAceiteContrato(new LocalDateTime());
			
			usuarioAlterado = update(usuarioFiliado);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return usuarioAlterado;
	}

	public UsuarioFiliado naoAceiteTermosContrato(UsuarioFiliado usuarioFiliado) {
		UsuarioFiliado usuarioAlterado = new UsuarioFiliado();
		Transaction transaction = getBeginTransation();
		
		try {
			Usuario user = usuarioFiliado.getUsuario();
			user.setStatus(false);
			update(user);
			
			usuarioFiliado.setTermosContratoAceite(false);
			usuarioAlterado = update(usuarioFiliado);
			
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return usuarioAlterado;
	}
}
