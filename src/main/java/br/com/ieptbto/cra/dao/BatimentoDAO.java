package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.ViewBatimento;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

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
	@SuppressWarnings("rawtypes")
	public List<Remessa> buscarRetornoCorrespondenteAoDeposito(Deposito deposito) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ret.remessa_id, sum(ret.valor_saldo_titulo) ");
		sql.append("FROM tb_titulo AS tit ");
		sql.append("INNER JOIN tb_retorno AS ret ON tit.id_titulo=ret.titulo_id ");
		sql.append("WHERE (ret.tipo_ocorrencia='1') AND ret.remessa_id IN (SELECT rem.id_remessa ");
		sql.append("	FROM tb_remessa AS rem ");
		sql.append("	INNER JOIN tb_arquivo AS arq ON rem.arquivo_id=arq.id_arquivo ");
		sql.append("	INNER JOIN tb_tipo_arquivo AS tipo ON arq.tipo_arquivo_id = tipo.id_tipo_arquivo ");
		sql.append("	WHERE rem.situacao_batimento_retorno = 'NAO_CONFIRMADO' AND tipo.id_tipo_arquivo = 3) ");
		sql.append("GROUP BY ret.remessa_id ");
		sql.append("HAVING SUM(ret.valor_saldo_titulo)=" + deposito.getValorCredito().toString());

		List<Remessa> arquivosRetorno = new ArrayList<>();
		Query query = getSession().createSQLQuery(sql.toString());
		Iterator iterator = query.list().iterator();
		while (iterator.hasNext()) {
			Object[] posicao = (Object[]) iterator.next();
			Integer id = Integer.class.cast(posicao[0]);
			Criteria criteria = getCriteria(Remessa.class);
			criteria.add(Restrictions.eq("id", id));
			arquivosRetorno.add(Remessa.class.cast(criteria.uniqueResult()));
		}
		return arquivosRetorno;
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

	@SuppressWarnings("unchecked")
	public List<ViewBatimento> buscarArquivosViewBatimento() {
		Query query = getSession().getNamedQuery("findAllArquivosBatimento");
		return query.list();
	}
}