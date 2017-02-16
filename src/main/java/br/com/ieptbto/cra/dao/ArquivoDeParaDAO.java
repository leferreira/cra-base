package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.regra.RegraBasicaInstrumentoBanco;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class ArquivoDeParaDAO extends AbstractBaseDAO {

	/**
	 * Salvar Agências CAF
	 * 
	 * @param listaAgencias
	 */
	public void salvarArquivoCAF(List<AgenciaCAF> listaAgencias) {
		Transaction transaction = getBeginTransation();

		try {
			for (AgenciaCAF agencia : listaAgencias) {
				save(agencia);
			}
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo CAF na base de dados.");
		}
	}
	
	public void limparAgenciasCaf() {
		
		try {
			Query query = createSQLQuery("DELETE FROM tb_agencia_caf; DELETE FROM audit_tb_agencia_caf;");
			query.executeUpdate();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível limpar a base de agências CAF. Favor entrar em contato com a CRA!");
		}
	}


	/**
	 * Salvar agências Arquivo Bradesco
	 * 
	 * @param listaAgencias
	 */
	public void salvarArquivoBradesco(List<AgenciaBradesco> listaAgencias) {
		Transaction transaction = getBeginTransation();

		try {
			for (AgenciaBradesco agencia : listaAgencias) {
				save(agencia);
			}
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo Bradesco na base de dados.");
		}
	}

	public void limparAgenciasBancoDoBrasil() {
		
		try {
			Query query = createSQLQuery("DELETE FROM tb_agencia_banco_brasil; DELETE FROM audit_tb_agencia_banco_brasil;");
			query.executeUpdate();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível limpar a base de agências Banco do Brasil. Favor entrar em contato com a CRA!");
		}
	}
	
	/**
	 * Salvar arquivo de agências Banco do Brasil
	 * 
	 * @param listaAgencias
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void salvarArquivoBancoDoBrasil(List<AgenciaBancoDoBrasil> listaAgencias) {
		Transaction transaction = getBeginTransation();

		try {
			for (AgenciaBancoDoBrasil agencia : listaAgencias) {
				save(agencia);
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo Bradesco na base de dados.");
		}
	}

	public void limparAgenciasBradesco() {
		
		try {
			Query query = createSQLQuery("DELETE FROM tb_agencia_bradesco; DELETE FROM audit_tb_agencia_bradesco;");
			query.executeUpdate();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível limpar a base de agências Bradesco. Favor entrar em contato com a CRA!");
		}
	}

	/**
	 * Buscar agência do Banco do Brasil de acordo com o núemro do contrato
	 * @param numeroContrato
	 * @return
	 */
	public AgenciaBancoDoBrasil buscarAgenciaBancoDoBrasilPorContrato(String numeroContrato) {
		Criteria criteria = getCriteria(AgenciaBancoDoBrasil.class);
		criteria.add(Restrictions.ilike("numeroContrato", numeroContrato, MatchMode.EXACT));
		return AgenciaBancoDoBrasil.class.cast(criteria.uniqueResult());
	}

	/**
	 * Buscar agência do Banco Bradesco de acordo com os dados de título
	 * 
	 * @param tituloRemessa
	 * @return
	 */
	public AgenciaBradesco buscarAgenciaBradescoPorTitulo(TituloRemessa tituloRemessa) {
		Criteria criteria = getCriteria(AgenciaBradesco.class);
		criteria.add(Restrictions.like("cnpj", tituloRemessa.getDocumentoSacador(), MatchMode.EXACT));
		criteria.add(Restrictions.like("codigoAgenciaCedente", tituloRemessa.getAgenciaCodigoCedente(), MatchMode.EXACT));
		criteria.setMaxResults(1);
		return AgenciaBradesco.class.cast(criteria.uniqueResult());
	}

	/**
	 * Buscar agência CAF por código da agência e tipoRegra
	 * 
	 * @param agencia
	 * @param bancoTipo
	 * @return
	 */
	public AgenciaCAF buscarAgenciaCAFPorCodigoRegra(String agencia, RegraBasicaInstrumentoBanco banco) {
		Criteria criteria = getCriteria(AgenciaCAF.class);
		criteria.add(Restrictions.eq("banco", banco.getCodigoPortador()));
		criteria.add(Restrictions.like("codigoAgencia", agencia, MatchMode.EXACT));
		criteria.setMaxResults(1);
		return AgenciaCAF.class.cast(criteria.uniqueResult());
	}
}