package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
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
}
