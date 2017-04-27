package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.view.ViewBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class RetornoDAO extends AbstractBaseDAO {

	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosConfirmados() {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.CONFIRMADO));
		criteria.add(Restrictions.eq("situacao", false));
		return criteria.list();
	}
	
	/**
	 * conusulta a view de Batimento Retorno e traz os arquivos de retorno já confirmados 
	 * juntamente com os arquivos que não contém títulos pagos e já entram como confirmados 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ViewBatimentoRetorno> buscarRetornoConfirmados() {
		Query query = getSession().getNamedQuery("findAllRetornoConfirmados");
		return query.list();
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
		BigDecimal valor = BigDecimal.class.cast(criteria.uniqueResult());
		if (valor == null || valor.equals(BigDecimal.ZERO)) {
			return new BigDecimal(0);
		}
		return valor;
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
		BigDecimal valor = BigDecimal.class.cast(criteria.uniqueResult());
		if (valor == null || valor.equals(BigDecimal.ZERO)) {
			return new BigDecimal(0);
		}
		return valor;
	}

	public BigDecimal buscarValorDeTitulosPagos(Arquivo retorno) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", retorno));
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		BigDecimal valor = BigDecimal.class.cast(criteria.uniqueResult());
		if (valor == null || valor.equals(BigDecimal.ZERO)) {
			return new BigDecimal(0);
		}
		return valor;
	}

	public BigDecimal buscarValorDemaisDespesas(Remessa retorno) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("valorDemaisDespesas"));
		BigDecimal valor = BigDecimal.class.cast(criteria.uniqueResult());
		if (valor == null || valor.equals(BigDecimal.ZERO)) {
			return new BigDecimal(0);
		}
		return valor;
	}

	public BigDecimal buscarValorDemaisDespesas(Arquivo retorno) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", retorno));
		criteria.setProjection(Projections.sum("valorDemaisDespesas"));
		BigDecimal valor = BigDecimal.class.cast(criteria.uniqueResult());
		if (valor == null || valor.equals(BigDecimal.ZERO)) {
			return new BigDecimal(0);
		}
		return valor;
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
		BigDecimal valor = BigDecimal.class.cast(criteria.uniqueResult());
		if (valor == null || valor.equals(BigDecimal.ZERO)) {
			return new BigDecimal(0);
		}
		return valor;
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
		BigDecimal valor = BigDecimal.class.cast(criteria.uniqueResult());
		if (valor == null || valor.equals(BigDecimal.ZERO)) {
			return new BigDecimal(0);
		}
		return valor;
	}

	/**
	 * Salvar Arquivos de Retorno gerados
	 * @param usuario
	 * @param arquivosRetorno
	 * @return
	 */
	public List<Arquivo> gerarRetornos(Usuario usuario, List<Arquivo> arquivosRetorno) {
		Transaction transaction = getBeginTransation();

		try {
			for (Arquivo arquivo : arquivosRetorno) {
				StatusArquivo status = new StatusArquivo();
				status.setData(new LocalDateTime());
				status.setStatusDownload(StatusDownload.AGUARDANDO);

				arquivo.setUsuarioEnvio(usuario);
				arquivo.setStatusArquivo(save(status));
				save(arquivo);

				for (Remessa retorno : arquivo.getRemessas()) {
					retorno.setSituacao(true);
					retorno.setArquivoGeradoProBanco(arquivo);
					update(retorno);
				}
				logger.info("O arquivo " + arquivo.getNomeArquivo() + " foi inserido na base com sucesso!");
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível salvar os arquivos de retorno gerados. Favor entrar em contato com a CRA...");
		}
		return arquivosRetorno;
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

	/**
	 * Verifica se já foram gerados os arquivos de retorno pela CRA para os bancos
	 * @return
	 */
	public Boolean verificarArquivoRetornoGeradoCra(Instituicao cra) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("dataEnvio", new LocalDate()));
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoFebraban.RETORNO));
		criteria.add(Restrictions.eq("instituicaoEnvio", cra));
		criteria.setMaxResults(1);
		Arquivo arquivoRetornoCRA = Arquivo.class.cast(criteria.uniqueResult());
		if (arquivoRetornoCRA == null) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<Remessa> buscarArquivosRetornosVinculadosPorDeposito(Deposito deposito) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("batimento", "batimento");
		criteria.createAlias("batimento.depositosBatimento", "depositosBatimento");
		criteria.add(Restrictions.eq("depositosBatimento.deposito", deposito));
		return criteria.list();
	}
}
