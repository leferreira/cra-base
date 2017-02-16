package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class InstituicaoDAO extends AbstractBaseDAO {

	@Autowired
	private TipoInstituicaoDAO tipoInstituicaoDAO;
	@Autowired
	private MunicipioDAO municipioDAO;

	public Instituicao salvar(Instituicao instituicao) {
		Transaction transaction = getBeginTransation();
		try {
			instituicao = save(instituicao);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esses dados na base.");
		}
		return instituicao;
	}

	public Instituicao alterar(Instituicao instituicao) {
		Transaction transaction = getBeginTransation();

		try {
			update(instituicao);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível alterar esses dados na base.");
		}
		return instituicao;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Instituicao buscarCartorioPorMunicipio(String nomeMunicipio) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.createAlias("municipio", "municipio");
		criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.CARTORIO));

		Criterion restrict1 = Restrictions.ilike("municipio.nomeMunicipio", nomeMunicipio, MatchMode.EXACT);
		Criterion restrict2 = Restrictions.ilike("municipio.nomeMunicipioSemAcento",
				RemoverAcentosUtil.removeAcentos(nomeMunicipio.trim().toUpperCase()), MatchMode.EXACT);

		criteria.add(Restrictions.or(restrict1, restrict2));
		if (criteria.uniqueResult() != null) {
			return Instituicao.class.cast(criteria.uniqueResult());
		}
		return null;

	}

	public boolean isInstituicaoAtiva(Instituicao instituicao) {
		if (instituicao.isSituacao()) {
			return true;
		} else {
			return false;
		}
	}

	public void inserirInstituicaoInicial(String nomeMunicipio) {
		Municipio municipio = municipioDAO.buscarMunicipioPorNome(nomeMunicipio);
		inserirInstituicaoInicial(municipio);
	}

	public void inserirInstituicaoInicial(Municipio municipio) {
		Transaction transaction = getBeginTransation();
		try {
			Instituicao instituicao = new Instituicao();
			instituicao.setNomeFantasia("CRA");
			instituicao.setSituacao(true);
			instituicao.setCnpj("123");
			instituicao.setMunicipio(municipio);
			instituicao.setRazaoSocial("CRA");
			instituicao.setTipoInstituicao(tipoInstituicaoDAO.buscarTipoInstituicao(TipoInstituicaoSistema.CRA));
			save(instituicao);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
	}

	public Instituicao buscarInstituicaoPorNomeFantasia(String nome) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("nomeFantasia", nome));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<Instituicao> buscarListaInstituicao() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.createAlias("municipio", "municipio");
		criteria.add(Restrictions.ne("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.CARTORIO));
		criteria.addOrder(Order.asc("nomeFantasia"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Instituicao> buscarListaInstituicaoAtivas() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.add(Restrictions.ne("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.CARTORIO));
		criteria.add(Restrictions.eq("situacao", true));
		criteria.addOrder(Order.asc("nomeFantasia"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Instituicao> listarTodas() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.addOrder(Order.asc("nomeFantasia"));
		return criteria.list();
	}

	/**
	 * Buscar todos os cartórios, ativos ou não e menos as instituicões
	 * 
	 * @return List<Instituicao>
	 */
	@SuppressWarnings({ "unchecked" })
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<Instituicao> getCartorios() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.createAlias("municipio", "municipio");
		criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.CARTORIO));
		criteria.addOrder(Order.asc("municipio.nomeMunicipio"));
		return criteria.list();
	}

	/**
	 * Buscar todos instituicões financieiras, ativos ou não
	 * 
	 * @return List<Instituicao>
	 */
	@SuppressWarnings("unchecked")
	public List<Instituicao> getInstituicoesFinanceiras() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.addOrder(Order.asc("nomeFantasia"));
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA));
		criteria.addOrder(Order.asc("nomeFantasia"));
		return criteria.list();
	}

	public Instituicao buscarInstituicaoInicial(String nomeFantasia) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("nomeFantasia", nomeFantasia));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	public Instituicao getCartorioPeloCodigoMunicipio(String codigoMunicipio) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("municipio", "municipio");
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.add(Restrictions.eq("municipio.codigoIBGE", codigoMunicipio));
		criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.CARTORIO));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	public Instituicao getInstituicaoPorCodigo(String codigoCompensacao) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("codigoCompensacao", codigoCompensacao));

		return Instituicao.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<Instituicao> getConvenios() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.CONVENIO));
		criteria.addOrder(Order.asc("nomeFantasia"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Instituicao> getInstituicoesFinanceirasEConvenios() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.add(Restrictions.ne("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.CARTORIO));
		criteria.add(Restrictions.ne("tipoInstituicao.tipoInstituicao", TipoInstituicaoSistema.CRA));
		criteria.addOrder(Order.asc("nomeFantasia"));
		return criteria.list();
	}
}