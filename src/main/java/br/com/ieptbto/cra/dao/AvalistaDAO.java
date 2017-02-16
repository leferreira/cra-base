package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.TituloFiliado;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class AvalistaDAO extends AbstractBaseDAO {

	/**
	 * Salva ou atualiza os dados de um avalista
	 * @param avalista
	 * @return
	 */
	public Avalista saveOrUpdate(Avalista avalista) {
		Avalista novoAvalista = new Avalista();
		try {
			if (avalista.getId() != 0) {
				novoAvalista = update(avalista);
			} else {
				novoAvalista = save(avalista);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return novoAvalista;
	}

	/**
	 * Removar avalista
	 * 
	 * @param avalista
	 */
	public Avalista remover(Avalista avalista) {
		Transaction transaction = getBeginTransation();
		try {
			delete(avalista);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return avalista;
	}

	/**
	 * Busca avalistas cadastrados e vínculados por título filiado
	 * 
	 * @param titulo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Avalista> buscarAvalistasPorTitulo(TituloFiliado titulo) {
		Criteria criteria = getCriteria(Avalista.class);
		criteria.add(Restrictions.eq("tituloFiliado", titulo));
		return criteria.list();
	}
}
