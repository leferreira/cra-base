package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "unchecked" })
@Repository
public class RemessaDAO extends AbstractBaseDAO {

	public List<Remessa> buscarRemessaAvancado(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoSistema tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio, List<TipoArquivoFebraban> tiposArquivo, List<StatusDownload> situacoesArquivos) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");

		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoDestino", usuario.getInstituicao()),
					Restrictions.eq("instituicaoOrigem", usuario.getInstituicao())));
		}
		if (StringUtils.isNotBlank(nomeArquivo)) {
			criteria.add(Restrictions.ilike("a.nomeArquivo", nomeArquivo, MatchMode.ANYWHERE));
		}
		if (!tiposArquivo.isEmpty()) {
			criteria.createAlias("a.tipoArquivo", "tipo");
			Disjunction disjunction = Restrictions.disjunction();
			for (TipoArquivoFebraban tipo : tiposArquivo) {
				disjunction.add(Restrictions.eq("tipo.tipoArquivo", tipo));
			}
			criteria.add(disjunction);
		}
		if (!situacoesArquivos.isEmpty()) {
			Disjunction disj = Restrictions.disjunction();
			for (StatusDownload situacao : situacoesArquivos) {
				disj.add(Restrictions.eq("statusDownload", situacao));
			}
			criteria.add(disj);
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			criteria.createAlias("instituicaoOrigem", "instituicaoOrigem");
			criteria.createAlias("instituicaoOrigem.tipoInstituicao", "tipoInstituicao");
			criteria.createAlias("instituicaoDestino", "instituicaoDestino");
			criteria.createAlias("instituicaoDestino.tipoInstituicao", "tipoInstituicaoDestino");
			criteria.add(Restrictions.or(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao), Restrictions.eq("tipoInstituicaoDestino.tipoInstituicao", tipoInstituicao)));
		}
		if (bancoConvenio != null) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoDestino", bancoConvenio), Restrictions.eq("instituicaoOrigem", bancoConvenio)));
		}
		if (cartorio != null) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoDestino", cartorio), Restrictions.eq("instituicaoOrigem", cartorio)));
		}
		if (dataInicio != null) {
			criteria.add(Restrictions.between("a.dataEnvio", dataInicio, dataFim));
		}
		criteria.addOrder(Order.desc("a.dataEnvio"));
		return criteria.list();
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

	public List<Remessa> confirmacoesPendentes(Instituicao instituicao) {
		List<Remessa> remessas = new ArrayList<Remessa>();

		Hibernate.initialize(instituicao.getTipoInstituicao());
		StringBuffer sql = new StringBuffer();
		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)) {
			sql.append(
					"SELECT t.remessa_id from TB_TITULO t INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa LEFT OUTER JOIN tb_confirmacao AS conf ON t.id_titulo=conf.titulo_id WHERE conf.id_confirmacao is null AND rem.arquivo_id>18088 GROUP BY t.remessa_id ORDER BY t.remessa_id ASC");
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CARTORIO)) {
			sql.append(
					"SELECT t.remessa_id from TB_TITULO t INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa LEFT OUTER JOIN tb_confirmacao AS conf ON t.id_titulo=conf.titulo_id WHERE conf.id_confirmacao is null AND rem.instituicao_destino_id= "
							+ instituicao.getId() + " AND rem.arquivo_id>18088 GROUP BY t.remessa_id ORDER BY t.remessa_id ASC");
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CONVENIO)) {
			sql.append(
					"SELECT t.remessa_id from TB_TITULO t INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa LEFT OUTER JOIN tb_confirmacao AS conf ON t.id_titulo=conf.titulo_id WHERE conf.id_confirmacao is null AND rem.instituicao_origem_id= "
							+ instituicao.getId() + " AND rem.arquivo_id>18088 GROUP BY t.remessa_id ORDER BY t.remessa_id ASC");
		}

		Query query = getSession().createSQLQuery(sql.toString());
		List<Integer> remessasId = query.list();
		for (Integer id : remessasId) {
			Criteria criteria = getCriteria(Remessa.class);
			criteria.add(Restrictions.eq("id", id));
			remessas.add(Remessa.class.cast(criteria.uniqueResult()));
		}
		return remessas;
	}

	public Anexo verificarAnexosRemessa(Remessa remessa) {
		Criteria criteria = getCriteria(Anexo.class);
		criteria.createAlias("titulo", "titulo");
		criteria.add(Restrictions.eq("titulo.remessa", remessa));
		criteria.setMaxResults(1);
		return Anexo.class.cast(criteria.uniqueResult());
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
		criteria.add(Restrictions.ilike("arquivo.nomeArquivo", nomeArquivo, MatchMode.EXACT));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicao));
		Remessa remessa = Remessa.class.cast(criteria.uniqueResult());
		if (remessa == null) {
			return null;
		}
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
		if (remessa == null) {
			return null;
		}
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
		if (remessa == null) {
			return null;
		}
		remessa.setTitulos(new ArrayList<Titulo>());

		Criteria criteriaTitulo = getCriteria(Retorno.class);
		criteriaTitulo.add(Restrictions.eq("remessa", remessa));
		remessa.getTitulos().addAll(criteriaTitulo.list());
		return remessa;
	}

	public List<Remessa> buscarRemessasPorArquivo(Arquivo arquivo) {
		arquivo = buscarPorPK(arquivo);
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("arquivo", arquivo));

		return criteria.list();
	}
}
