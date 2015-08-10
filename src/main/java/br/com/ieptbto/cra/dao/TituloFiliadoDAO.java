package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Avalista;
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
	
	@Autowired
	AvalistaDAO avalistaDAO;
	
	public TituloFiliado salvar(TituloFiliado titulo) {
		TituloFiliado novoTitulo = new TituloFiliado();
		Transaction transaction = getBeginTransation();

		try {
			novoTitulo = save(titulo);
			for (Avalista avalista : titulo.getAvalistas()) {
				avalista.setTipoDocumento(verificarTipoDocumento(avalista.getDocumento()));
				avalista.setTituloFiliado(novoTitulo);
				avalistaDAO.saveOrUpdate(avalista);
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return novoTitulo;
	}

	private String verificarTipoDocumento(String documento) {
		if (documento.length() == 14) {
			return "002";
		}
		return "001";
	}

	public TituloFiliado alterar(TituloFiliado titulo) {
		TituloFiliado alterado = new TituloFiliado();
		Transaction transaction = getBeginTransation();

		try {
			alterado = update(titulo);
			for (Avalista avalista : titulo.getAvalistas()) {
				avalista.setTipoDocumento(verificarTipoDocumento(avalista.getDocumento()));
				avalista.setTituloFiliado(alterado);
				avalistaDAO.saveOrUpdate(avalista);
			}
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
		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.addOrder(Order.desc("nomeDevedor"));
		return criteria.list();
	}

	public void enviarTitulosPendentes(List<TituloFiliado> listaTitulosFiliado) {
		Transaction transaction = getBeginTransation();

		try {
			for (TituloFiliado titulo : listaTitulosFiliado) {
				titulo.setSituacaoTituloConvenio(SituacaoTituloConvenio.ENVIADO);
				titulo.setDataEnvioCRA(new LocalDate());
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
		criteria.createAlias("pracaProtesto", "p");
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
	public List<TituloFiliado> consultarTitulosFiliado(Usuario user, TituloFiliado tituloBuscado) {
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

		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteriaTitulos.addOrder(Order.asc("nomeDevedor"));
		return criteriaTitulos.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<TituloFiliado> consultarTitulosConvenio(Instituicao instituicao, TituloFiliado titulo) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.createAlias("filiado", "filiado");
		criteria.createAlias("filiado.instituicaoConvenio", "filiado.instituicaoConvenio");
		criteria.add(Restrictions.eq("filiado.instituicaoConvenio", instituicao));

		if (titulo.getFiliado() != null)
			criteria.add(Restrictions.eq("filiado", titulo.getFiliado()));

		if (titulo.getNumeroTitulo() != null)
			criteria.add(Restrictions.ilike("numeroTitulo", titulo.getNumeroTitulo(), MatchMode.EXACT));

		if (titulo.getNomeDevedor() != null)
			criteria.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE));

		if (titulo.getDocumentoDevedor() != null)
			criteria.add(Restrictions.ilike("documentoDevedor", titulo.getDocumentoDevedor(), MatchMode.ANYWHERE));

		if (titulo.getDataEmissao() != null)
			criteria.add(Restrictions.between("dataEmissao", titulo.getDataEmissao(), titulo.getDataEmissao()));

		if (titulo.getPracaProtesto() != null)
			criteria.add(Restrictions.eq("pracaProtesto", titulo.getPracaProtesto()));

		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.addOrder(Order.desc("nomeDevedor"));
		return criteria.list();
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

		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.add(Restrictions.between("dataEnvioCRA", dataInicio, dataFim));
		criteria.addOrder(Order.asc("nomeDevedor"));
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

		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.add(Restrictions.between("dataEnvioCRA", dataInicio, dataFim));
		criteria.addOrder(Order.asc("nomeDevedor"));
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
