package br.com.ieptbto.cra.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamentoConvenio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
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
	
	@SuppressWarnings("unchecked")
	public List<Avalista> avalistasTituloFiliado(TituloFiliado titulo) {
		Criteria criteriaAvalistas = getCriteria(Avalista.class);
		criteriaAvalistas.add(Restrictions.eq("tituloFiliado", titulo));
		
		return criteriaAvalistas.list();
	}

	public int getNumeroDeTitulosPendentes() {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.add(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.ENVIADO));
		return criteria.list().size();
	}

	@SuppressWarnings("unchecked")
	public TituloRemessa buscarTituloDoConvenioNaCra(TituloFiliado tituloFiliado) {
		String nossoNumero = tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao() + tituloFiliado.getId();
		
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("nossoNumero", StringUtils.rightPad(nossoNumero, 15, "0")));
		List<TituloRemessa> titulosRemessa = criteria.list();
		for (TituloRemessa titulo : titulosRemessa) {
			if (titulo.getDataCadastro().before(tituloFiliado.getDataEntrada().toDate())) {
				return titulo;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> consultarTitulosFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim, 
			Municipio pracaProtesto, TituloFiliado tituloFiliado, SituacaoTituloConvenio situacaoTituloAguardando) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.add(Restrictions.eq("filiado", filiado));

		if (tituloFiliado.getNumeroTitulo() != null)
			criteria.add(Restrictions.ilike("numeroTitulo", tituloFiliado.getNumeroTitulo(), MatchMode.EXACT));

		if (tituloFiliado.getNomeDevedor() != null)
			criteria.add(Restrictions.ilike("nomeDevedor", tituloFiliado.getNomeDevedor(), MatchMode.ANYWHERE));

		if (tituloFiliado.getDocumentoDevedor() != null)
			criteria.add(Restrictions.ilike("documentoDevedor", tituloFiliado.getDocumentoDevedor(), MatchMode.ANYWHERE));

		if (dataInicio != null && dataFim != null)
			criteria.add(Restrictions.between("dataEnvioCRA", dataInicio, dataFim));

		if (pracaProtesto != null)
			criteria.add(Restrictions.ilike("pracaProtesto", pracaProtesto));
		
		if (situacaoTituloAguardando != null) 
			criteria.add(Restrictions.ne("situacaoTituloConvenio", situacaoTituloAguardando));
			
		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<TituloFiliado> consultarTitulosConvenio(Instituicao instituicao, LocalDate dataInicio, LocalDate dataFim, 
			Filiado filiado, Municipio pracaProtesto, TituloFiliado tituloFiliado) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.createAlias("filiado", "filiado");
		criteria.createAlias("filiado.instituicaoConvenio", "filiado.instituicaoConvenio");
		criteria.add(Restrictions.eq("filiado.instituicaoConvenio", instituicao));

		if (tituloFiliado.getNumeroTitulo() != null)
			criteria.add(Restrictions.ilike("numeroTitulo", tituloFiliado.getNumeroTitulo(), MatchMode.EXACT));

		if (tituloFiliado.getNomeDevedor() != null)
			criteria.add(Restrictions.ilike("nomeDevedor", tituloFiliado.getNomeDevedor(), MatchMode.ANYWHERE));

		if (tituloFiliado.getDocumentoDevedor() != null)
			criteria.add(Restrictions.eq("filiado", tituloFiliado.getFiliado()));

		if (filiado != null) 
			criteria.add(Restrictions.eq("filiado", filiado));
		
		if (dataInicio != null && dataFim != null)
			criteria.add(Restrictions.between("dataEnvioCRA", dataInicio, dataFim));

		if (pracaProtesto != null)
			criteria.add(Restrictions.eq("pracaProtesto", pracaProtesto));

		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.addOrder(Order.desc("nomeDevedor"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> buscarTitulosParaRelatorioFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
	        TipoRelatorio tipoRelatorio, Municipio pracaProtesto) {
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
		Transaction transaction = getBeginTransation();
		try {
			for (TituloFiliado tituloFiliado : listaTitulosConvenios) {
				tituloFiliado.setSituacaoTituloConvenio(SituacaoTituloConvenio.EM_PROCESSO);
				update(tituloFiliado);
				logger.info("Titulo Filiado enviado para o cartório com sucesso {" + tituloFiliado.getId() + "}");
			}
			transaction.commit();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			new InfraException(ex.getMessage(), ex.getCause());
		}
	}

	public int quatidadeTitulosPendentesEnvioFiliados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.add(Restrictions.eq("filiado", filiado));
		criteria.add(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.AGUARDANDO));
		criteria.add(Restrictions.between("dataEnvioCRA", dataInicio, dataFim));
		return criteria.list().size();
	}

	public int quatidadeTitulosEmProcessoFiliados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.add(Restrictions.eq("filiado", filiado));
		criteria.add(Restrictions.or(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.EM_PROCESSO),Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.ENVIADO)));
		criteria.add(Restrictions.between("dataEnvioCRA", dataInicio, dataFim));
		return criteria.list().size();
	}

	public int quatidadeTitulosFnalizados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.add(Restrictions.eq("filiado", filiado));
		criteria.add(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.FINALIZADO));
		criteria.add(Restrictions.between("dataEnvioCRA", dataInicio, dataFim));
		return criteria.list().size();
	}

	public SolicitacaoDesistenciaCancelamentoConvenio enviarSolicitacaoDesistenciaCancelamento(SolicitacaoDesistenciaCancelamentoConvenio solicitacao) {
		Transaction transaction = getBeginTransation();
		try {
			solicitacao.getTituloFiliado().setSolicitacaoDesistenciaCancelamento(solicitacao);
			solicitacao.setTituloFiliado(update(solicitacao.getTituloFiliado()));
			save(solicitacao);
			logger.info("Solicitação de Desistência ou Cancelamento enviada !");
			
			transaction.commit();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			new InfraException(ex.getMessage(), ex.getCause());
		}
		return solicitacao;
	}
}
