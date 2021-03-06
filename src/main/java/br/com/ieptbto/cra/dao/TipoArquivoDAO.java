package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@Repository
public class TipoArquivoDAO extends AbstractBaseDAO {

	public TipoArquivo salvar(TipoArquivo tipoArquivo) {
		TipoArquivo novo = new TipoArquivo();
		Transaction transaction = getBeginTransation();
		try {
			novo = save(tipoArquivo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novo;
	}

	public TipoArquivo alterar(TipoArquivo tipoArquivo) {
		Transaction transaction = getBeginTransation();
		try {
			update(tipoArquivo);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return tipoArquivo;
	}

	@SuppressWarnings("unchecked")
	public List<TipoArquivo> buscarTiposArquivo() {
		Criteria criteria = getCriteria(TipoArquivo.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	@Transactional(readOnly = true)
	public TipoArquivo buscarPorTipoArquivo(TipoArquivoFebraban tipoArquivo) {
		Criteria criteria = getCriteria(TipoArquivo.class);
		criteria.add(Restrictions.eq("tipoArquivo", tipoArquivo));
		criteria.addOrder(Order.asc("id"));
		return TipoArquivo.class.cast(criteria.uniqueResult());
	}

	public TipoArquivo buscarTipoArquivo(Arquivo arquivo) {
		Criteria criteria = getCriteria(TipoArquivo.class);
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(arquivo.getNomeArquivo().toUpperCase());
		criteria.add(Restrictions.eq("tipoArquivo", tipoArquivo));
		return TipoArquivo.class.cast(criteria.uniqueResult());
	}

	public TipoArquivo buscarTipoArquivo(String nomeArquivo) {
		Criteria criteria = getCriteria(TipoArquivo.class);
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(nomeArquivo.toUpperCase());
		criteria.add(Restrictions.eq("tipoArquivo", tipoArquivo));
		return TipoArquivo.class.cast(criteria.uniqueResult());
	}

	public void inserirTipoArquivo(String tipo) {
		TipoArquivo tipoArquivo = new TipoArquivo();
		tipoArquivo.setTipoArquivo(TipoArquivoFebraban.get(tipo));

		salvar(tipoArquivo);
	}

	public Long buscarSequencialProximoArquivo(Instituicao instituicaoEnvio, TipoArquivoFebraban tipoArquivo) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipoArquivo));
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicaoEnvio));
		criteria.add(Restrictions.eq("dataEnvio", new LocalDate()));
		criteria.setProjection(Projections.count("id"));
		return Long.class.cast(criteria.uniqueResult());
	}
}
