package br.com.ieptbto.cra.dao;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class TituloFiliadoDAO extends AbstractBaseDAO {

	@Autowired
	private AvalistaDAO avalistaDAO;

	public TituloFiliado salvar(TituloFiliado titulo) {
		Transaction transaction = getBeginTransation();

		try {
			if (titulo.getId() != 0) {
				titulo = update(titulo);
			} else {
				titulo = save(titulo);
			}

			for (Avalista avalista : titulo.getAvalistas()) {
				if (avalista.getNome() != null && avalista.getDocumento() != null) {
					if (!avalista.getNome().trim().isEmpty() && !avalista.getDocumento().trim().isEmpty()) {
						avalista.setTipoDocumento(verificarTipoDocumento(avalista.getDocumento()));
						avalista.setTituloFiliado(titulo);
						avalistaDAO.saveOrUpdate(avalista);
					}
				}
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return titulo;
	}

	private String verificarTipoDocumento(String documento) {
		if (documento.length() == 14) {
			return "002";
		}
		return "001";
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
	public List<TituloFiliado> buscarTitulosParaEnvio(Filiado empresaFiliada, SetorFiliado setorFiliado) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.createAlias("filiado", "filiado");
		criteria.add(Restrictions.eq("filiado", empresaFiliada));

		if (setorFiliado != null) {
			criteria.add(Restrictions.eq("setor", setorFiliado));
		}
		criteria.add(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.AGUARDANDO));
		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}

	public void enviarTitulosPendentes(List<TituloFiliado> listaTitulosFiliado) {
		Transaction transaction = getBeginTransation();

		try {
			for (TituloFiliado titulo : listaTitulosFiliado) {
				titulo.setSituacaoTituloConvenio(SituacaoTituloConvenio.ENVIADO);
				titulo.setDataEnvioCRA(new LocalDate().toDate());
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
			if (titulo.getDataCadastro().before(tituloFiliado.getDataEntrada())) {
				return titulo;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> consultarTitulosFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim, Municipio pracaProtesto,
			TituloFiliado tituloFiliado, SituacaoTituloConvenio situacaoTituloAguardando) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.add(Restrictions.eq("filiado", filiado));

		if (tituloFiliado.getNumeroTitulo() != null)
			criteria.add(Restrictions.ilike("numeroTitulo", tituloFiliado.getNumeroTitulo(), MatchMode.ANYWHERE));

		if (tituloFiliado.getNomeDevedor() != null)
			criteria.add(Restrictions.ilike("nomeDevedor", tituloFiliado.getNomeDevedor(), MatchMode.ANYWHERE));

		if (tituloFiliado.getDocumentoDevedor() != null)
			criteria.add(Restrictions.ilike("documentoDevedor", tituloFiliado.getDocumentoDevedor(), MatchMode.ANYWHERE));

		if (dataInicio != null && dataFim != null)
			criteria.add(Restrictions.between("dataEnvioCRA", new java.sql.Date(dataInicio.toDate().getTime()), new java.sql.Date(dataFim.toDate().getTime())));

		if (pracaProtesto != null)
			criteria.add(Restrictions.ilike("pracaProtesto", pracaProtesto));

		if (situacaoTituloAguardando != null)
			criteria.add(Restrictions.ne("situacaoTituloConvenio", situacaoTituloAguardando));

		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> consultarTitulosConvenio(Instituicao instituicao, LocalDate dataInicio, LocalDate dataFim, Filiado filiado,
			Municipio pracaProtesto, TituloFiliado tituloFiliado) {
		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.createAlias("filiado", "filiado");
		criteria.createAlias("filiado.instituicaoConvenio", "filiado.instituicaoConvenio");
		criteria.add(Restrictions.eq("filiado.instituicaoConvenio", instituicao));

		if (tituloFiliado.getNumeroTitulo() != null)
			criteria.add(Restrictions.ilike("numeroTitulo", tituloFiliado.getNumeroTitulo(), MatchMode.ANYWHERE));

		if (tituloFiliado.getNomeDevedor() != null)
			criteria.add(Restrictions.ilike("nomeDevedor", tituloFiliado.getNomeDevedor(), MatchMode.ANYWHERE));

		if (tituloFiliado.getDocumentoDevedor() != null)
			criteria.add(Restrictions.eq("filiado", tituloFiliado.getFiliado()));

		if (filiado != null)
			criteria.add(Restrictions.eq("filiado", filiado));

		if (dataInicio != null && dataFim != null)
			criteria.add(Restrictions.between("dataEnvioCRA", new java.sql.Date(dataInicio.toDate().getTime()), new java.sql.Date(dataFim.toDate().getTime())));

		if (pracaProtesto != null)
			criteria.add(Restrictions.eq("pracaProtesto", pracaProtesto));

		criteria.add(Restrictions.ne("situacaoTituloConvenio", SituacaoTituloConvenio.REMOVIDO));
		criteria.addOrder(Order.desc("nomeDevedor"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TituloFiliado> buscarTitulosParaRelatorioFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
			SituacaoTituloRelatorio tipoRelatorio, Municipio pracaProtesto) {
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
	public List<TituloFiliado> buscarTitulosParaRelatorioConvenio(Instituicao convenio, Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
			Municipio pracaProtesto) {
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
				tituloFiliado = buscarPorPK(tituloFiliado, TituloFiliado.class);
				tituloFiliado.setSituacaoTituloConvenio(SituacaoTituloConvenio.EM_PROCESSO);
				update(tituloFiliado);
			}
			transaction.commit();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			new InfraException("Não foi possível marcar os títulos de convênios como enviados! Favor entrar em contato com a CRA...");
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
		criteria.add(Restrictions.or(Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.EM_PROCESSO),
				Restrictions.eq("situacaoTituloConvenio", SituacaoTituloConvenio.ENVIADO)));
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

	@SuppressWarnings("unchecked")
	public List<TituloRemessa> buscarListaTitulos(Usuario user, LocalDate dataInicio, Instituicao instiuicaoCartorio, String numeroTitulo, String nomeDevedor,
			String documentoDevedor, String codigoFiliado) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.instituicaoOrigem", user.getInstituicao()));

		if (codigoFiliado != null && !codigoFiliado.trim().isEmpty()) {
			criteria.add(Restrictions.ilike("agenciaCodigoCedente", codigoFiliado, MatchMode.EXACT));
		}
		if (numeroTitulo != null && numeroTitulo.trim() != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike("numeroTitulo", numeroTitulo.trim(), MatchMode.EXACT));
		}
		if (nomeDevedor != null && nomeDevedor.trim() != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike("nomeDevedor", nomeDevedor.trim(), MatchMode.ANYWHERE));
		}
		if (documentoDevedor != null && documentoDevedor.trim() != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike("numeroIdentificacaoDevedor", documentoDevedor.trim(), MatchMode.ANYWHERE));
		}
		if (dataInicio != null) {
			criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataInicio.plusDays(1)));
		}
		if (instiuicaoCartorio != null) {
			criteria.createAlias("remessa.cabecalho", "cabecalho");
			criteria.add(Restrictions.ilike("cabecalho.codigoMunicipio", instiuicaoCartorio.getMunicipio().getCodigoIBGE()));
		}
		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}

	@SuppressWarnings("rawtypes")
	public TituloFiliado buscarTituloFiliadoProcessadoNaCra(String nossoNumero, String numeroDoTitulo) {
		Query query = getSession()
				.createSQLQuery("SELECT tit.id_titulo_filiado FROM tb_titulo_filiado AS tit " + "INNER JOIN tb_filiado AS fil ON tit.filiado_id=fil.id_filiado "
						+ "INNER JOIN tb_instituicao AS ins ON fil.instituicao_id=ins.id_instituicao "
						+ "WHERE rpad(concat(ins.codigo_compensacao,tit.id_titulo_filiado,null, null),15,'0' )='" + nossoNumero + "' "
						+ "AND tit.numero_titulo='" + numeroDoTitulo.trim() + "';");

		List resultQuery = query.list();
		if (resultQuery.size() > 1) {
			return null;
		}
		int id = 0;
		Iterator iterator = resultQuery.iterator();
		while (iterator.hasNext()) {
			id = (Integer) iterator.next();
		}

		Criteria criteria = getCriteria(TituloFiliado.class);
		criteria.createAlias("usuarioEntradaManual", "usuarioEntradaManual");
		criteria.add(Restrictions.eq("id", id));
		return TituloFiliado.class.cast(criteria.uniqueResult());
	}
}
