package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoSolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;
import br.com.ieptbto.cra.exception.InfraException;

@Repository
public class SolicitacaoDesistenciaCancelamentoDAO extends AbstractBaseDAO{

	public SolicitacaoDesistenciaCancelamento salvarSolicitacaoDesistenciaCancelamento(SolicitacaoDesistenciaCancelamento solicitacaoCancelamento) {
		Transaction transaction = getBeginTransation();

		try {
			save(solicitacaoCancelamento);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return solicitacaoCancelamento;
	}
	
	public void alterarSolicitacoesParaEnviadas(List<SolicitacaoDesistenciaCancelamento> solicitacoesCancelamento) {
		Transaction transaction = getBeginTransation();

		try {
			for (SolicitacaoDesistenciaCancelamento solicitacaoCancelamento : solicitacoesCancelamento) {
				solicitacaoCancelamento.setStatusLiberacao(true);
				update(solicitacaoCancelamento);
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi salvar os cancelamentos como enviados mas os arquivos foram gerados !");
		}
	}


	@SuppressWarnings("unchecked")
	public List<SolicitacaoDesistenciaCancelamento> buscarCancelamentosSolicitados() {
		Criteria criteria = getCriteria(SolicitacaoDesistenciaCancelamento.class);
		criteria.createAlias("tituloRemessa", "tituloRemessa");
		criteria.add(Restrictions.eq("statusLiberacao", false));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoDesistenciaCancelamento> buscarSolicitacoesDesistenciasCancelamentoPorTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(SolicitacaoDesistenciaCancelamento.class);
		criteria.add(Restrictions.eq("tituloRemessa", titulo));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public SolicitacaoDesistenciaCancelamento verificarSolicitadoAnteriormente(
			SolicitacaoDesistenciaCancelamento solicitacaoDesistenciaCancelamento) {
		Criteria criteria = getCriteria(SolicitacaoDesistenciaCancelamento.class);
		criteria.add(Restrictions.eq("tituloRemessa", solicitacaoDesistenciaCancelamento.getTituloRemessa()));
		criteria.add(Restrictions.eq("tipoSolicitacao", solicitacaoDesistenciaCancelamento.getTipoSolicitacao()));
		criteria.setMaxResults(1);
		criteria.addOrder(Order.desc("id"));
		return SolicitacaoDesistenciaCancelamento.class.cast(criteria.uniqueResult());
	}

	public SolicitacaoDesistenciaCancelamento buscarSolicitacaoDesistenciaCancelamento(String nossoNumero,
			TipoSolicitacaoDesistenciaCancelamento tipoSolicitacao, CodigoIrregularidade codigoIrregularidade) {
		Criteria criteria = getCriteria(SolicitacaoDesistenciaCancelamento.class);
		criteria.createAlias("tituloRemessa", "tituloRemessa");
		criteria.add(Restrictions.eq("tituloRemessa.nossoNumero", nossoNumero));
		criteria.add(Restrictions.eq("tipoSolicitacao", tipoSolicitacao));
		criteria.add(Restrictions.eq("codigoIrregularidade", codigoIrregularidade));
		criteria.setMaxResults(1);
		criteria.addOrder(Order.desc("id"));
		return SolicitacaoDesistenciaCancelamento.class.cast(criteria.uniqueResult());
	}
}
