package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "unchecked" })
@Repository
public class RemessaDAO extends AbstractBaseDAO {

	@Autowired
	private InstituicaoDAO instituicaoDAO;

	public List<Remessa> buscarRemessaAvancado(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
	        Usuario usuarioCorrente, ArrayList<TipoArquivoEnum> tiposArquivo, ArrayList<StatusRemessa> situacoes) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");

		if (!usuarioCorrente.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoDestino", usuarioCorrente.getInstituicao()),
			        Restrictions.eq("instituicaoOrigem", usuarioCorrente.getInstituicao())));
		}

		if (StringUtils.isNotBlank(arquivo.getNomeArquivo()))
			criteria.add(Restrictions.ilike("a.nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));

		if (!tiposArquivo.isEmpty()) {
			criteria.createAlias("a.tipoArquivo", "tipoArquivo");
			criteria.add(filtrarRemessaPorTipoArquivo(tiposArquivo));
		}

		if (!situacoes.isEmpty()) {
			criteria.add(filtrarSituacaoRemessa(situacoes));
		}

		if (arquivo.getInstituicaoEnvio() != null) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoOrigem", arquivo.getInstituicaoEnvio()),
			        Restrictions.eq("instituicaoDestino", arquivo.getInstituicaoEnvio())));
		}

		if (municipio != null) {
			Instituicao cartorioProtesto = instituicaoDAO.buscarCartorioPorMunicipio(municipio.getNomeMunicipio());
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoOrigem", cartorioProtesto),
			        Restrictions.eq("instituicaoDestino", cartorioProtesto)));
		}

		if (dataInicio != null){
			criteria.add(Restrictions.between("dataRecebimento", dataInicio, dataFim));
		}
		
		criteria.addOrder(Order.desc("a.dataEnvio"));
		return criteria.list();
	}

	private Disjunction filtrarRemessaPorTipoArquivo(ArrayList<TipoArquivoEnum> tiposArquivo) {
		Disjunction disjunction = Restrictions.disjunction();
		for (TipoArquivoEnum tipo : tiposArquivo) {
			disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipo));
		}
		return disjunction;
	}

	private Disjunction filtrarSituacaoRemessa(ArrayList<StatusRemessa> situacoesRemessa) {
		Disjunction disjunction = Restrictions.disjunction();
		for (StatusRemessa status : situacoesRemessa) {
			disjunction.add(Restrictions.eq("statusRemessa", status));
		}
		return disjunction;
	}

	public Remessa alterarSituacaoRemessa(Remessa remessa) {
		Transaction transaction = getBeginTransation();

		try {
			update(remessa);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esses dados na base.");
		}
		return remessa;
	}

	public int getNumeroSequencialConvenio(Instituicao convenio, Instituicao instituicaoDestino) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("instituicaoOrigem", convenio));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicaoDestino));
		return criteria.list().size();
	}

	@SuppressWarnings("rawtypes")
	public List<Remessa> confirmacoesPendentes(Instituicao instituicao) {
		List<Remessa> remessas = new ArrayList<Remessa>();
		String sql = "";
		
		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			sql = "select rem.instituicao_destino_id, t.remessa_id "
					+ "from TB_TITULO t "
					+ "INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa "
					+ "INNER JOIN tb_instituicao AS ins ON rem.instituicao_origem_id=ins.id_instituicao "
					+ "WHERE rem.id_remessa in (SELECT DISTINCT (tit.remessa_id) "
					+ "	from TB_TITULO tit "
					+ "	LEFT JOIN tb_confirmacao con ON tit.id_titulo = con.titulo_id "
					+ "	INNER JOIN tb_remessa rem ON tit.remessa_id=rem.id_remessa "
					+ "	where con.titulo_id IS NULL and tit.id_titulo > 37085) "
					+ "AND rem.arquivo_id>18088 "
					+ "GROUP BY rem.instituicao_destino_id,t.remessa_id "
					+ "ORDER BY remessa_id ASC;";
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			sql = "SELECT ins.nome_fantasia,t.remessa_id from TB_TITULO t "
					+ "INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa "
					+ "INNER JOIN tb_instituicao AS ins ON rem.instituicao_origem_id=ins.id_instituicao "
					+ "WHERE rem.id_remessa in (SELECT DISTINCT (tit.remessa_id) "
					+ "from TB_TITULO tit LEFT JOIN tb_confirmacao con ON tit.id_titulo = con.titulo_id "
					+ "INNER JOIN tb_remessa rem ON tit.remessa_id=rem.id_remessa "
					+ "where con.titulo_id IS NULL and tit.id_titulo > 37085 ) "
					+ "AND rem.instituicao_destino_id=" + instituicao.getId() + " "
					+ "AND rem.arquivo_id>18088 "
					+ "GROUP BY ins.nome_fantasia, t.remessa_id ORDER BY ins.nome_fantasia;";
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			sql = "select mun.nome_municipio,t.remessa_id "
					+ "from TB_TITULO t "
					+ "INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa "
					+ "INNER JOIN tb_instituicao AS ins ON rem.instituicao_destino_id=ins.id_instituicao "
					+ "INNER JOIN tb_instituicao AS org ON rem.instituicao_origem_id=org.id_instituicao "
					+ "INNER JOIN tb_municipio AS mun ON ins.municipio_id=mun.id_municipio "
					+ "WHERE rem.id_remessa in (SELECT DISTINCT (tit.remessa_id) "
					+ "from TB_TITULO tit "
					+ "LEFT JOIN tb_confirmacao con ON tit.id_titulo = con.titulo_id "
					+ "INNER JOIN tb_remessa rem ON tit.remessa_id=rem.id_remessa "
					+ "where con.titulo_id IS NULL "
					+ "and tit.id_titulo > 37085) "
					+ "AND org.tipo_instituicao_id<>4 "
					+ "AND rem.instituicao_origem_id=" + instituicao.getId() + " "
					+ "OR rem.status_remessa LIKE 'AGUARDANDO' "
					+ "AND org.tipo_instituicao_id<>4 "
					+ "AND rem.instituicao_origem_id=" + instituicao.getId() + " "
					+ "GROUP BY mun.nome_municipio,t.remessa_id "
					+ "ORDER BY remessa_id ASC";
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CONVENIO)) {
			sql = "select mun.nome_municipio,t.remessa_id "
					+ "from TB_TITULO t "
					+ "INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa "
					+ "INNER JOIN tb_instituicao AS ins ON rem.instituicao_destino_id=ins.id_instituicao "
					+ "INNER JOIN tb_instituicao AS org ON rem.instituicao_origem_id=org.id_instituicao "
					+ "INNER JOIN tb_municipio AS mun ON ins.municipio_id=mun.id_municipio "
					+ "WHERE rem.id_remessa in (SELECT DISTINCT (tit.remessa_id) "
					+ "from TB_TITULO tit "
					+ "LEFT JOIN tb_confirmacao con ON tit.id_titulo = con.titulo_id "
					+ "INNER JOIN tb_remessa rem ON tit.remessa_id=rem.id_remessa "
					+ "where con.titulo_id IS NULL "
					+ "and tit.id_titulo > 37085) "
					+ "AND rem.instituicao_origem_id=" + instituicao.getId() + " "
					+ "OR rem.status_remessa LIKE 'AGUARDANDO' "
					+ "AND rem.instituicao_origem_id=" + instituicao.getId() + " "
					+ "GROUP BY mun.nome_municipio,t.remessa_id "
					+ "ORDER BY remessa_id ASC";
		}
		
		Query query = getSession().createSQLQuery(sql);
		Iterator iterator = query.list().iterator();
		while (iterator.hasNext()) {
			Object[] posicao = (Object[]) iterator.next();
			Integer id = Integer.class.cast(posicao[1]);
			Criteria criteria = getCriteria(Remessa.class);
			criteria.add(Restrictions.eq("id", id));
			remessas.add(Remessa.class.cast(criteria.uniqueResult()));
		}
		return remessas;
	}

	public List<Anexo> verificarAnexosRemessa(Remessa remessa) {
		Criteria criteria = getCriteria(Anexo.class);
		criteria.createAlias("titulo", "titulo");
		criteria.add(Restrictions.eq("titulo.remessa", remessa));
		return criteria.list();
	}
 
	public Remessa baixarArquivoCartorioRemessa(Remessa remessa) {
		Remessa remessaDownload = buscarPorPK(remessa);
		
		Criteria criteriaTitulo = getCriteria(TituloRemessa.class);
		criteriaTitulo.add(Restrictions.eq("remessa", remessaDownload));
		remessaDownload.setTitulos(criteriaTitulo.list());
		return remessaDownload;
	}

	public Remessa baixarArquivoCartorioConfirmacao(Remessa remessa) {
		Remessa remessaDownload = buscarPorPK(remessa);
		
		Criteria criteriaTitulo = getCriteria(Confirmacao.class);
		criteriaTitulo.add(Restrictions.eq("remessa", remessaDownload));
		remessaDownload.setTitulos(criteriaTitulo.list());
		return remessaDownload;
	}

	public Remessa baixarArquivoCartorioRetorno(Remessa remessa) {
		Remessa remessaDownload = buscarPorPK(remessa);
		
		Criteria criteriaTitulo = getCriteria(Retorno.class);
		criteriaTitulo.add(Restrictions.eq("remessa", remessaDownload)); 
		remessaDownload.setTitulos(criteriaTitulo.list());
		return remessaDownload;
	}
	
	@SuppressWarnings("rawtypes")
	public Remessa baixarArquivoCartorioRemessa(Instituicao instituicao, String nomeArquivo) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicao));
		Remessa remessa = Remessa.class.cast(criteria.uniqueResult());
		remessa.setTitulos(new ArrayList<Titulo>());
		
		Criteria criteriaTitulo = getCriteria(TituloRemessa.class);
		criteriaTitulo.add(Restrictions.eq("remessa", remessa)); 
		remessa.getTitulos().addAll(criteriaTitulo.list());
		return remessa;
	}

	@SuppressWarnings("rawtypes")
	public Remessa baixarArquivoCartorioConfirmacao(Instituicao instituicao, String nomeArquivo) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		criteria.add(Restrictions.eq("instituicaoOrigem", instituicao));
		Remessa remessa = Remessa.class.cast(criteria.uniqueResult());
		remessa.setTitulos(new ArrayList<Titulo>());
		
		Criteria criteriaTitulo = getCriteria(Confirmacao.class);
		criteriaTitulo.add(Restrictions.eq("remessa", remessa)); 
		remessa.getTitulos().addAll(criteriaTitulo.list());
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Remessa baixarArquivoCartorioRetorno(Instituicao instituicao, String nomeArquivo) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		criteria.add(Restrictions.eq("instituicaoOrigem", instituicao));
		Remessa remessa = Remessa.class.cast(criteria.uniqueResult());
		remessa.setTitulos(new ArrayList<Titulo>());
		
		Criteria criteriaTitulo = getCriteria(Retorno.class);
		criteriaTitulo.add(Restrictions.eq("remessa", remessa)); 
		remessa.getTitulos().addAll(criteriaTitulo.list());
		return remessa;
	}
}
