package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.CabecalhoCnp;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
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

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ArquivoCnp salvarArquivoCartorioCentralNacionalProtesto(Usuario user, ArquivoCnp arquivoCnp) {
		Transaction transaction = getBeginTransation();
		try {
			arquivoCnp = save(arquivoCnp);

			for (RemessaCnp remessaCnp : arquivoCnp.getRemessasCnp()) {
				remessaCnp.setArquivoLiberadoConsulta(false);
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
			logger.info("O arquivo CNP do cartório " + user.getInstituicao().getNomeFantasia() + " foi salvo na base de dados. ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esses dados na base.");
		}
		return arquivoCnp;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ArquivoCnp getArquivoCnpHojeInstituicao(Instituicao instituicao) {
		Criteria criteria = getCriteria(ArquivoCnp.class);
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicao));
		criteria.add(Restrictions.eq("dataEnvio", new LocalDate()));
		return ArquivoCnp.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<RemessaCnp> buscarRemessasCnpPendentes() {
		List<RemessaCnp> remessaGeradas = new ArrayList<RemessaCnp>();
		Criteria criteria = getCriteria(RemessaCnp.class);
		criteria.add(Restrictions.eq("arquivoLiberadoConsulta", false));

		List<RemessaCnp> remessas = criteria.list();
		for (RemessaCnp remessa : remessas) {
			Criteria criteriaTituloCNP = getCriteria(TituloCnp.class);
			criteriaTituloCNP.add(Restrictions.eq("remessa", remessa));

			remessa.setTitulos(criteriaTituloCNP.list());
			remessaGeradas.add(remessa);
		}
		return remessaGeradas;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = false)
	public void salvarArquivoCnpNacional(ArquivoCnp arquivoCnp) {
		Transaction transaction = getBeginTransation();

		try {
			for (RemessaCnp remessaCnp : arquivoCnp.getRemessasCnp()) {
				remessaCnp.setDataLiberacaoConsulta(new LocalDate());
				remessaCnp.setArquivoLiberadoConsulta(true);
				update(remessaCnp);
			}
			transaction.commit();
			logger.info("O arquivo CNP do nacional foi disponibilizado com sucesso. ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esses dados na base.");
		}
	}

	// @Transactional(propagation = Propagation.NOT_SUPPORTED)
	// public void salvarArquivoCnpNacional(ArquivoCnp arquivoCnp) {
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	// try {
	// for (RemessaCnp remessaCnp : arquivoCnp.getRemessasCnp()) {
	// Query query = createSQLQuery("UPDATE tb_remessa_cnp AS rem " + "SET
	// data_liberacao='" + dateFormat.format(new LocalDate().toDate())
	// + "', arquivo_liberado_consulta=true " + "WHERE id_remessa_cnp=" +
	// remessaCnp.getId());
	// query.executeUpdate();
	// }
	//
	// } catch (Exception ex) {
	// logger.error(ex.getMessage(), ex);
	// throw new InfraException("Não foi possível inserir esses dados na
	// base.");
	// }
	// }

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RemessaCnp isArquivoJaDisponibilizadoConsultaPorData(LocalDate dataLiberacao) {
		Criteria criteria = getCriteria(RemessaCnp.class);
		criteria.add(Restrictions.eq("dataLiberacaoConsulta", dataLiberacao));
		criteria.setMaxResults(1);
		return RemessaCnp.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<RemessaCnp> buscarRemessasCnpPorData(LocalDate dataLiberacao) {
		List<RemessaCnp> remessaGeradas = new ArrayList<RemessaCnp>();
		Criteria criteria = getCriteria(RemessaCnp.class);
		criteria.add(Restrictions.eq("dataLiberacaoConsulta", dataLiberacao));

		List<RemessaCnp> remessas = criteria.list();
		for (RemessaCnp remessa : remessas) {
			Criteria criteriaTituloCNP = getCriteria(TituloCnp.class);
			criteriaTituloCNP.add(Restrictions.eq("remessa", remessa));

			remessa.setTitulos(criteriaTituloCNP.list());
			remessaGeradas.add(remessa);
		}
		return remessaGeradas;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<TituloCnp> consultarProtestos(String documentoDevedor) {
		Criteria criteria = getCriteria(TituloCnp.class);
		criteria.add(Restrictions.ilike("numeroDocumentoDevedor", documentoDevedor, MatchMode.EXACT));
		criteria.add(Restrictions.eq("tipoInformacao", "P"));
		criteria.addOrder(org.hibernate.criterion.Order.asc("cidadeDevedor"));
		return criteria.list();
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public TituloCnp consultarCancelamento(String documentoDevedor, String numeroProtocoloCartorio) {
		Criteria criteria = getCriteria(TituloCnp.class);
		criteria.add(Restrictions.ilike("numeroDocumentoDevedor", documentoDevedor, MatchMode.EXACT));
		criteria.add(Restrictions.ilike("numeroProtocoloCartorio", numeroProtocoloCartorio, MatchMode.EXACT));
		criteria.add(Restrictions.eq("tipoInformacao", "C"));
		return TituloCnp.class.cast(criteria.uniqueResult());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Municipio carregarMunicipioCartorio(Municipio municipio) {
		return buscarPorPK(municipio, Municipio.class);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int buscarSequencialCabecalhoCnp(String codigoMunicipio) {
		Criteria criteria = getCriteria(CabecalhoCnp.class);
		criteria.add(Restrictions.ilike("emBranco53", codigoMunicipio, MatchMode.EXACT));
		criteria.setProjection(Projections.max("numeroRemessaArquivo"));
		String resultado = criteria.uniqueResult().toString();

		Integer numeroSequencial = 1;
		if (resultado != null) {
			if (StringUtils.isNotEmpty(resultado.trim()) && StringUtils.isNotBlank(resultado.trim())) {
				numeroSequencial = Integer.valueOf(resultado);
			}
		}
		return numeroSequencial;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Instituicao> consultarCartoriosCentralNacionalProtesto() {
		Criteria criteria = getCriteria(ArquivoCnp.class);
		criteria.setProjection(Projections.groupProperty("instituicaoEnvio"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<TituloCnp> buscarTitulosPorMunicipio(Municipio municipio) {
		Criteria criteria = getCriteria(TituloCnp.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.emBranco53", municipio.getCodigoIBGE()));
		return criteria.list();
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public CabecalhoCnp ultimoCabecalhoCnpCartorio(Municipio municipio) {
		Criteria criteria = getCriteria(CabecalhoCnp.class);
		criteria.add(Restrictions.eq("emBranco53", municipio.getCodigoIBGE()));
		criteria.addOrder(Order.desc("id"));
		criteria.setMaxResults(1);
		return CabecalhoCnp.class.cast(criteria.uniqueResult());
	}
}