package br.com.ieptbto.cra.dao;

import java.text.Normalizer;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

@Repository
public class MunicipioDAO extends AbstractBaseDAO {

	private static final int CARTORIO = 2;
	private static final String SIGLA_TOCANTINS = "TO";

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Municipio carregarMunicipio(Municipio municipio) {
		return buscarPorPK(municipio, Municipio.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Municipio> consultarMunicipios(String nomeMunicipio, String codigoIbge, String uf) {
		Criteria criteria = getCriteria(Municipio.class);
		if (StringUtils.isNotBlank(nomeMunicipio)) {
			criteria.add(Restrictions.ilike("nomeMunicipio", nomeMunicipio));
		}
		if (StringUtils.isNotBlank(codigoIbge)) {
			criteria.add(Restrictions.ilike("codigoIBGE", codigoIbge));
		}
		if (StringUtils.isNotBlank(uf)) {
			criteria.add(Restrictions.ilike("uf", uf));
		}
		criteria.addOrder(Order.asc("nomeMunicipio"));
		return criteria.list();
	}

	/**
	 * Salvar um novo municipio
	 * @param municipio
	 * @return
	 */
	public Municipio salvar(Municipio municipio) {
		Transaction transaction = getBeginTransation();
		try {
			municipio.setNomeMunicipioSemAcento(getNomeMunicipioSemAcento(municipio.getNomeMunicipio()));
			municipio = save(municipio);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
		}
		return municipio;
	}

	private String getNomeMunicipioSemAcento(String nomeMunicipio) {
		nomeMunicipio = Normalizer.normalize(nomeMunicipio, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		return nomeMunicipio.toUpperCase();
	}

	public Municipio alterar(Municipio municipio) {
		Transaction transaction = getBeginTransation();
		try {
			update(municipio);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return municipio;
	}

	public Municipio buscarMunicipioPorNome(String nomeMunicipio) {
		Criteria criteria = getCriteria(Municipio.class);
		Criterion restrict1 = Restrictions.ilike("nomeMunicipio", nomeMunicipio, MatchMode.EXACT);
		Criterion restrict2 = Restrictions.ilike("nomeMunicipioSemAcento", RemoverAcentosUtil.removeAcentos(nomeMunicipio), MatchMode.EXACT);
		criteria.add(Restrictions.or(restrict1, restrict2));
		criteria.setMaxResults(1);
		return Municipio.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<Municipio> listarTodos() {
		Criteria criteria = getCriteria(Municipio.class);
		criteria.addOrder(Order.asc("nomeMunicipio"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Municipio> listarTodosTocantins() {
		Criteria criteria = getCriteria(Municipio.class);
		criteria.addOrder(Order.asc("nomeMunicipio"));
		criteria.add(Restrictions.eq("uf", SIGLA_TOCANTINS));
		return criteria.list();
	}

	public Municipio buscaMunicipioPorCodigoIBGE(String codigoMunicipio) {
		Criteria criteria = getCriteria(Municipio.class);
		criteria.add(Restrictions.eq("codigoIBGE", codigoMunicipio));
		return Municipio.class.cast(criteria.uniqueResult());
	}

	public Municipio buscarMunicipioDoCartorio(Instituicao cartorio) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("municipio", "municipio");
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");

		criteria.add(Restrictions.eq("tipoInstituicao.id", CARTORIO));
		criteria.add(Restrictions.eq("nomeFantasia", cartorio.getNomeFantasia()));
		criteria.setMaxResults(1);
		Instituicao cartorioBuscado = Instituicao.class.cast(criteria.uniqueResult());
		return cartorioBuscado.getMunicipio();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Municipio> buscarMunicipiosAtivos() {
		Criteria criteria = getCriteria(Municipio.class);
		criteria.addOrder(Order.asc("nomeMunicipio"));
		criteria.add(Restrictions.eq("uf", SIGLA_TOCANTINS));
		criteria.add(Restrictions.eq("situacao", true));
		return criteria.list();
	}
}