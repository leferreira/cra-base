package br.com.ieptbto.cra.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

/**
 * 
 * @author Lefer
 *
 */
@Repository
public class CabecalhoDAO extends AbstractBaseDAO {

	/**
	 * Verifica se o número seguencial da remessa é único para aquele banco e
	 * cidade.
	 * 
	 * @param cabecalhoRemessa
	 * @return
	 */
	public boolean isSequencialUnicoPorBanco(CabecalhoRemessa cabecalhoRemessa) {
		Criteria criteria = getCriteria(CabecalhoRemessa.class);

		criteria.add(Restrictions.eq("codigoMunicipio", cabecalhoRemessa.getCodigoMunicipio()));
		criteria.add(Restrictions.eq("numeroCodigoPortador", cabecalhoRemessa.getNumeroCodigoPortador()));
		criteria.add(Restrictions.eq("numeroCodigoPortador", cabecalhoRemessa.getNumeroCodigoPortador()));
		criteria.add(Restrictions.eq("numeroSequencialRemessa", cabecalhoRemessa.getNumeroSequencialRemessa()));
		criteria.add(Restrictions.eq("identificacaoTransacaoTipo", cabecalhoRemessa.getIdentificacaoTransacaoTipo()));

		if (criteria.uniqueResult() == null || criteria.list().isEmpty()) {
			return true;
		}
		return false;
	}

	public CabecalhoRemessa buscarUltimoCabecalhoRetornoPorMunicipio(CabecalhoRemessa cabecalhoRemessa) {
		Criteria criteria = getCriteria(CabecalhoRemessa.class);
		criteria.add(Restrictions.eq("numeroCodigoPortador", cabecalhoRemessa.getNumeroCodigoPortador()));
		criteria.add(Restrictions.eq("codigoMunicipio", cabecalhoRemessa.getCodigoMunicipio()));
		criteria.add(Restrictions.eq("identificacaoTransacaoTipo", TipoArquivoEnum.RETORNO.getIdentificacaoTransacaoCabecalho()));
		criteria.setMaxResults(1);
		criteria.addOrder(Order.desc("id"));
		return CabecalhoRemessa.class.cast(criteria.uniqueResult());
	}

	public CabecalhoRemessa buscarUltimoCabecalhoRemessaPorMunicipio(String codigoPortador, String codigoMunicipio) {
		Criteria criteria = getCriteria(CabecalhoRemessa.class);
		criteria.add(Restrictions.eq("numeroCodigoPortador", codigoPortador));
		criteria.add(Restrictions.eq("codigoMunicipio", codigoMunicipio));
		criteria.add(Restrictions.eq("identificacaoTransacaoTipo", TipoArquivoEnum.REMESSA.getIdentificacaoTransacaoCabecalho()));
		criteria.setMaxResults(1);
		criteria.addOrder(Order.desc("id"));
		return CabecalhoRemessa.class.cast(criteria.uniqueResult());
	}

	public CabecalhoRemessa buscarUltimoCabecalhoRemessa(CabecalhoRemessa cabecalhoRemessa) {
		Criteria criteria = getCriteria(CabecalhoRemessa.class);
		criteria.add(Restrictions.eq("numeroCodigoPortador", cabecalhoRemessa.getNumeroCodigoPortador()));
		criteria.add(Restrictions.eq("codigoMunicipio", cabecalhoRemessa.getCodigoMunicipio()));
		criteria.add(Restrictions.eq("identificacaoTransacaoTipo", TipoArquivoEnum.REMESSA.getIdentificacaoTransacaoCabecalho()));
		criteria.setMaxResults(1);
		criteria.addOrder(Order.desc("id"));
		return CabecalhoRemessa.class.cast(criteria.uniqueResult());
	}
}
