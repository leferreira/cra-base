package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
@Repository
public class LayoutFiliadoDao extends AbstractBaseDAO {

	public void salvar(List<LayoutFiliado> listaCampos) {
		Transaction transaction = getBeginTransation();
		try {
			for (LayoutFiliado layoutFiliado : listaCampos) {
				save(layoutFiliado);
			}
			transaction.commit();

		} catch (Exception e) {
			transaction.rollback();
			logger.error(e.getMessage());
			throw new InfraException("Não foi possível inserir o layout.");
		}
	}

}
