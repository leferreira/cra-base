package br.com.ieptbto.cra.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;

@Repository
public class AdministracaoDAO extends AbstractBaseDAO {

	@Autowired
	HistoricoOcorrenciaDAO historicoDAO;
	@Autowired
	TituloDAO tituloDAO;
	
	public void removerRemessa(Instituicao instituicao, Arquivo arquivo) {
		Query query;
		try {
			for (Remessa r : arquivo.getRemessas()) {
				query = createSQLQuery("DELETE FROM tb_historico" 
						+ " WHERE remessa_id=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_titulo"
						+ " WHERE remessa_id=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_remessa"
						+ " WHERE id_remessa=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_cabecalho"
						+ " WHERE id_cabecalho=" + r.getCabecalho().getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_rodape"
						+ " WHERE id_rodape=" + r.getRodape().getId());
				query.executeUpdate();
			}
			int idStatus = arquivo.getStatusArquivo().getId();
			query = createSQLQuery("DELETE FROM tb_arquivo" 
				+ " WHERE id_arquivo=" + arquivo.getId());
			query.executeUpdate();
			query = createSQLQuery("DELETE FROM tb_status_arquivo" 
				+ " WHERE id_status_arquivo=" + idStatus);
			query.executeUpdate();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void removerConfirmacao(Instituicao instituicao, Arquivo arquivo) {
		Query query;
		try {
			for (Remessa r : arquivo.getRemessas()) {
				query = createSQLQuery("DELETE FROM tb_historico" 
						+ " WHERE remessa_id=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_confirmacao"
						+ " WHERE remessa_id=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_remessa"
						+ " WHERE id_remessa=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_cabecalho"
						+ " WHERE id_cabecalho=" + r.getCabecalho().getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_rodape"
						+ " WHERE id_rodape=" + r.getRodape().getId());
				query.executeUpdate();
			}
			int idStatus = arquivo.getStatusArquivo().getId();
			query = createSQLQuery("DELETE FROM tb_arquivo" 
				+ " WHERE id_arquivo=" + arquivo.getId());
			query.executeUpdate();
			query = createSQLQuery("DELETE FROM tb_status_arquivo" 
				+ " WHERE id_status_arquivo=" + idStatus);
			query.executeUpdate();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void removerRetorno(Instituicao instituicao, Arquivo arquivo) {
		Query query;
		try {
			for (Remessa r : arquivo.getRemessas()) {
				query = createSQLQuery("DELETE FROM tb_historico" 
						+ " WHERE remessa_id=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_retorno"
						+ " WHERE remessa_id=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_remessa"
						+ " WHERE id_remessa=" + r.getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_cabecalho"
						+ " WHERE id_cabecalho=" + r.getCabecalho().getId());
				query.executeUpdate();
				query = createSQLQuery("DELETE FROM tb_rodape"
						+ " WHERE id_rodape=" + r.getRodape().getId());
				query.executeUpdate();
			}
			int idStatus = arquivo.getStatusArquivo().getId();
			query = createSQLQuery("DELETE FROM tb_arquivo" 
				+ " WHERE id_arquivo=" + arquivo.getId());
			query.executeUpdate();
			query = createSQLQuery("DELETE FROM tb_status_arquivo" 
				+ " WHERE id_status_arquivo=" + idStatus);
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
			Query query = createSQLQuery("DELETE FROM tb_arquivo" 
			+ " WHERE id_arquivo=" + arquivo.getId());
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
			for (Remessa r : arquivo.getRemessas()) {
				r.setArquivoGeradoProBanco(r.getArquivo());
				r.setSituacao(false);
				r.setSituacaoBatimento(true);
				update(r);				
			}
			transaction.commit();
			Query query = createSQLQuery("DELETE FROM tb_arquivo" 
			+ " WHERE id_arquivo=" + arquivo.getId());
			query.executeUpdate();
			transaction = session.beginTransaction();
			delete(arquivo.getStatusArquivo());
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
	}
}
