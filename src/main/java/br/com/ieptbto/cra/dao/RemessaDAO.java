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

		criteria.addOrder(Order.desc("dataRecebimento"));
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

	public List<Remessa> confirmacoesPendentes(Instituicao instituicao) {
		List<Remessa> remessas = new ArrayList<Remessa>();
		String q = "select tit.remessa_id from TB_TITULO tit " + "LEFT JOIN tb_confirmacao con ON tit.id_titulo = con.titulo_id "
		        + " INNER JOIN tb_remessa rem ON tit.remessa_id=rem.id_remessa "
		        + " where con.titulo_id IS NULL and rem.instituicao_destino_id = " + instituicao.getId()
		        + " and tit.id_titulo > 37085 group by tit.remessa_id ";

		Query query = getSession().createSQLQuery(q);
		query.getQueryString();
		List<Integer> result = query.list();
		if (result.isEmpty()) {
			return new ArrayList<Remessa>();
		}

		for (Integer id : result) {
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

	public List<DesistenciaProtesto> buscarRemessaDesistenciaProtesto(Arquivo arquivo, LocalDate dataInicio, LocalDate dataFim,
	        ArrayList<TipoArquivoEnum> tiposArquivo, Instituicao portador, Usuario usuario) {

		Criteria criteria = getCriteria(DesistenciaProtesto.class);
		criteria.createAlias("remessaDesistenciaProtesto", "remessa");
		criteria.createAlias("remessa.arquivo", "arquivo");
		criteria.createAlias("cabecalhoCartorio", "cabecalho");
		if (arquivo != null && arquivo.getNomeArquivo() != null) {
			criteria.add(Restrictions.ilike("arquivo.nomeArquivo", arquivo.getNomeArquivo()));
		} else if (tiposArquivo != null && !tiposArquivo.isEmpty()) {
			criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
			for (TipoArquivoEnum tipoArquivoEnum : tiposArquivo) {
				criteria.add((Restrictions.eq("tipoArquivo.tipoArquivo", tipoArquivoEnum)));
			}
		}

		if (dataInicio != null) {
			if (dataFim == null) {
				dataFim = new LocalDate(9999 - 01 - 01);
			}
			criteria.add((Restrictions.between("arquivo.dataEnvio", dataInicio, dataFim)));
		}

		if (dataInicio == null && dataFim != null) {
			dataInicio = new LocalDate(1901 - 01 - 01);
			criteria.add((Restrictions.between("arquivo.dataEnvio", dataInicio, dataFim)));
		}

		if (portador != null) {
			criteria.add(Restrictions.eq("cabecalho.numeroCodigoPortador", portador));
		}

		if (TipoInstituicaoCRA.CARTORIO.equals(usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao())) {
			Hibernate.initialize(usuario.getInstituicao());
			Hibernate.initialize(usuario.getInstituicao().getMunicipio());
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", usuario.getInstituicao().getMunicipio().getCodigoIBGE()));
		} else if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao())) {
			criteria.createAlias("arquivo", "arquivo");
			criteria.add(Restrictions.eq("arquivo.instituicaoEnvio", usuario.getInstituicao()));
		}

		return criteria.list();
	}

	public List<DesistenciaProtesto> buscarRemessaDesistenciaProtesto(Instituicao instituicao) {
		Criteria criteria = getCriteria(DesistenciaProtesto.class);
		criteria.createAlias("cabecalhoCartorio", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", instituicao.getMunicipio().getCodigoIBGE()));
		criteria.add(Restrictions.eq("download", false));

		return criteria.list();
	}
}
