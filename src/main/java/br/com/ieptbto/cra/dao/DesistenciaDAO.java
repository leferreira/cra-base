package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
	public List<DesistenciaProtesto> consultarDesistencias(String nomeArquivo, Instituicao portador, Instituicao cartorio,
			LocalDate dataInicio, LocalDate dataFim, List<TipoArquivoFebraban> tiposArquivo, Usuario usuario) {
		Criteria criteria = getCriteria(DesistenciaProtesto.class);
		criteria.createAlias("remessaDesistenciaProtesto", "remessa");
		criteria.createAlias("remessa.arquivo", "arquivo");

		if (StringUtils.isNotBlank(nomeArquivo)) {
			criteria.add(Restrictions.ilike("arquivo.nomeArquivo", nomeArquivo, MatchMode.ANYWHERE));
		}
		if (tiposArquivo != null && !tiposArquivo.isEmpty()) {
			criteria.createAlias("arquivo.tipoArquivo", "tipo");
			Disjunction disjunction = Restrictions.disjunction();
			for (TipoArquivoFebraban tipo : tiposArquivo) {
				disjunction.add(Restrictions.eq("tipo.tipoArquivo", tipo));
			}
			criteria.add(disjunction);
		}
		if (dataInicio != null && dataFim != null) {
			criteria.add((Restrictions.between("arquivo.dataEnvio", dataInicio, dataFim)));
		}
		if (portador != null) {
			criteria.createAlias("remessa.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", portador.getCodigoCompensacao()));
		}
		if (cartorio != null) {
			criteria.createAlias("cabecalhoCartorio", "cabecalho");
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", cartorio.getMunicipio().getCodigoIBGE()));
		}

		Instituicao instituicaoUsuario = buscarPorPK(usuario.getInstituicao());
		TipoInstituicaoCRA tipoInstituicaoUsuario = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicaoUsuario == TipoInstituicaoCRA.CARTORIO) {
			criteria.createAlias("cabecalhoCartorio", "cabecalho");
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", instituicaoUsuario.getMunicipio().getCodigoIBGE()));
		} else if (tipoInstituicaoUsuario == TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA) {
			criteria.createAlias("remessa.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", instituicaoUsuario.getCodigoCompensacao()));
		}
		return criteria.list();
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

	public DesistenciaProtesto alterarSituacaoDesistenciaProtesto(DesistenciaProtesto desistenciaProtesto, boolean download) {
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
		criteria.createAlias("desistencias", "desistencias");
		criteria.createAlias("remessaDesistenciaProtesto.arquivo", "arquivo");
		criteria.createAlias("cabecalhoCartorio", "cabecalhoCartorio");
		criteria.add(Restrictions.eq("cabecalhoCartorio.codigoMunicipio", cartorio.getMunicipio().getCodigoIBGE()));
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		return DesistenciaProtesto.class.cast(criteria.uniqueResult());
	}
}