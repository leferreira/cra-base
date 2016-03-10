package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class AdministracaoDAO extends AbstractBaseDAO {

    public void removerRemessa(Instituicao instituicao, Arquivo arquivo) {
	Query query;
	try {
	    for (Remessa r : arquivo.getRemessas()) {
		query = createSQLQuery("DELETE FROM tb_anexo " + "WHERE titulo_id in ("
			+ "SELECT tit.id_titulo FROM tb_titulo AS tit "
			+ "WHERE tit.id_titulo in ( SELECT anexo.titulo_id FROM tb_anexo AS anexo ) "
			+ "AND tit.remessa_id= " + r.getId() + ")");
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_titulo" + " WHERE remessa_id=" + r.getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_remessa" + " WHERE id_remessa=" + r.getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_cabecalho" + " WHERE id_cabecalho=" + r.getCabecalho().getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_rodape" + " WHERE id_rodape=" + r.getRodape().getId());
		query.executeUpdate();
	    }
	    int idStatus = arquivo.getStatusArquivo().getId();
	    query = createSQLQuery("DELETE FROM tb_arquivo" + " WHERE id_arquivo=" + arquivo.getId());
	    query.executeUpdate();
	    query = createSQLQuery("DELETE FROM tb_status_arquivo" + " WHERE id_status_arquivo=" + idStatus);
	    query.executeUpdate();
	} catch (Exception ex) {
	    logger.error(ex.getMessage(), ex);
	}
    }

    public void removerConfirmacao(Instituicao instituicao, Arquivo arquivo) {
	Query query;
	try {
	    for (Remessa r : arquivo.getRemessas()) {
		query = createSQLQuery("DELETE FROM tb_confirmacao" + " WHERE remessa_id=" + r.getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_remessa" + " WHERE id_remessa=" + r.getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_cabecalho" + " WHERE id_cabecalho=" + r.getCabecalho().getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_rodape" + " WHERE id_rodape=" + r.getRodape().getId());
		query.executeUpdate();
	    }
	    int idStatus = arquivo.getStatusArquivo().getId();
	    query = createSQLQuery("DELETE FROM tb_arquivo" + " WHERE id_arquivo=" + arquivo.getId());
	    query.executeUpdate();
	    query = createSQLQuery("DELETE FROM tb_status_arquivo" + " WHERE id_status_arquivo=" + idStatus);
	    query.executeUpdate();
	} catch (Exception ex) {
	    logger.error(ex.getMessage(), ex);
	}
    }

    public void removerRetorno(Instituicao instituicao, Arquivo arquivo) {
	Query query;
	try {
	    for (Remessa r : arquivo.getRemessas()) {
		query = createSQLQuery("DELETE FROM tb_retorno" + " WHERE remessa_id=" + r.getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_remessa" + " WHERE id_remessa=" + r.getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_cabecalho" + " WHERE id_cabecalho=" + r.getCabecalho().getId());
		query.executeUpdate();
		query = createSQLQuery("DELETE FROM tb_rodape" + " WHERE id_rodape=" + r.getRodape().getId());
		query.executeUpdate();
	    }
	    int idStatus = arquivo.getStatusArquivo().getId();
	    query = createSQLQuery("DELETE FROM tb_arquivo" + " WHERE id_arquivo=" + arquivo.getId());
	    query.executeUpdate();
	    query = createSQLQuery("DELETE FROM tb_status_arquivo" + " WHERE id_status_arquivo=" + idStatus);
	    query.executeUpdate();
	} catch (Exception ex) {
	    logger.error(ex.getMessage(), ex);
	}
    }

    public void removerConfimacaoPelaCra(Arquivo arquivo) {
	Session session = getSession();
	session.clear();
	session.flush();
	Transaction transaction = session.beginTransaction();
	try {
	    for (Remessa r : arquivo.getRemessas()) {
		r.setArquivoGeradoProBanco(r.getArquivo());
		r.setSituacao(false);
		update(r);
	    }
	    transaction.commit();
	    Query query = createSQLQuery("DELETE FROM tb_arquivo" + " WHERE id_arquivo=" + arquivo.getId());
	    query.executeUpdate();
	    transaction = session.beginTransaction();
	    delete(arquivo.getStatusArquivo());
	    transaction.commit();
	} catch (Exception ex) {
	    transaction.rollback();
	    logger.error(ex.getMessage(), ex);
	}
    }

    public void removerRetornoPelaCra(Arquivo arquivo) {
	Session session = getSession();
	session.clear();
	session.flush();
	Transaction transaction = session.beginTransaction();
	try {
	    for (Remessa remessa : arquivo.getRemessas()) {
		remessa.setArquivoGeradoProBanco(remessa.getArquivo());
		remessa.setSituacao(false);
		remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.CONFIRMADO);
		update(remessa);
	    }
	    transaction.commit();
	    Query query = createSQLQuery("DELETE FROM tb_arquivo" + " WHERE id_arquivo=" + arquivo.getId());
	    query.executeUpdate();
	    transaction = session.beginTransaction();
	    delete(arquivo.getStatusArquivo());
	    transaction.commit();
	} catch (Exception ex) {
	    transaction.rollback();
	    logger.error(ex.getMessage(), ex);
	}
    }

    @SuppressWarnings("unchecked")
    public List<Arquivo> buscarArquivosRemover(Arquivo arquivo, Usuario usuario, ArrayList<TipoArquivoEnum> tiposArquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim, ArrayList<SituacaoArquivo> situacaoArquivos) {
	Criteria criteria = getCriteria(Arquivo.class);

	if (arquivo.getInstituicaoEnvio() != null) {
	    criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", arquivo.getInstituicaoEnvio()), Restrictions.eq("instituicaoRecebe", arquivo.getInstituicaoEnvio())));
	}

	if (!situacaoArquivos.isEmpty()) {
	    Disjunction disjunction = Restrictions.disjunction();
	    criteria.createAlias("statusArquivo", "statusArquivo");
	    Disjunction disj = Restrictions.disjunction();
	    for (SituacaoArquivo status : situacaoArquivos) {
		disjunction.add(Restrictions.eq("statusArquivo.situacaoArquivo", status));
	    }
	    criteria.add(disj);
	}

	if (arquivo.getNomeArquivo() != null && arquivo.getNomeArquivo() != StringUtils.EMPTY) {
	    criteria.add(Restrictions.ilike("nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
	}

	if (!tiposArquivo.isEmpty()) {
	    Disjunction disjunction = Restrictions.disjunction();
	    for (TipoArquivoEnum tipoArquivo : tiposArquivo) {
		disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipoArquivo));
	    }
	    criteria.add(disjunction);
	}

	if (dataInicio != null) {
	    criteria.add(Restrictions.between("dataEnvio", dataInicio, dataFim));
	}
	criteria.createAlias("tipoArquivo", "tipoArquivo");
	criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO));
	criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO));
	criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO));
	criteria.addOrder(Order.desc("dataEnvio"));
	return criteria.list();
    }

    public void executaArrumaDataTituloFiliado(List<TituloFiliado> titulos) {
    }
}
