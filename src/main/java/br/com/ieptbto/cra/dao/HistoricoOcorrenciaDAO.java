package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.HistoricoOcorrenciaTitulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class HistoricoOcorrenciaDAO extends AbstractBaseDAO {

	public HistoricoOcorrenciaTitulo salvarHistoricoOcorrencia(
			HistoricoOcorrenciaTitulo historicoOcorrenciaTitulo) {
		HistoricoOcorrenciaTitulo novoHistorico = new HistoricoOcorrenciaTitulo();
		Transaction transaction = getBeginTransation();
		try {
			novoHistorico = save(historicoOcorrenciaTitulo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novoHistorico;
	}

	@SuppressWarnings("unchecked")
	public List<Historico> buscarHistoricoTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(Historico.class);
		criteria.add(Restrictions.eq("titulo",titulo));
		return criteria.list();
	}
}
