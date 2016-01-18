package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.RemessaCnp;
import br.com.ieptbto.cra.entidade.TituloCnp;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class CentralNancionalProtestoDAO extends AbstractBaseDAO {

	@Transactional
	public ArquivoCnp salvarArquivoCartorioCentralNacionalProtesto(Usuario user, ArquivoCnp arquivoCnp) {
		Transaction transaction = getBeginTransation();
		try {
			arquivoCnp = save(arquivoCnp);
			
			for (RemessaCnp remessaCnp : arquivoCnp.getRemessaCnp()) {
				remessaCnp.setArquivo(arquivoCnp);
				remessaCnp.setCabecalho(save(remessaCnp.getCabecalho()));
				remessaCnp.setRodape(save(remessaCnp.getRodape()));
				remessaCnp = save(remessaCnp);
				
				for (TituloCnp tituloCnp : remessaCnp.getTitulos()) {
					tituloCnp.setRemessa(remessaCnp);
					save(tituloCnp);
				}
			}
			transaction.commit();
			logger.info("O arquivo CNP do cartório "+ user.getInstituicao().getNomeFantasia() + " foi salvo na base de dados. ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esses dados na base.");
		}
		return arquivoCnp;
	}

	@Transactional
	public ArquivoCnp getArquivoCnpHojeInstituicao(Instituicao instituicao) {
		Criteria criteria = getCriteria(ArquivoCnp.class);
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicao));
		criteria.add(Restrictions.eq("dataEnvio", new LocalDate()));
		return ArquivoCnp.class.cast(criteria.uniqueResult());
	}

//	@SuppressWarnings("unchecked")
	public List<RemessaCnp> buscarRemessasCnpPendentes() {
//		Criteria criteria =  getCriteria(RemessaCnp.class);
//		criteria.add(Restrictions.eq("", ""));
//		return criteria.list();
		return new ArrayList<>();
	}

	public void salvarArquivoCnpNacional(ArquivoCnp arquivoCnp) {
		// TODO Auto-generated method stub
		
	}

}