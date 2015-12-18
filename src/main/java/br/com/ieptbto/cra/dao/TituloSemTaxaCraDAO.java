package br.com.ieptbto.cra.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloSemTaxaCRA;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso
 *
 */
@Repository
public class TituloSemTaxaCraDAO extends AbstractBaseDAO {


	@SuppressWarnings("rawtypes")
	public TituloSemTaxaCRA verificarTituloEnviadoSemTaxa(Titulo titulo) {
		String protocolo = titulo.getNumeroProtocoloCartorio();
		if (titulo.getNumeroProtocoloCartorio() != null && !titulo.getNumeroProtocoloCartorio().equals("0")) {
			Integer numeroProtocolo = Integer.parseInt(titulo.getNumeroProtocoloCartorio().trim());
			protocolo = numeroProtocolo.toString();
		}
		Criteria criteria = getCriteria(TituloSemTaxaCRA.class);
		criteria.add(Restrictions.eq("protocolo", protocolo));
		criteria.add(Restrictions.eq("codigoIBGE", titulo.getRemessa().getCabecalho().getCodigoMunicipio().trim()));
		return TituloSemTaxaCRA.class.cast(criteria.uniqueResult());
	}
	
	public TituloSemTaxaCRA updateSituacao(TituloSemTaxaCRA tituloSemTaxa) {
		Session session = getSession();
		session.clear();
		session.flush();
		Transaction transaction = session.beginTransaction();
		try {
			update(tituloSemTaxa);
			transaction.commit();
			logger.info("O título [Protocolo :" + tituloSemTaxa.getProtocolo() + " Cog.Municipio :" + tituloSemTaxa.getCodigoIBGE() + "] foi corrido as taxas !");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível alterar esses dados na base.");
		}
		return tituloSemTaxa;
	}
}
