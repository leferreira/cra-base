package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class TituloFiliadoDAO extends AbstractBaseDAO {

	public TituloFiliado salvar(TituloFiliado titulo) {
		TituloFiliado novoTitulo = new TituloFiliado();
		Transaction transaction = getBeginTransation();

		try {
			novoTitulo = save(titulo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}

		return novoTitulo;
	}

	public TituloFiliado alterar(TituloFiliado titulo) {
		TituloFiliado alterado = new TituloFiliado();
		Transaction transaction = getBeginTransation();

		try {
			alterado = update(titulo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}

		return alterado;
	}

	public void removerTituloFiliado(TituloFiliado titulo) {
		Transaction transaction = getBeginTransation();

		try {
			titulo.setSituacaoTituloConvenio(SituacaoTituloConvenio.REMOVIDO);
			update(titulo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> buscarTitulosParaEnvioAoConvenio(Filiado empresaFiliada) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.createAlias("filiado", "filiado");
		criteria.add(Restrictions.eq("filiado", empresaFiliada));
		criteria.add(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.AGUARDANDO));
		criteria.addOrder(Order.desc("nomeDevedor"));
		return criteria.list();
	}

	public void enviarTitulosPendentes(List<TituloFiliado> listaTitulosFiliado) {
		Transaction transaction = getBeginTransation();

		try {

			for (TituloFiliado titulo : listaTitulosFiliado) {
				titulo.setSituacaoTituloConvenio(SituacaoTituloConvenio.ENVIADO);
				update(titulo);
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> buscarTitulosConvenios() {
		Criteria criteria = getCriteria(TituloFiliado.class);
		// criteria.createAlias("filiado", "filiado");
		criteria.createAlias("pracaProtesto", "p");
		// criteria.createAlias("filiado.instituicaoConvenio",
		// "instituicaoConvenio");
		// criteria.setProjection(Projections.distinct(Projections.property("pracaProtesto")));
		// criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("p.id")));

		criteria.add(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.ENVIADO));
		criteria.addOrder(Order.asc("pracaProtesto"));
		return criteria.list();
	}

	public int getNumeroDeTitulosPendentes() {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.add(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.ENVIADO));
		return criteria.list().size();
	}

	public TituloRemessa buscarTituloDoConvenioNaCra(TituloFiliado tituloFiliado) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> consultarTitulosFiliado(Usuario user,
			TituloFiliado tituloBuscado) {
		Criteria criteria = getCriteria(UsuarioFiliado.class);
		criteria.createAlias("filiado", "filiado");
		criteria.add(Restrictions.eq("usuario", user));
		Filiado empresaFiliado  = UsuarioFiliado.class.cast(criteria.uniqueResult()).getFiliado();

		Criteria criteriaTitulos = getCriteria(TituloFiliado.class);
		criteriaTitulos.add(Restrictions.eq("filiado", empresaFiliado));

		if (tituloBuscado.getNumeroTitulo() != null)
			criteriaTitulos.add(Restrictions.ilike("numeroTitulo", tituloBuscado.getNumeroTitulo(), MatchMode.EXACT));

		if (tituloBuscado.getNomeDevedor() != null)
			criteriaTitulos.add(Restrictions.ilike("nomeDevedor", tituloBuscado.getNomeDevedor(), MatchMode.ANYWHERE));

		if (tituloBuscado.getDocumentoDevedor() != null)
			criteriaTitulos.add(Restrictions.ilike("documentoDevedor", tituloBuscado.getDocumentoDevedor(), MatchMode.ANYWHERE));

		if (tituloBuscado.getDataEmissao() != null)
			criteriaTitulos.add(Restrictions.between("dataEmissao", tituloBuscado.getDataEmissao(), tituloBuscado.getDataEmissao()));

		if (tituloBuscado.getPracaProtesto() != null)
			criteriaTitulos.add(Restrictions.eq("pracaProtesto", tituloBuscado.getPracaProtesto()));
		
		criteriaTitulos.addOrder(Order.asc("nomeDevedor"));
		return criteriaTitulos.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<TituloFiliado> consultarTitulosConvenio(Instituicao instituicao, TituloFiliado titulo) {
		Criteria criteriaTitulos = getCriteria(TituloFiliado.class);
		criteriaTitulos.createAlias("filiado", "filiado");
		criteriaTitulos.createAlias("filiado.instituicaoConvenio", "filiado.instituicaoConvenio");
		criteriaTitulos.add(Restrictions.eq("filiado.instituicaoConvenio", instituicao));

		if (titulo.getFiliado() != null)
			criteriaTitulos.add(Restrictions.eq("filiado", titulo.getFiliado()));

		if (titulo.getNumeroTitulo() != null)
			criteriaTitulos.add(Restrictions.ilike("numeroTitulo", titulo.getNumeroTitulo(), MatchMode.EXACT));

		if (titulo.getNomeDevedor() != null)
			criteriaTitulos.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE));

		if (titulo.getDocumentoDevedor() != null)
			criteriaTitulos.add(Restrictions.ilike("documentoDevedor", titulo.getDocumentoDevedor(), MatchMode.ANYWHERE));

		if (titulo.getDataEmissao() != null)
			criteriaTitulos.add(Restrictions.between("dataEmissao", titulo.getDataEmissao(), titulo.getDataEmissao()));

		if (titulo.getPracaProtesto() != null)
			criteriaTitulos.add(Restrictions.eq("pracaProtesto", titulo.getPracaProtesto()));
		
		criteriaTitulos.addOrder(Order.desc("nomeDevedor"));
		return criteriaTitulos.list();
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> buscarTitulosParaRelatorioFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
	        Municipio pracaProtesto) {
		Criteria criteria = getCriteria(TituloFiliado.class);

		if (filiado != null)
			criteria.add(Restrictions.eq("filiado", filiado));

		if (pracaProtesto != null) {
			criteria.add(Restrictions.eq("pracaProtesto", pracaProtesto));
		}

		criteria.add(Restrictions.between("dataEmissao", dataInicio, dataFim));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> buscarTitulosParaRelatorioConvenio(Instituicao convenio, Filiado filiado, LocalDate dataInicio,
	        LocalDate dataFim, Municipio pracaProtesto) {
		Criteria criteria = getCriteria(TituloFiliado.class);

		if (convenio != null) {
			criteria.createAlias("filiado", "filiado");
			criteria.add(Restrictions.eq("filiado.instituicaoConvenio", convenio));
		}

		if (filiado != null)
			criteria.add(Restrictions.eq("filiado", filiado));

		if (pracaProtesto != null) {
			criteria.add(Restrictions.eq("pracaProtesto", pracaProtesto));
		}

		criteria.add(Restrictions.between("dataEmissao", dataInicio, dataFim));
		return criteria.list();
	}

	public void marcarComoEnviadoParaCRA(List<TituloFiliado> listaTitulosConvenios) {
		try {
			for (TituloFiliado tituloFiliado : listaTitulosConvenios) {
				Transaction transaction = getBeginTransation();
				tituloFiliado.setSituacaoTituloConvenio(SituacaoTituloConvenio.EM_PROCESSO);
				update(tituloFiliado);
				transaction.commit();
				logger.info("Titulo Filiado enviado para o cartório com sucesso {" + tituloFiliado.getId() + "}");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			new InfraException(ex.getMessage(), ex.getCause());
		}
	}
}
