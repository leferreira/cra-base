package br.com.ieptbto.cra.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;

/**
 * @author Thasso Araújo
 *
 */

@Repository
public class InstrumentoProtestoDAO extends AbstractBaseDAO {

    private static final Logger logger = Logger.getLogger(InstrumentoProtestoDAO.class);

    public void salvarInstrumentoProtesto(InstrumentoProtesto instrumento) {
	Transaction transaction = getBeginTransation();

	try {
	    save(instrumento);

	    transaction.commit();
	} catch (Exception ex) {
	    transaction.rollback();
	    logger.info(ex.getMessage());
	}
    }

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

    public void alterarParaInstrumentosGerados(List<InstrumentoProtesto> instrumentosProtesto) {
	Transaction transaction = getBeginTransation();

	try {
	    for (InstrumentoProtesto instrumento : instrumentosProtesto) {
		instrumento.setGerado(true);
		update(instrumento);
	    }

	    transaction.commit();
	} catch (Exception ex) {
	    transaction.rollback();
	    logger.info(ex.getMessage());
	}
    }

    @SuppressWarnings("unchecked")
    public List<InstrumentoProtesto> buscarInstrumentosParaSlip() {
	Criteria criteria = getCriteria(InstrumentoProtesto.class);
	criteria.createAlias("tituloRetorno", "tituloRetorno");
	criteria.createAlias("tituloRetorno.titulo", "titulo");
	criteria.add(Restrictions.eq("gerado", false));
	criteria.addOrder(Order.asc("titulo.codigoPortador")).addOrder(Order.asc("titulo.pracaProtesto")).addOrder(Order.asc("tituloRetorno.numeroProtocoloCartorio"));
	return criteria.list();
    }

    public InstrumentoProtesto carregarTituloInstrumento(InstrumentoProtesto instrumento) {
	Criteria criteria = getCriteria(InstrumentoProtesto.class);
	criteria.add(Restrictions.eq("id", instrumento.getId()));
	criteria.createAlias("titulo", "titulo");
	return InstrumentoProtesto.class.cast(criteria.uniqueResult());
    }

    public String quantidadeEnvelopes() {
	Criteria criteria = getCriteria(EnvelopeSLIP.class);
	criteria.setProjection(Projections.max("id"));
	Integer max = Integer.class.cast(criteria.uniqueResult());

	if (max == null) {
	    max = 1;
	}
	return max.toString();
    }

    @SuppressWarnings("unchecked")
    public List<EnvelopeSLIP> buscarEnvelopesPendetesLiberacao() {
	Criteria criteria = getCriteria(EnvelopeSLIP.class);
	criteria.add(Restrictions.eq("liberado", false));
	return criteria.list();
    }

    public Retorno carregarRetorno(Retorno retorno) {
	Criteria criteria = getCriteria(Retorno.class);
	criteria.createAlias("cabecalho", "cabecalho");
	criteria.add(Restrictions.eq("numeroProtocoloCartorio", retorno.getNumeroProtocoloCartorio()));
	criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", retorno.getCabecalho().getCodigoMunicipio()));
	return Retorno.class.cast(criteria.uniqueResult());
    }

    public InstrumentoProtesto isTituloJaFoiGeradoInstrumento(Retorno tituloRetorno) {
	Criteria criteria = getCriteria(InstrumentoProtesto.class);
	criteria.add(Restrictions.eq("tituloRetorno", tituloRetorno));
	return InstrumentoProtesto.class.cast(criteria.uniqueResult());
    }
}
