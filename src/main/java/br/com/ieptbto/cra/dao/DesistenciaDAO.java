package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class DesistenciaDAO extends AbstractBaseDAO {

	@SuppressWarnings("unchecked")
	public List<PedidoDesistencia> buscarPedidosDesistenciaProtestoPorTitulo(TituloRemessa tituloRemessa) {
		Criteria criteria = getCriteria(PedidoDesistencia.class);
		criteria.add(Restrictions.eq("titulo", tituloRemessa));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<PedidoDesistencia> buscarPedidosDesistenciaProtesto(DesistenciaProtesto desistenciaProtesto) {
		Criteria criteria = getCriteria(PedidoDesistencia.class);
		criteria.createAlias("titulo", "titulo");
		criteria.add(Restrictions.eq("desistenciaProtesto", desistenciaProtesto));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<DesistenciaProtesto> buscarDesistenciaProtesto(Arquivo arquivo, Instituicao portador,
			Municipio municipio, LocalDate dataInicio, LocalDate dataFim, ArrayList<TipoArquivoEnum> tiposArquivo,
			Usuario usuario) {
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

	private Disjunction filtrarRemessaPorTipoArquivo(ArrayList<TipoArquivoEnum> tiposArquivo) {
		Disjunction disjunction = Restrictions.disjunction();
		for (TipoArquivoEnum tipo : tiposArquivo) {
			disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipo));
		}
		return disjunction;
	}

	@SuppressWarnings({ "unchecked" })
	public List<DesistenciaProtesto> buscarRemessaDesistenciaProtestoPendenteDownload(Instituicao instituicao) {
		Criteria criteria = getCriteria(DesistenciaProtesto.class);
		criteria.createAlias("cabecalhoCartorio", "cabecalho");

		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", instituicao.getMunicipio().getCodigoIBGE()));
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			criteria.createAlias("remessaDesistenciaProtesto", "remessaDesistenciaProtesto");
			criteria.createAlias("remessaDesistenciaProtesto.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", instituicao.getCodigoCompensacao()));
		}
		criteria.add(Restrictions.eq("download", false));
		return criteria.list();
	}

	public DesistenciaProtesto alterarSituacaoDesistenciaProtesto(DesistenciaProtesto desistenciaProtesto,
			boolean download) {
		Transaction transaction = getSession().beginTransaction();

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

	public DesistenciaProtesto buscarDesistenciaProtesto(DesistenciaProtesto entidade) {
		return super.buscarPorPK(entidade);
	}

	public DesistenciaProtesto buscarDesistenciaProtesto(Instituicao cartorio, String nomeArquivo) {
		Criteria criteria = getCriteria(DesistenciaProtesto.class);
		criteria.createAlias("remessaDesistenciaProtesto", "remessaDesistenciaProtesto");
		criteria.createAlias("remessaDesistenciaProtesto.arquivo", "arquivo");
		criteria.createAlias("cabecalhoCartorio", "cabecalhoCartorio");
		criteria.add(Restrictions.eq("cabecalhoCartorio.codigoMunicipio", cartorio.getMunicipio().getCodigoIBGE()));
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		return DesistenciaProtesto.class.cast(criteria.uniqueResult());
	}

	@Transactional
	public void alterarSituacaoDesistenciaProtesto(Instituicao cartorio, String nomeArquivo) {
		StringBuffer sql = new StringBuffer();

		cartorio.setMunicipio(buscarPorPK(cartorio.getMunicipio(), Municipio.class));
		try {
			sql.append("UPDATE tb_desistencia_protesto AS dp ");
			sql.append("SET download_realizado=true ");
			sql.append("FROM tb_remessa_desistencia_protesto AS rem, tb_cabecalho AS cab, tb_arquivo AS arq ");
			sql.append("WHERE dp.remessa_desistencia_protesto_id=rem.id_remessa_desistencia_protesto ");
			sql.append("AND rem.arquivo_id=arq.id_arquivo ");
			sql.append("AND arq.nome_arquivo LIKE '" + nomeArquivo + "' ");
			sql.append("AND cab.codigo_municipio='" + cartorio.getMunicipio().getCodigoIBGE() + "'");

			Query query = getSession().createSQLQuery(sql.toString());
			query.executeUpdate();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível alterar a desistência para recebido.");
		}
	}
}
