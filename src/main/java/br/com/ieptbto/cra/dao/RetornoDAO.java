package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class RetornoDAO extends AbstractBaseDAO {
	
	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosParaBatimento(){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.NAO_CONFIRMADO));
		criteria.add(Restrictions.eq("situacao", false));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosAguardandoLiberacao(){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO));
		criteria.add(Restrictions.eq("situacao", false));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosConfirmados(){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.CONFIRMADO));
		criteria.add(Restrictions.eq("situacao", false));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosConfirmadosPorInstituicao(Instituicao instituicaoDestino){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("situacaoBatimentoRetorno", SituacaoBatimentoRetorno.CONFIRMADO));
		criteria.add(Restrictions.eq("situacao", false));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicaoDestino));
		return criteria.list();
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Arquivo retorno){
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", retorno));
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}
	
	public BigDecimal buscarValorDemaisDespesas(Remessa retorno){
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("valorDemaisDespesas"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}
	
	public BigDecimal buscarValorDemaisDespesas(Arquivo retorno){
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", retorno));
		criteria.setProjection(Projections.sum("valorDemaisDespesas"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}
	
	public BigDecimal buscarValorDeCustasCartorio(Remessa retorno){
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
	
	public BigDecimal buscarValorDeCustasCartorio(Arquivo retorno){
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
	

	public Batimento salvarBatimento(Batimento batimento){
		Transaction transaction = getBeginTransation();
		
		try {
			Remessa remessa = batimento.getRemessa();
			remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO);	
			if (remessa.getInstituicaoDestino().getTipoBatimento().equals(TipoBatimento.BATIMENTO_REALIZADO_PELA_CRA)) {
				remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.CONFIRMADO);				
			}
			
			batimento.setRemessa(update(remessa));
			batimento = save(batimento);
			if (batimento.getDepositosBatimento() != null) {
				for (BatimentoDeposito batimentoDeposito : batimento.getDepositosBatimento()){
					Deposito deposito = batimentoDeposito.getDeposito();
					deposito.setSituacaoDeposito(SituacaoDeposito.IDENTIFICADO);
					
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
			throw new InfraException("Não foi possível inserir os depósitos na base de dados.");
		}
		return batimento;
	}
	
	public Remessa confirmarBatimento(Remessa retorno){
		Transaction transaction = getBeginTransation();
		
		try {
			retorno.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO);				
			if (retorno.getInstituicaoDestino().getTipoBatimento().equals(TipoBatimento.BATIMENTO_REALIZADO_PELA_CRA)) {
				retorno.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.CONFIRMADO);				
			}
			retorno = update(retorno);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível confirmar estas remessas.");
		}
		return retorno;
	}
	
	public void removerBatimento(Remessa retorno, Batimento batimento){
		
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE tb_remessa ");
			if (retorno.getSituacaoBatimentoRetorno().equals(SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO)) {
				sql.append("SET situacao_batimento_retorno='" + SituacaoBatimentoRetorno.NAO_CONFIRMADO.toString() +"' ");

			} else if (retorno.getInstituicaoOrigem().getTipoBatimento().equals(TipoBatimento.BATIMENTO_REALIZADO_PELA_INSTITUICAO) 
					&& retorno.getSituacaoBatimentoRetorno().equals(SituacaoBatimentoRetorno.CONFIRMADO)) {
				sql.append("SET situacao_batimento_retorno='" + SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO.toString() +"' ");

			} else if ((retorno.getInstituicaoOrigem().getTipoBatimento().equals(TipoBatimento.BATIMENTO_REALIZADO_PELA_CRA) 
					&& retorno.getSituacaoBatimentoRetorno().equals(SituacaoBatimentoRetorno.CONFIRMADO))){
				sql.append("SET situacao_batimento_retorno='" + SituacaoBatimentoRetorno.NAO_CONFIRMADO.toString() +"' ");
			}
			sql.append("WHERE id_remessa=" + retorno.getId() +";");
			Query query =  createSQLQuery(sql.toString());
			query.executeUpdate();
			
			sql = new StringBuffer();
			sql.append("DELETE FROM tb_batimento_deposito ");
			sql.append("WHERE batimento_id=" + batimento.getId() +"; ");
			query = createSQLQuery(sql.toString());
			query.executeUpdate();
			
			sql = new StringBuffer();
			sql.append("DELETE FROM tb_batimento ");
			sql.append("WHERE id_batimento=" + batimento.getId() +"; ");
			query = createSQLQuery(sql.toString());
			query.executeUpdate();

		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir os depósitos na base de dados.");
		}
	}
	
	public void gerarRetornos(Usuario usuarioAcao, List<Arquivo> arquivosDeRetorno){
		Transaction transaction = getBeginTransation();
		
		try {
			for (Arquivo retorno : arquivosDeRetorno){
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

	public void liberarRetornoBatimento(List<Remessa> retornoLiberados) {
		Transaction transaction = getBeginTransation();
		
		try {
			for (Remessa retorno : retornoLiberados) {
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
	}
	
}
