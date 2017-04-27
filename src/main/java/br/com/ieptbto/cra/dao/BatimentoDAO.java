package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.view.ViewBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BatimentoDAO extends AbstractBaseDAO {

	@Autowired
	RemessaDAO remessaDAO;

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosParaBatimento() {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.NAO_CONFIRMADO));
		criteria.add(Restrictions.eq("situacao", false));
		return criteria.list();
	}
	
	/**
	 * @param instiuicao
	 * @param dataBatimento
	 * @param dataComoDataLimite
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosAguardandoLiberacao(Instituicao instiuicao, LocalDate dataBatimento, boolean dataComoDataLimite) {
		if (dataBatimento == null) {
			return new ArrayList<Remessa>();
		}

		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("batimento", "batimento");
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO));
		criteria.add(Restrictions.eq("instituicaoDestino", instiuicao));
		criteria.add(Restrictions.eq("situacao", false));
		if (dataComoDataLimite == true) {
			criteria.add(Restrictions.le("batimento.data", dataBatimento));
		} else {
			criteria.add(Restrictions.eq("batimento.data", dataBatimento));
		}
		return criteria.list();
	}
	
	/**
	 * @param dataBatimento
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosParaPagamentoInstituicao(LocalDate dataBatimento) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("batimento", "batimento");
		criteria.createAlias("instituicaoDestino", "instituicaoDestino");
		criteria.createAlias("batimento.depositosBatimento", "depositosBatimento");
		criteria.createAlias("depositosBatimento.deposito", "deposito");
		criteria.add(Restrictions.eq("instituicaoDestino.tipoBatimento", TipoBatimento.BATIMENTO_REALIZADO_PELA_INSTITUICAO));
		criteria.add(Restrictions.ne("deposito.numeroDocumento", ConfiguracaoBase.DEPOSITO_CARTORIO));

		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.CONFIRMADO));
		disjunction.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO));
		criteria.add(disjunction);

		criteria.add(Restrictions.eq("batimento.data", dataBatimento));
		criteria.setProjection(Projections.distinct(Projections.property("batimento.remessa")));
		return criteria.list();
	}
	
	/**
	 * @param batimento
	 * @return
	 */
	public Batimento salvarBatimento(Batimento batimento) {
		Transaction transaction = getBeginTransation();

		try {
			Remessa remessa = remessaDAO.buscarPorPK(batimento.getRemessa());
			remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO);
			
			TipoBatimento tipoBatimento = remessa.getInstituicaoDestino().getTipoBatimento();
			if (tipoBatimento.equals(TipoBatimento.BATIMENTO_REALIZADO_PELA_CRA)
					|| tipoBatimento.equals(TipoBatimento.LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO)) {
				remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.CONFIRMADO);
			}
			batimento.setRemessa(update(remessa));
			batimento = save(batimento);
			if (batimento.getDepositosBatimento() != null) {
				for (BatimentoDeposito batimentoDeposito : batimento.getDepositosBatimento()) {
					Deposito deposito = batimentoDeposito.getDeposito();
					deposito.setSituacaoDeposito(SituacaoDeposito.IDENTIFICADO);
					update(deposito);

					batimentoDeposito.setDeposito(update(deposito));
					batimentoDeposito.setBatimento(batimento);
					save(batimentoDeposito);
				}
			}
			transaction.commit();
		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
			throw new InfraException("Não foi possível salvar o batimento dos arquivos de retorno! Favor entrar em contato com a CRA...");
		}
		return batimento;
	}

	/**
	 * @param batimento
	 * @return
	 */
	public Batimento removerBatimento(Batimento batimento) {
		try {
			Query query = createSQLQuery("DELETE FROM tb_batimento_deposito WHERE batimento_id=" + batimento.getId() + ";"
					+ "DELETE FROM audit_tb_batimento_deposito WHERE batimento_id=" + batimento.getId() + ";"
					+ "DELETE FROM tb_batimento AS bat WHERE bat.id_batimento=" + batimento.getId() + ";"
					+ "DELETE FROM audit_tb_batimento AS bat WHERE bat.id_batimento=" + batimento.getId() + ";");
			query.executeUpdate();

		} catch (InfraException ex) {
			logger.error(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível remover o batimento do arquivo de retorno! Favor entrar em contato com a CRA...");
		}
		return batimento;
	}

	/**
	 * @param retorno
	 * @return
	 */
	public Remessa retornarArquivoRetornoParaBatimento(Remessa retorno) {
		Transaction transaction = getBeginTransation();

		try {
			Remessa retornoAlterado = buscarPorPK(retorno, Remessa.class);
			retornoAlterado.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.NAO_CONFIRMADO);
			update(retornoAlterado);
			transaction.commit();

		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível retornar o arquivo para o batimento! Favor entrar em contato com a CRA...");
		}
		return retorno;
	}

	/**
	 * @param deposito
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> buscarRetornoCorrespondenteAoDeposito() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT idremessa_remessa, totalValorlPagos ");
		sql.append("FROM view_batimento_retorno ");
		sql.append("WHERE situacaobatimento_remessa='NAO_CONFIRMADO' ");
		Query query = getSession().createSQLQuery(sql.toString());
		return query.list();
	}

	/**
	 * @param deposito
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Batimento> buscarBatimentosDoDeposito(Deposito deposito) {
		Criteria criteria = getCriteria(Batimento.class);
		criteria.createAlias("depositosBatimento", "depositosBatimento");
		criteria.add(Restrictions.eq("depositosBatimento.deposito", deposito));
		return criteria.list();
	}

	/**
	 * @param retorno
	 * @return
	 */
	public Batimento buscarBatimentoDoRetorno(Remessa retorno) {
		Criteria criteria = getCriteria(Batimento.class);
		criteria.createAlias("depositosBatimento", "depositosBatimento");
		criteria.add(Restrictions.eq("remessa", retorno));
		return Batimento.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<Deposito> buscarDepositosPorBatimento(Batimento batimento) {
		Criteria criteria = getCriteria(Deposito.class);
		criteria.createAlias("batimentosDeposito", "batimentosDeposito");
		criteria.add(Restrictions.eq("batimentosDeposito.batimento", batimento));
		return criteria.list();
	}

	/**
	 * consulta a view de BatimentoRetorno quanto aos arquivos de retorno 
	 * que nãot tiveram o pagamento idenficado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ViewBatimentoRetorno> buscarRetornoBatimentoNaoConfimados() {
		Query query = getSession().getNamedQuery("findAllRetornoNaoConfirmados");
		return query.list();
	}
	
	/**
	 * Consulta a view de RetornoBatimento quanto aos arquivos que estão
	 *  aguardando liberação dos bancos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ViewBatimentoRetorno> buscarRetornoBatimentoAguardandoLiberacao() {
		Query query = getSession().getNamedQuery("findAllRetornoAguardandoLiberacao");
		return query.list();
	}
}