package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
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
	public List<DesistenciaProtesto> buscarDesistenciaProtesto(Arquivo arquivo, Instituicao portador, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
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

	@Transactional(readOnly = true)
	public Arquivo buscarDesistenciaCancelamentoCartorio(Instituicao cartorio, String nome) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.eq("nomeArquivo", nome));
		
		if (nome.contains(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.getConstante())) {
			criteria.createAlias("remessaDesistenciaProtesto", "remessaDesistenciaProtesto");
			criteria.createAlias("remessaDesistenciaProtesto.desistenciaProtesto", "desistenciaCancelamento");
			criteria.createAlias("desistenciaCancelamento.desistencias", "desistencias");
		} else if (nome.contains(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.getConstante())) {
			criteria.createAlias("remessaCancelamentoProtesto", "remessaCancelamentoProtesto");
			criteria.createAlias("remessaCancelamentoProtesto.cancelamentoProtesto", "desistenciaCancelamento");
			criteria.createAlias("desistenciaCancelamento.cancelamentos", "cancelamentos");
		} else if (nome.contains(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
			criteria.createAlias("remessaAutorizacao", "remessaAutorizacao");
			criteria.createAlias("remessaAutorizacao.autorizacaoCancelamento", "desistenciaCancelamento");
			criteria.createAlias("autorizacaoCancelamento.autorizacoesCancelamentos", "autorizacoesCancelamentos");
		}
		criteria.createAlias("desistenciaCancelamento.cabecalhoCartorio", "cabecalhoCartorio");
		criteria.add(Restrictions.eq("cabecalhoCartorio.codigoMunicipio", cartorio.getMunicipio().getCodigoIBGE()));
		return Arquivo.class.cast(criteria.uniqueResult());
	}
}
