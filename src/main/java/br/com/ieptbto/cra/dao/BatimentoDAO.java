package br.com.ieptbto.cra.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.TipoDeposito;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class BatimentoDAO extends AbstractBaseDAO {

	public Deposito salvarDeposito(Deposito deposito) {
		Transaction transaction = getBeginTransation();

		try {
			deposito = save(deposito);
			if (deposito.getBatimentosDeposito() != null) {
				for (BatimentoDeposito batimentoDeposito : deposito.getBatimentosDeposito()) {
					Batimento batimento = batimentoDeposito.getBatimento();
					batimento.getRemessa().setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO);
					if (batimento.getRemessa().getInstituicaoDestino().getTipoBatimento().equals(TipoBatimento.BATIMENTO_REALIZADO_PELA_CRA)
							|| batimento.getRemessa().getInstituicaoDestino().getTipoBatimento().equals(TipoBatimento.LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO)) {
						batimento.getRemessa().setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.CONFIRMADO);
					}

					batimentoDeposito.setDeposito(deposito);
					batimentoDeposito.setBatimento(save(batimento));
					save(batimentoDeposito);
				}
			}
			transaction.commit();
		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir os depósitos na base de dados.");
		}
		return deposito;
	}

	public Deposito atualizarDeposito(Deposito deposito) {
		Transaction transaction = getSession().beginTransaction();

		try {
			merge(deposito);
			transaction.commit();

		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
			throw new InfraException("Não foi possível atualizar os depósitos na base de dados.");
		}
		return deposito;
	}

	@SuppressWarnings("rawtypes")
	public Remessa buscarRetornoCorrespondenteAoDeposito(Deposito deposito) {
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

		Query query = getSession().createSQLQuery(sql.toString());
		List result = query.list();
		if (result.size() > 1) {
			return null;
		}

		Iterator iterator = result.iterator();
		while (iterator.hasNext()) {
			Object[] posicao = (Object[]) iterator.next();
			Integer id = Integer.class.cast(posicao[0]);
			Criteria criteria = getCriteria(Remessa.class);
			criteria.add(Restrictions.eq("id", id));
			return Remessa.class.cast(criteria.uniqueResult());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Deposito> buscarDepositosExtrato() {
		Criteria criteria = getCriteria(Deposito.class);
		criteria.addOrder(Order.asc("data"));
		criteria.add(Restrictions.eq("situacaoDeposito", SituacaoDeposito.NAO_IDENTIFICADO));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Batimento> buscarBatimentosDoDeposito(Deposito deposito) {
		Criteria criteria = getCriteria(Batimento.class);
		criteria.createAlias("depositosBatimento", "depositosBatimento");
		criteria.add(Restrictions.eq("depositosBatimento.deposito", deposito));
		return criteria.list();
	}

	public Batimento buscarBatimentoDoRetorno(Remessa retorno) {
		Criteria criteria = getCriteria(Batimento.class);
		criteria.createAlias("depositosBatimento", "depositosBatimento");
		criteria.add(Restrictions.eq("remessa", retorno));
		return Batimento.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<Deposito> consultarDepositos(Deposito deposito, LocalDate dataInicio, LocalDate dataFim) {
		Criteria criteria = getCriteria(Deposito.class);

		if (dataInicio != null) {
			criteria.add(Restrictions.between("data", dataInicio, dataFim));
		}
		if (deposito.getNumeroDocumento() != null) {
			criteria.add(Restrictions.ilike("numeroDocumento", deposito.getNumeroDocumento(), MatchMode.ANYWHERE));
		}
		if (deposito.getValorCredito() != null) {
			criteria.add(Restrictions.eq("valorCredito", deposito.getValorCredito()));
		}
		if (deposito.getSituacaoDeposito() != null) {
			criteria.add(Restrictions.eq("situacaoDeposito", deposito.getSituacaoDeposito()));
		}
		if (!deposito.getTipoDeposito().equals(TipoDeposito.NAO_INFORMADO)) {
			criteria.add(Restrictions.eq("tipoDeposito", deposito.getTipoDeposito()));
		}

		criteria.addOrder(Order.asc("data"));
		return criteria.list();
	}

	public void atualizarInformacoesDeposito(Deposito deposito) {
		Transaction transaction = getBeginTransation();

		try {
			update(deposito);
			transaction.commit();

		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
			throw new InfraException("Não foi possível atualizar os depósitos na base de dados.");
		}
	}
}
