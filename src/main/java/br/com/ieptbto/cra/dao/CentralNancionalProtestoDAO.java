package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LoteCnp;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.RegistroCnp;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoRegistroCnp;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.regra.FabricaRegraValidacaoCNP;
import br.com.ieptbto.cra.util.CpfCnpjUtil;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class CentralNancionalProtestoDAO extends AbstractBaseDAO {

	@Autowired
	FabricaRegraValidacaoCNP validarRegistroCnp;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void salvarLiberacaoLoteCnp(LoteCnp lote) {
		Transaction transaction = getBeginTransation();

		try {
			lote.setDataLiberacao(new LocalDate().toDate());
			lote.setStatus(true);
			update(lote);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível marcar os lotes de registros da cnp como liberados na dados na base.");
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public LoteCnp salvarLote5Anos(LoteCnp loteCnp) {
		Transaction transaction = getBeginTransation();

		try {
			loteCnp = save(loteCnp);

			for (RegistroCnp registroCnp : loteCnp.getRegistrosCnp()) {
				if (registroCnp.getTipoRegistroCnp().equals(TipoRegistroCnp.PROTESTO)) {
					if (validarRegistroCnp.validarProtesto(registroCnp)) {
						registroCnp.setLoteCnp(loteCnp);
						save(registroCnp);
					}
				}
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return loteCnp;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public LoteCnp salvarLoteRegistrosCnp(Usuario user, LoteCnp lote) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();

		try {
			lote = save(lote);
			transaction.commit();
			
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			return null;
		}
		
		List<RegistroCnp> registrosSalvos = new ArrayList<>();
		for (RegistroCnp registro : lote.getRegistrosCnp()) {
			transaction = session.beginTransaction(); 

			if (registro.getTipoRegistroCnp().equals(TipoRegistroCnp.PROTESTO)) {
				registro.setLoteCnp(lote);

				try {
					registro = save(registro);
					transaction.commit();
				} catch (ConstraintViolationException ex) {
					transaction.rollback();
					session.clear();
					logger.info("Registro de Protesto Rejeitado por Duplicidade: " + registro.getNomeDevedor() + " [NumeroProtocoloCartorio=" + registro.getNumeroProtocoloCartorio() + "] " 
							+ " [DocumentoDevedor=" + registro.getNumeroDocumentoDevedor() + registro.getComplementoDocumentoDevedor() + registro.getDigitoControleDocumentoDevedor() + "]");
				}
			} else if (registro.getTipoRegistroCnp().equals(TipoRegistroCnp.CANCELAMENTO)) {
				RegistroCnp registroProtesto = buscarRegistroProtesto(lote.getInstituicaoOrigem(), registro);
				if (registroProtesto != null) {
					registro.setDataProtesto(registroProtesto.getDataProtesto());
					registro.setValorProtesto(registroProtesto.getValorProtesto());
					registro.setLoteCnp(lote);

					try {
						registro = save(registro);
						transaction.commit();
					} catch (ConstraintViolationException ex) {
						transaction.rollback();
						session.clear();
						logger.info("Registro de Cancelamento Rejeitado por Duplicidade: " + registro.getNomeDevedor() + " [NumeroProtocoloCartorio=" + registro.getNumeroProtocoloCartorio() + "] " 
						+ " [DocumentoDevedor=" + registro.getNumeroDocumentoDevedor() + registro.getComplementoDocumentoDevedor() + registro.getDigitoControleDocumentoDevedor() + "]");
					}
				}
			}
			if (registro != null && registro.getId() != 0) { 
				registrosSalvos.add(registro);
			}
		}
		lote.setRegistrosCnp(null);
		if (registrosSalvos.isEmpty()) {
			return null;
		}
		lote.setRegistrosCnp(registrosSalvos);
		return lote;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public LoteCnp isLoteLiberadoConsultaPorData(LocalDate dataLiberaca) {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.setMaxResults(1);
		criteria.add(Restrictions.eq("dataLiberacao", new Date(dataLiberaca.toDate().getTime())));
		criteria.add(Restrictions.eq("status", true));
		return LoteCnp.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<RegistroCnp> consultarProtestos(String documentoDevedor) {
		Criteria criteria = getCriteria(RegistroCnp.class);
		criteria.add(Restrictions.eq("numeroDocumentoDevedor", CpfCnpjUtil.buscarNumeroDocumento(documentoDevedor)));
		criteria.add(Restrictions.eq("complementoDocumentoDevedor", CpfCnpjUtil.buscarComplementoDocumento(documentoDevedor)));
		criteria.add(Restrictions.eq("digitoControleDocumentoDevedor", CpfCnpjUtil.calcularDigitoControle(documentoDevedor)));
		criteria.add(Restrictions.eq("tipoRegistroCnp", TipoRegistroCnp.PROTESTO.toString()));
		return criteria.list();
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RegistroCnp consultarCancelamento(String documentoDevedor, String numeroProtocoloCartorio) {
		Criteria criteria = getCriteria(RegistroCnp.class);
		criteria.add(Restrictions.eq("numeroDocumentoDevedor", CpfCnpjUtil.buscarNumeroDocumento(documentoDevedor)));
		criteria.add(Restrictions.eq("complementoDocumentoDevedor", CpfCnpjUtil.buscarComplementoDocumento(documentoDevedor)));
		criteria.add(Restrictions.eq("digitoControleDocumentoDevedor", CpfCnpjUtil.calcularDigitoControle(documentoDevedor)));
		criteria.add(Restrictions.eq("tipoRegistroCnp", TipoRegistroCnp.CANCELAMENTO.toString()));
		criteria.add(Restrictions.eq("numeroProtocoloCartorio", numeroProtocoloCartorio));
		return RegistroCnp.class.cast(criteria.uniqueResult());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RegistroCnp buscarProtestoCancelamento(Municipio municipio, RegistroCnp registro) {
		Criteria criteria = getCriteria(RegistroCnp.class);
		criteria.createAlias("loteCnp", "loteCnp");
		criteria.createAlias("loteCnp.instituicaoOrigem", "instituicaoOrigem");
		criteria.add(Restrictions.eq("instituicaoOrigem.municipio", municipio));
		criteria.add(Restrictions.eq("tipoInformacao", "P"));
		criteria.add(Restrictions.eq("dataProtesto", registro.getDataProtesto()));
		criteria.add(Restrictions.eq("numeroProtocoloCartorio", registro.getNumeroProtocoloCartorio()));
		criteria.setMaxResults(1);
		return RegistroCnp.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<Instituicao> buscarCartoriosComLotesPendentesEnvioNacional() {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.add(Restrictions.eq("status", false));
		criteria.setProjection(Projections.groupProperty("instituicaoOrigem"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<Instituicao> buscarCartoriosEviaramLotesNacionalPorData(LocalDate data) {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.add(Restrictions.eq("status", true));
		criteria.add(Restrictions.eq("dataLiberacao", new Date(data.toDate().getTime())));
		criteria.setProjection(Projections.groupProperty("instituicaoOrigem"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<LoteCnp> buscarLotesPendentesEnvio(Instituicao cartorio) {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.add(Restrictions.eq("status", false));
		criteria.add(Restrictions.eq("instituicaoOrigem", cartorio));

		List<LoteCnp> lotes = criteria.list();
		for (LoteCnp loteCnp : lotes) {
			Criteria criteriaRegistros = getCriteria(RegistroCnp.class);
			criteriaRegistros.add(Restrictions.eq("loteCnp", loteCnp));
			criteriaRegistros.addOrder(Order.desc("tipoRegistroCnp")).addOrder(Order.asc("numeroProtocoloCartorio"));
			loteCnp.setRegistrosCnp(new ArrayList<RegistroCnp>());
			loteCnp.getRegistrosCnp().addAll(criteriaRegistros.list());
		}
		return lotes;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<LoteCnp> buscarLotesProtesto5Anos(Instituicao cartorio) {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.add(Restrictions.eq("status", false));
		criteria.add(Restrictions.eq("instituicaoOrigem", cartorio));

		List<LoteCnp> lotes = criteria.list();
		for (LoteCnp loteCnp : lotes) {
			Criteria criteriaRegistros = getCriteria(RegistroCnp.class);
			criteriaRegistros.add(Restrictions.eq("loteCnp", loteCnp));
			criteriaRegistros.add(Restrictions.eq("tipoRegistroCnp", TipoRegistroCnp.PROTESTO));
			criteriaRegistros.addOrder(Order.desc("tipoRegistroCnp")).addOrder(Order.asc("numeroProtocoloCartorio"));
			loteCnp.setRegistrosCnp(new ArrayList<RegistroCnp>());
			loteCnp.getRegistrosCnp().addAll(criteriaRegistros.list());
		}
		return lotes;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<LoteCnp> buscarLotesParaEnvioPorDate(Instituicao cartorio, LocalDate data) {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.add(Restrictions.eq("dataLiberacao", new Date(data.toDate().getTime())));
		criteria.add(Restrictions.eq("status", true));
		criteria.add(Restrictions.eq("instituicaoOrigem", cartorio));

		List<LoteCnp> lotes = criteria.list();
		for (LoteCnp loteCnp : lotes) {
			Criteria criteriaRegistros = getCriteria(RegistroCnp.class);
			criteriaRegistros.add(Restrictions.eq("loteCnp", loteCnp));
			criteriaRegistros.addOrder(Order.desc("tipoRegistroCnp")).addOrder(Order.asc("numeroProtocoloCartorio"));
			loteCnp.setRegistrosCnp(new ArrayList<RegistroCnp>());
			loteCnp.getRegistrosCnp().addAll(criteriaRegistros.list());
		}
		return lotes;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public String gerarSequencialCnp(Instituicao instituicao) {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.add(Restrictions.eq("instituicaoOrigem", instituicao));
		criteria.setProjection(Projections.max("sequencialLiberacao"));
		Object resultado = criteria.uniqueResult();

		String sequencial = "0";
		if (resultado != null) {
			sequencial = resultado.toString();
		}
		if (!sequencial.trim().isEmpty()) {
			return Integer.toString(Integer.valueOf(sequencial) + 1);
		}
		return Integer.toString(1);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public String gerarSequencialCnp(Instituicao instituicao, LocalDate data) {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.add(Restrictions.eq("instituicaoOrigem", instituicao));
		criteria.add(Restrictions.eq("dataLiberacao", new Date(data.toDate().getTime())));
		criteria.setProjection(Projections.max("sequencialLiberacao"));
		String resultado = criteria.uniqueResult().toString();

		if (!resultado.trim().isEmpty()) {
			return resultado;
		}
		return Integer.toString(1);
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Instituicao> consultarCartoriosCentralNacionalProtesto() {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.setProjection(Projections.groupProperty("instituicaoOrigem"));
		return criteria.list();
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RegistroCnp buscarRegistroProtesto(Instituicao instituicao, RegistroCnp registro) {
		Query query = getSession().createQuery("select r from RegistroCnp r "
				+ "inner join r.loteCnp l "
				+ "where r.tipoRegistroCnp= :tipoRegistroCnp "
				+ "and r.numeroDocumentoDevedor= :numeroDocumentoDevedor "
				+ "and r.digitoControleDocumentoDevedor= :digitoControleDocumentoDevedor "
				+ "and r.numeroProtocoloCartorio= :numeroProtocoloCartorio "
				+ "and l.instituicaoOrigem= :instituicaoOrigem ");
		query.setParameter("tipoRegistroCnp", TipoRegistroCnp.PROTESTO);
		query.setParameter("numeroDocumentoDevedor", registro.getNumeroDocumentoDevedor());
		query.setParameter("digitoControleDocumentoDevedor", registro.getDigitoControleDocumentoDevedor());
		query.setParameter("numeroProtocoloCartorio", registro.getNumeroProtocoloCartorio());
		query.setParameter("instituicaoOrigem", instituicao);
		query.setMaxResults(1);
		return RegistroCnp.class.cast(query.uniqueResult());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RegistroCnp buscarRegistroCancelamento(Instituicao instituicao, RegistroCnp registro) {
		Criteria criteria = getCriteria(RegistroCnp.class);
		criteria.createAlias("loteCnp", "loteCnp");
		criteria.add(Restrictions.eq("tipoRegistroCnp", TipoRegistroCnp.CANCELAMENTO));
		criteria.add(Restrictions.eq("numeroDocumentoDevedor", registro.getNumeroDocumentoDevedor()));
		criteria.add(Restrictions.eq("digitoControleDocumentoDevedor", registro.getDigitoControleDocumentoDevedor()));
		criteria.add(Restrictions.eq("numeroProtocoloCartorio", registro.getNumeroProtocoloCartorio()));
		criteria.add(Restrictions.eq("loteCnp.instituicaoOrigem", instituicao));
		criteria.setMaxResults(1);
		return RegistroCnp.class.cast(criteria.uniqueResult());
	}

	public LoteCnp buscarLote5anosInteituicao(Instituicao instituicao) {
		Criteria criteria = getCriteria(LoteCnp.class);
		criteria.add(Restrictions.eq("instituicaoOrigem", instituicao));
		criteria.add(Restrictions.eq("lote5anos", true));
		criteria.setMaxResults(1);
		return LoteCnp.class.cast(criteria.uniqueResult());
	}
}