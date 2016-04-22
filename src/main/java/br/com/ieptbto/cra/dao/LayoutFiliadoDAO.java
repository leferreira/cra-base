package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.enumeration.TipoArquivoLayoutEmpresa;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
@Repository
public class LayoutFiliadoDAO extends AbstractBaseDAO {

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

	public List<LayoutFiliado> buscarEmpresas() {
		Criteria criteria = getCriteria(LayoutFiliado.class);
		List<LayoutFiliado> lista = new ArrayList<>();

		criteria.setProjection(
		        Projections.projectionList().add(Projections.groupProperty("empresa")).add(Projections.groupProperty("tipoArquivo")));

		for (Object objetos : criteria.list()) {
			Object[] objeto = (Object[]) objetos;
			LayoutFiliado layout = new LayoutFiliado();
			layout.setEmpresa(Instituicao.class.cast(objeto[0]));
			layout.setTipoArquivo(TipoArquivoLayoutEmpresa.get(String.valueOf(objeto[1])));
			lista.add(layout);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<LayoutFiliado> buscarLayout(Instituicao instituicao) {
		Criteria criteria = getCriteria(LayoutFiliado.class);
		criteria.createAlias("empresa", "empresa");
		criteria.add(Restrictions.eq("empresa", instituicao));

		return criteria.list();
	}

	public boolean isLayout(Instituicao instituicao) {
		if (!buscarLayout(instituicao).isEmpty()) {
			return true;
		}
		return false;
	}

}
