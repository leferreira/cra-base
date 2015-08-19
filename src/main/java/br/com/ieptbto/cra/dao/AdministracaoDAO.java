package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;

@Repository
public class AdministracaoDAO extends AbstractBaseDAO {

	@SuppressWarnings("rawtypes")
	public void removerRemessa(Instituicao instituicao, Arquivo arquivo,
			List<Remessa> remessas) {
		Transaction transaction = getBeginTransation();
		try {
			for (Remessa r : remessas) {
				for (Titulo t : r.getTitulos()) {
					TituloRemessa titulo = TituloRemessa.class.cast(t);
					for (Historico h : titulo.getHistoricos()) {
						delete(h);
					}
					delete(titulo);
				}
				delete(r.getCabecalho());
				delete(r.getRodape());
				delete(r);
			}
			delete(arquivo.getStatusArquivo());
			delete(arquivo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void removerConfirmacao(Instituicao instituicao, Arquivo arquivo,
			List<Remessa> remessas) {
		Transaction transaction = getBeginTransation();
		try {
			for (Remessa r : remessas) {
				for (Titulo t : r.getTitulos()) {
					TituloRemessa titulo = TituloRemessa.class.cast(t);
					for (Historico h : titulo.getHistoricos()) {
						delete(h);
					}
					delete(titulo.getConfirmacao());
				}
				delete(r.getCabecalho());
				delete(r.getRodape());
				delete(r);
			}
			delete(arquivo.getStatusArquivo());
			delete(arquivo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void removerRetorno(Instituicao instituicao, Arquivo arquivo,
			List<Remessa> remessas) {
		Transaction transaction = getBeginTransation();
		try {
			for (Remessa r : remessas) {
				delete(r.getCabecalho());
				delete(r.getRodape());
				delete(r);
			}
			delete(arquivo.getStatusArquivo());
			delete(arquivo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
	}
}
