package br.com.ieptbto.cra.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.DadosArquivoRecebido;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Leandro
 *
 */
@Repository
public class DadosArquivoRecebidoDao extends AbstractBaseDAO {

	/**
	 * Método responsável por persistir os dadosRecebidos pelo WS na base de
	 * dados
	 * 
	 * @param dadosArquivoRecebido
	 * @return
	 */
	@Transactional
	public DadosArquivoRecebido salvar(DadosArquivoRecebido dadosArquivoRecebido) {
		try {

			return save(dadosArquivoRecebido);

		} catch (Exception ex) {
			new InfraException("Não foi possível inserir os dados recebidos pelo WS na base de dados");
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<DadosArquivoRecebido> buscarDados(CraAcao acao, Date dataInicio, Date dataFim) {
		Criteria criteria = getCriteria(DadosArquivoRecebido.class);
		criteria.add(Restrictions.ilike("servico", acao.getNomeServico(), MatchMode.EXACT));
		criteria.add(Restrictions.sqlRestriction("DATE(data_recebimento) >= ?", dataInicio, org.hibernate.type.StandardBasicTypes.DATE));
		criteria.add(Restrictions.sqlRestriction("DATE(data_recebimento) <= ?", dataFim, org.hibernate.type.StandardBasicTypes.DATE));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
}