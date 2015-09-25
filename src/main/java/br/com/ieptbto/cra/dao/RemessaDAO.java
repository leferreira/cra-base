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
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Titulo;
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
	TituloDAO tituloDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;

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

		if (dataInicio != null)
			criteria.add(Restrictions.between("dataRecebimento", dataInicio, dataFim));

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

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Remessa buscarArquivosPorNome(String nome) {
		List<Titulo> titulos = new ArrayList<Titulo>();
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nome));
		criteria.setMaxResults(1);
		Remessa remessa = Remessa.class.cast(criteria.uniqueResult());

		if (remessa == null) {
			return null;
		}

		Criteria criteriaTitulo = getCriteria(Titulo.class);
		criteriaTitulo.createAlias("remessa", "remessa");
		criteriaTitulo.add(Restrictions.eq("remessa", remessa));
		titulos = criteriaTitulo.list();

		remessa.setTitulos(titulos);

		return remessa;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Remessa buscarRemessaParaCartorio(Instituicao cartorio, String nomeArquivo) {
		List<Titulo> titulos = new ArrayList<Titulo>();
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		criteria.add(Restrictions.eq("instituicaoDestino", cartorio));
		criteria.setMaxResults(1);
		Remessa remessa = Remessa.class.cast(criteria.uniqueResult());

		if (remessa == null) {
			return null;
		}

		Criteria criteriaTitulo = getCriteria(Titulo.class);
		criteriaTitulo.createAlias("remessa", "remessa");
		criteriaTitulo.add(Restrictions.eq("remessa", remessa));
		titulos = criteriaTitulo.list();

		remessa.setTitulos(titulos);

		return remessa;
	}

	public Remessa buscarPorPK(Remessa entidade) {
		Remessa remessa = super.buscarPorPK(entidade);
		Criteria criteriaTitulo = getCriteria(Titulo.class);
		criteriaTitulo.createAlias("remessa", "remessa");
		criteriaTitulo.add(Restrictions.eq("remessa", remessa));
		remessa.setTitulos(criteriaTitulo.list());
		return remessa;
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

	public Remessa isRemessaEnviada(String nomeArquivo, Instituicao instituicaoOrigem) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		criteria.add(Restrictions.eq("instituicaoOrigem", instituicaoOrigem));
		criteria.setMaxResults(1);
		return Remessa.class.cast(criteria.uniqueResult());
	}

	public int getNumeroSequencialConvenio(Instituicao convenio, Instituicao instituicaoDestino) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("instituicaoOrigem", convenio));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicaoDestino));
		return criteria.list().size();
	}

	public int verificarSequencialArquivo(Remessa remessa) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("cabecalho", "cabecalho");
		criteria.add(Restrictions.like("arquivo.nomeArquivo", remessa.getArquivo().getNomeArquivo().substring(0, 11), MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("cabecalho.dataMovimento", remessa.getCabecalho().getDataMovimento()));
		criteria.add(Restrictions.eq("instituicaoOrigem", remessa.getInstituicaoOrigem()));
		return criteria.list().size() + 1;
	}

	public boolean verificarDuplicidadeRetorno(Remessa remessa) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("cabecalho", "cabecalho");
		criteria.add(Restrictions.like("arquivo.nomeArquivo", remessa.getArquivo().getNomeArquivo().substring(0, 3)));
		criteria.add(Restrictions.eq("instituicaoOrigem", remessa.getInstituicaoOrigem()));
		criteria.add(Restrictions.eq("cabecalho.numeroSequencialRemessa", remessa.getCabecalho().getNumeroSequencialRemessa()));
		Remessa buscada = Remessa.class.cast(criteria.uniqueResult());
		if (buscada == null) {
			return false;
		}
		return true;
	}

	public List<Remessa> buscarRemessasPorArquivo(Instituicao instituicao, Arquivo arquivo) {
		Criteria criteria = getCriteria(Remessa.class);
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoDestino", instituicao),
			        Restrictions.eq("instituicaoOrigem", instituicao)));
		}
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.ilike("arquivo.nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
		criteria.addOrder(Order.asc("dataRecebimento"));
		return criteria.list();
	}

	@SuppressWarnings("rawtypes")
	public List<Remessa> confirmacoesPendentes(Instituicao instituicao) {
		List<Remessa> remessas = new ArrayList<Remessa>();
		String q = "";
		
		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			q = "select mun.nome_municipio,t.remessa_id "
					+ "from TB_TITULO t "
					+ "INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa "
					+ "INNER JOIN tb_instituicao AS ins ON rem.instituicao_destino_id=ins.id_instituicao "
					+ "INNER JOIN tb_instituicao AS org ON rem.instituicao_origem_id=org.id_instituicao "
					+ "INNER JOIN tb_municipio AS mun ON ins.municipio_id=mun.id_municipio "
					+ "WHERE rem.id_remessa in (SELECT DISTINCT (tit.remessa_id) "
					+ "from TB_TITULO tit "
					+ "LEFT JOIN tb_confirmacao con ON tit.id_titulo = con.titulo_id "
					+ "INNER JOIN tb_remessa rem ON tit.remessa_id=rem.id_remessa "
					+ "where con.titulo_id IS NULL and tit.id_titulo > 37085) "
					+ "AND org.tipo_instituicao_id<>4 "
					+ "OR rem.status_remessa LIKE 'AGUARDANDO' "
					+ "AND org.tipo_instituicao_id<>4 "
					+ "GROUP BY mun.nome_municipio,t.remessa_id ORDER BY mun.nome_municipio";
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			q = "select ins.nome_fantasia,t.remessa_id from TB_TITULO t "
					+ "INNER JOIN tb_remessa rem ON t.remessa_id=rem.id_remessa "
					+ "INNER JOIN tb_instituicao AS ins ON rem.instituicao_origem_id=ins.id_instituicao "
					+ "WHERE rem.id_remessa in (SELECT DISTINCT (tit.remessa_id) "
					+ "from TB_TITULO tit LEFT JOIN tb_confirmacao con ON tit.id_titulo = con.titulo_id "
					+ "INNER JOIN tb_remessa rem ON tit.remessa_id=rem.id_remessa "
					+ "where con.titulo_id IS NULL and tit.id_titulo > 37085 )"
					+ "AND rem.instituicao_destino_id=" + instituicao.getId() + " "
					+ "AND ins.tipo_instituicao_id<>4 "
					+ "OR rem.status_remessa LIKE 'AGUARDANDO' "
					+ "AND rem.instituicao_destino_id=" + instituicao.getId() + " "
					+ "AND ins.tipo_instituicao_id<>4 "
					+ "GROUP BY ins.nome_fantasia, t.remessa_id ORDER BY ins.nome_fantasia";
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			q = "select mun.nome_municipio,t.remessa_id "
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
					+ "ORDER BY mun.nome_municipio";
		}
		
		Query query = getSession().createSQLQuery(q);
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

	public Remessa buscarRemessaDaConfirmacao(Remessa confirmacao) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.createAlias("cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.dataMovimento", confirmacao.getCabecalho().getDataMovimento()));
		criteria.add(Restrictions.eq("cabecalho.numeroSequencialRemessa", confirmacao.getCabecalho().getNumeroSequencialRemessa()));
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", confirmacao.getCabecalho().getCodigoMunicipio()));
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.REMESSA));
		return Remessa.class.cast(criteria.uniqueResult());
	}

	public List<DesistenciaProtesto> buscarRemessaDesistenciaProtesto(Arquivo arquivo, Instituicao portador, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
	        ArrayList<TipoArquivoEnum> tiposArquivo, Usuario usuario) {

		Criteria criteria = getCriteria(DesistenciaProtesto.class);
		criteria.createAlias("remessaDesistenciaProtesto", "remessa");
		criteria.createAlias("remessa.arquivo", "arquivo");

		if (StringUtils.isNotBlank(arquivo.getNomeArquivo())) {
			criteria.add(Restrictions.ilike("arquivo.nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
		} 
		
		if (tiposArquivo != null && !tiposArquivo.isEmpty()) {
			criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
			criteria.add(filtrarRemessaPorTipoArquivo(tiposArquivo));
		}
		
		if (dataInicio != null && dataFim != null) {
			criteria.add((Restrictions.between("arquivo.dataEnvio", dataInicio, dataFim)));
		}

		if (portador != null) {
			criteria.createAlias("remessa.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", portador.getCodigoCompensacao()));
		}
		
		if (municipio != null) {
			criteria.createAlias("cabecalhoCartorio", "cabecalho");
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", municipio.getCodigoIBGE()));
		}

		if (usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			criteria.createAlias("cabecalhoCartorio", "cabecalho");
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", usuario.getInstituicao().getMunicipio().getCodigoIBGE()));
		} else if (usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			criteria.createAlias("remessa.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", usuario.getInstituicao().getCodigoCompensacao()));
		}
		return criteria.list();
	}

	public List<DesistenciaProtesto> buscarRemessaDesistenciaProtestoPendenteDownload(Instituicao instituicao) {
		Criteria criteria = getCriteria(DesistenciaProtesto.class);
		criteria.createAlias("cabecalhoCartorio", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", instituicao.getMunicipio().getCodigoIBGE()));
		criteria.add(Restrictions.eq("download", false));

		return criteria.list();
	}

	public DesistenciaProtesto alterarSituacaoDesistenciaProtesto(DesistenciaProtesto desistenciaProtesto, boolean download) {
		Transaction transaction = getBeginTransation();

		try {
			desistenciaProtesto.setDownload(download);
			update(desistenciaProtesto);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível atualizar o status da DP.");
		}
		return desistenciaProtesto;

	}

	public DesistenciaProtesto buscarRemessaDesistenciaProtesto(DesistenciaProtesto entidade) {
		return super.buscarPorPK(entidade);
	}
}
