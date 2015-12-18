package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.BancoTipoRegraBasicaInstrumento;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class ArquivoDeParaDAO extends AbstractBaseDAO{

	public void salvarArquivoCAF(List<AgenciaCAF> listaAgencias) {
		Transaction transaction = getBeginTransation();
		
		try {
		
			for (AgenciaCAF agencia: listaAgencias){
				save(agencia);
			}
			transaction.commit();
			
			logger.info("O arquivo CAF enviado foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo CAF na base de dados.");
		}
	}

	public void salvarArquivoBradesco(List<AgenciaBradesco> listaAgencias) {
		Transaction transaction = getBeginTransation();
		
		try {
			
			for (AgenciaBradesco agencia: listaAgencias){
				save(agencia);
			}
			transaction.commit();
			
			logger.info("O arquivo Bradesco enviado foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo Bradesco na base de dados.");
		}
	}
	
	public void salvarArquivoBancoDoBrasil(List<AgenciaBancoDoBrasil> listaAgencias) {
		Transaction transaction = getBeginTransation();
		
		try {
			
			for (AgenciaBancoDoBrasil agencia: listaAgencias){
				save(agencia);
			}
			transaction.commit();
			
			logger.info("O arquivo banco do brasil enviado foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo Bradesco na base de dados.");
		}
	}

	public AgenciaBancoDoBrasil buscarAgenciaArquivoBancoDoBrasil() {
		// TODO Auto-generated method stub
		return null;
	}

	public AgenciaBradesco buscarAgenciaArquivoDeParaBradesco(TituloRemessa tituloRemessa) {
		Criteria criteria = getCriteria(AgenciaBradesco.class);
		criteria.add(Restrictions.like("cnpj", tituloRemessa.getDocumentoSacador(), MatchMode.EXACT));
		criteria.add(Restrictions.like("codigoAgenciaCedente", tituloRemessa.getAgenciaCodigoCedente(), MatchMode.EXACT));
		criteria.setMaxResults(1);
		return AgenciaBradesco.class.cast(criteria.uniqueResult());
	}

	public AgenciaCAF buscarAgenciaArquivoCAF(String agencia, BancoTipoRegraBasicaInstrumento banco) {
		Criteria criteria = getCriteria(AgenciaCAF.class);
		criteria.add(Restrictions.eq("banco", banco.getCodigoPortador()));
		criteria.add(Restrictions.like("codigoAgencia", agencia, MatchMode.EXACT));
		criteria.setMaxResults(1);
		return AgenciaCAF.class.cast(criteria.uniqueResult());
	}
}
