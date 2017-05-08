package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class InstrumentoProtestoDAO extends AbstractBaseDAO {

    /**
     * Salvar entrada de instrumento de protesto na CRA
     *
     * @param instrumento
     * @return
     */
    public InstrumentoProtesto salvarInstrumentoProtesto(InstrumentoProtesto instrumento) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

		try {
			instrumento = save(instrumento);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.info(ex.getMessage(), ex);
			throw new InfraException("Não foi possível salvar o registro do instrumento de protesto! Favor entrar em contato com a CRA...");
		}
		return instrumento;
	}

    /**
     * Atualizar os dados do instrumento de protesto
     *
     * @param instrumentosProtesto
     */
    public InstrumentoProtesto atualizarInstrumentoProtesto(InstrumentoProtesto instrumentosProtesto) {
		Transaction transaction = getBeginTransation();

		try {
			update(instrumentosProtesto);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.info(ex.getMessage());
		}
		return instrumentosProtesto;
	}

    /**
     * Remove o registro da entrada do instrumento de protesto na CRA
     *
     * @param instrumentoProtesto
     */
    public void removerInstrumento(InstrumentoProtesto instrumentoProtesto) {

        try {
            Query query = createSQLQuery("DELETE FROM tb_instrumento_protesto WHERE " +
                    "id_instrumento_protesto=" + instrumentoProtesto.getId());
            query.executeUpdate();

        } catch (Exception ex) {
            logger.info(ex.getMessage());
            throw new InfraException("Não foi possível remover o registro do instrumento de protesto do banco de dados!");
        }
    }

    /**
     * Salvar os envelopes gerados com as slips
     *
     * @param envelopes
     */
    public void salvarEnvelopesEtiquetas(List<EnvelopeSLIP> envelopes) {
        EnvelopeSLIP envelopeSalvo = new EnvelopeSLIP();
        Transaction transaction = getBeginTransation();

        try {
            for (EnvelopeSLIP envelope : envelopes) {
                envelope.setDataGeracao(new LocalDate());
                envelope.setLiberado(false);
                envelopeSalvo = save(envelope);

                for (EtiquetaSLIP etiqueta : envelope.getEtiquetas()) {
                    etiqueta.setEnvelope(envelopeSalvo);
                    save(etiqueta);
                }
            }

            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            logger.info(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
	public List<InstrumentoProtesto> buscarInstrumentosParaSlip() {
		Criteria criteria = getCriteria(InstrumentoProtesto.class);
		criteria.createAlias("tituloRetorno", "tituloRetorno");
		criteria.createAlias("tituloRetorno.titulo", "titulo");
		criteria.add(Restrictions.eq("gerado", false));
		criteria.addOrder(Order.asc("titulo.codigoPortador")).addOrder(Order.asc("titulo.pracaProtesto"))
				.addOrder(Order.asc("tituloRetorno.numeroProtocoloCartorio"));
		return criteria.list();
	}

	public InstrumentoProtesto buscarInstrumentoProtesto(Retorno tituloRetorno) {
		Criteria criteria = getCriteria(InstrumentoProtesto.class);
		criteria.add(Restrictions.eq("tituloRetorno", tituloRetorno));
		return InstrumentoProtesto.class.cast(criteria.uniqueResult());
	}

    /**
     * Gerador de sequencia diario de código de envelopes seguindo o padrão de
     * etiquetas
     *
     * @return
     */
    public Long buscarSequencialDiarioEnvelopes() {
		Criteria criteria = getCriteria(EnvelopeSLIP.class);
		criteria.add(Restrictions.eq("dataGeracao", new LocalDate()));
		criteria.setProjection(Projections.count("id"));
		Long total = Long.class.cast(criteria.uniqueResult());
		if (total == null) {
			total = new Long(0);
		}
		return total;
	}

    /**
     * Verifica se contém instrumentos que tiveram Slips geradas e não forma confirmadas pelo usuário
     *
     * @return
     */
    public boolean verificarEtiquetasGeradasNaoConfimadas() {
		Criteria criteria = getCriteria(EtiquetaSLIP.class);
		criteria.createAlias("instrumentoProtesto", "instrumentoProtesto", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("instrumentoProtesto.gerado", false));
		criteria.setMaxResults(1);
		EtiquetaSLIP etiqueta = EtiquetaSLIP.class.cast(criteria.uniqueResult());
		if (etiqueta != null) {
			return true;
		}
		return false;
	}
}
