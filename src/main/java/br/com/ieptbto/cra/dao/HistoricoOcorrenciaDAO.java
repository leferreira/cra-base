package br.com.ieptbto.cra.dao;

import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.HistoricoOcorrenciaTitulo;

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

}
