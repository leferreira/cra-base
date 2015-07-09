package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class TipoInstituicaoDAO extends AbstractBaseDAO {

	@SuppressWarnings("unchecked")
	public TipoInstituicao alterar(TipoInstituicao tipoInstituicao, List<PermissaoEnvio> permissoes) {
		Transaction transaction = getBeginTransation();
		
		try {
			Criteria criteria = getCriteria(PermissaoEnvio.class);
			criteria.add(Restrictions.eq("tipoInstituicao", tipoInstituicao));
			List<PermissaoEnvio> lista = criteria.list();
			
			for (PermissaoEnvio permissao: lista){
				delete(permissao);
			}
			for (PermissaoEnvio permissao : permissoes){
				save(permissao);
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			throw new InfraException("Não foi possível alterar as permissões para o tipo selecionado !");
		}
		return tipoInstituicao;
	}

	public void inserirTipoInstituicaoInicial(String tipo) {
		Transaction transaction = getBeginTransation();
		try {
			TipoInstituicao tipoInstituicao = new TipoInstituicao();
			tipoInstituicao.setId(Integer.parseInt(TipoInstituicaoCRA.get(tipo).getConstante()));
			tipoInstituicao.setTipoInstituicao(TipoInstituicaoCRA.get(tipo));
			save(tipoInstituicao);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<TipoInstituicao> buscarListaTipoInstituicao() {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.add(Restrictions.ne("tipoInstituicao", TipoInstituicaoCRA.CARTORIO));
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TipoInstituicao> listarTodos() {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		// criteria.createAlias("arquivosEnvioPermitido",
		// "arquivosEnvioPermitido");
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}

	public TipoInstituicao buscarTipoInstituicao(TipoInstituicaoCRA tipoInstituicao) {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.add(Restrictions.eq("tipoInstituicao", tipoInstituicao));
		return TipoInstituicao.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<PermissaoEnvio> permissoesPorTipoInstituicao(TipoInstituicao tipo) {
		Criteria criteria = getCriteria(PermissaoEnvio.class);
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.createAlias("tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoInstituicao", tipo));
		return criteria.list();
	}
}
