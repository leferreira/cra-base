package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class RetornoDAO extends AbstractBaseDAO {

	public static final String CONSTANTE_TIPO_DEPOSITO_CARTORIO = "CARTORIO";

	public Retorno carregarTituloRetornoPorId(Retorno retorno) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("id", retorno.getId()));
		return Retorno.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosParaBatimento() {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.NAO_CONFIRMADO));
		criteria.add(Restrictions.eq("situacao", false));
		return criteria.list();
	}

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

	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosParaPagamentoInstituicao(LocalDate dataBatimento) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("batimento", "batimento");
		criteria.createAlias("instituicaoDestino", "instituicaoDestino");
		criteria.createAlias("batimento.depositosBatimento", "depositosBatimento");
		criteria.createAlias("depositosBatimento.deposito", "deposito");

		criteria.add(Restrictions.eq("instituicaoDestino.tipoBatimento", TipoBatimento.BATIMENTO_REALIZADO_PELA_INSTITUICAO));
		criteria.add(Restrictions.ne("deposito.numeroDocumento", CONSTANTE_TIPO_DEPOSITO_CARTORIO));

		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.CONFIRMADO));
		disjunction.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO));
		criteria.add(disjunction);

		criteria.add(Restrictions.eq("batimento.data", dataBatimento));
		criteria.setProjection(Projections.distinct(Projections.property("batimento.remessa")));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosConfirmados() {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.CONFIRMADO));
		criteria.add(Restrictions.eq("situacao", false));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosConfirmadosPorInstituicao(Instituicao instituicaoDestino) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.CONFIRMADO));
		criteria.add(Restrictions.eq("situacao", false));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicaoDestino));
		return criteria.list();
	}

	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}

	public BigDecimal buscarSomaValorTitulosPagosRemessas(Instituicao instituicao, LocalDate dataBatimento, boolean dataComoDataLimite) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.batimento", "batimento");

		if (dataComoDataLimite == true) {
			criteria.add(Restrictions.le("batimento.data", dataBatimento));
		} else {
			criteria.add(Restrictions.eq("batimento.data", dataBatimento));
		}
		criteria.add(Restrictions.eq("remessa.instituicaoDestino", instituicao));
		criteria.add(Restrictions.eq("remessa.situacaoBatimentoRetorno", SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO));
		criteria.add(Restrictions.eq("remessa.situacao", false));
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}

	public BigDecimal buscarValorDeTitulosPagos(Arquivo retorno) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", retorno));
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}

	public BigDecimal buscarValorDemaisDespesas(Remessa retorno) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("valorDemaisDespesas"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}

	public BigDecimal buscarValorDemaisDespesas(Arquivo retorno) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", retorno));
		criteria.setProjection(Projections.sum("valorDemaisDespesas"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}

	public BigDecimal buscarValorDeCustasCartorio(Remessa retorno) {
		Criteria criteria = getCriteria(Retorno.class);

		Disjunction disj = Restrictions.disjunction();
		disj.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PROTESTADO.getConstante()));
		disj.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.RETIRADO.getConstante()));
		disj.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_COM_CUSTAS.getConstante()));
		criteria.add(disj);
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("valorCustaCartorio"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}

	public BigDecimal buscarValorDeCustasCartorio(Arquivo retorno) {
		Criteria criteria = getCriteria(Retorno.class);

		Disjunction disj = Restrictions.disjunction();
		disj.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PROTESTADO.getConstante()));
		disj.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.RETIRADO.getConstante()));
		disj.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_COM_CUSTAS.getConstante()));
		criteria.add(disj);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", retorno));
		criteria.setProjection(Projections.sum("valorCustaCartorio"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}

	public void gerarRetornos(Usuario usuarioAcao, List<Arquivo> arquivosDeRetorno) {
		Transaction transaction = getBeginTransation();

		try {
			for (Arquivo retorno : arquivosDeRetorno) {
				StatusArquivo status = new StatusArquivo();
				status.setData(new LocalDateTime());
				status.setSituacaoArquivo(SituacaoArquivo.AGUARDANDO);

				retorno.setUsuarioEnvio(usuarioAcao);
				retorno.setStatusArquivo(save(status));
				save(retorno);

				for (Remessa r : retorno.getRemessas()) {
					r.setSituacao(true);
					r.setArquivoGeradoProBanco(retorno);
					update(r);
				}
				logger.info("O arquivo " + retorno.getNomeArquivo() + " foi inserido na base com sucesso!");
			}
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível realizar esse batimento.");
		}
	}

	@SuppressWarnings("unchecked")
	public List<BatimentoDeposito> buscarDepositosBatimento(Remessa retorno) {
		Criteria criteria = getCriteria(BatimentoDeposito.class);
		criteria.createAlias("deposito", "deposito");
		criteria.createAlias("batimento", "batimento");
		criteria.add(Restrictions.eq("batimento.remessa", retorno));
		return criteria.list();
	}

	public List<Remessa> liberarRetornoBatimento(List<Remessa> arquivosLIberados) {
		Transaction transaction = getBeginTransation();

		try {
			for (Remessa retorno : arquivosLIberados) {
				retorno = buscarPorPK(retorno, Remessa.class);
				retorno.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.CONFIRMADO);
				update(retorno);
			}

			transaction.commit();
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir os depósitos na base de dados.");
		}
		return arquivosLIberados;
	}

	@SuppressWarnings("unchecked")
	public Boolean verificarArquivoRetornoGeradoCra(Instituicao cra) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("dataEnvio", new LocalDate()));
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.RETORNO));
		criteria.add(Restrictions.eq("instituicaoEnvio", cra));

		List<Arquivo> arquivosRetornoCRA = criteria.list();
		if (arquivosRetornoCRA.isEmpty()) {
			return false;
		}
		return true;
	}
}
