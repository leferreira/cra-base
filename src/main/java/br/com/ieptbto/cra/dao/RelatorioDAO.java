package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.view.ViewTitulo;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class RelatorioDAO extends AbstractBaseDAO {

	public List<ViewTitulo> relatorioTitulosGeral(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao, 
			Instituicao bancoConvenio, Instituicao cartorio) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT v ");
		sql.append("FROM ViewTitulo v ");
		sql.append("WHERE v.dataRecebimento_Arquivo_Remessa BETWEEN :dataInicio AND :dataFim ");
		
		if (bancoConvenio != null && cartorio == null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
		}
		if (cartorio != null && bancoConvenio == null) {
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (bancoConvenio != null && cartorio != null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			sql.append("AND v.tipoInstituicao_Instituicao = " + tipoInstituicao.getConstante() + " ");
		}
		sql.append("ORDER BY v.nomeFantasia_Instituicao, v.nomeMunicipio_Municipio, v.id_TituloRemessa ASC");
		Query query = getSession().createQuery(sql.toString());
		query.setDate("dataInicio", dataInicio.toDate());
		query.setDate("dataFim", dataFim.toDate());
		return query.list();
	}

	public List<ViewTitulo> relatorioTitulosSemConfirmacao(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT v ");
		sql.append("FROM ViewTitulo v ");
		sql.append("WHERE v.dataRecebimento_Arquivo_Remessa BETWEEN :dataInicio AND :dataFim ");
		sql.append("AND v.id_Confirmacao IS NULL ");
		
		if (bancoConvenio != null && cartorio == null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
		}
		if (cartorio != null && bancoConvenio == null) {
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (bancoConvenio != null && cartorio != null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			sql.append("AND v.tipoInstituicao_Instituicao = " + tipoInstituicao.getConstante() + " ");
		}
		sql.append("ORDER BY v.nomeFantasia_Instituicao, v.nomeMunicipio_Municipio, v.id_TituloRemessa ASC");
		Query query = getSession().createQuery(sql.toString());
		query.setDate("dataInicio", dataInicio.toDate());
		query.setDate("dataFim", dataFim.toDate());
		return query.list();
	}

	public List<ViewTitulo> relatorioTitulosConfirmadosSemRetorno(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT v ");
		sql.append("FROM ViewTitulo v ");
		sql.append("WHERE v.dataRecebimento_Arquivo_Remessa BETWEEN :dataInicio AND :dataFim ");
		sql.append("AND v.situacaoTitulo = '" + TipoOcorrencia.ABERTO + "' ");
		sql.append("AND v.numeroControleDevedor_TituloRemessa = " + Integer.valueOf(ConfiguracaoBase.UM) + " ");
		
		if (bancoConvenio != null && cartorio == null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
		}
		if (cartorio != null && bancoConvenio == null) {
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (bancoConvenio != null && cartorio != null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			sql.append("AND v.tipoInstituicao_Instituicao = " + tipoInstituicao.getConstante() + " ");
		}
		sql.append("ORDER BY v.nomeFantasia_Instituicao, v.nomeMunicipio_Municipio, v.id_TituloRemessa ASC");
		Query query = getSession().createQuery(sql.toString());
		query.setDate("dataInicio", dataInicio.toDate());
		query.setDate("dataFim", dataFim.toDate());
		return query.list();
	}

	public List<ViewTitulo> relatorioTitulosRetorno(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao, Instituicao bancoConvenio,
			Instituicao cartorio) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT v ");
		sql.append("FROM ViewTitulo v ");
		sql.append("WHERE v.dataRecebimento_Arquivo_Remessa BETWEEN :dataInicio AND :dataFim ");
		sql.append("AND v.id_Retorno IS NOT NULL ");
		
		if (bancoConvenio != null && cartorio == null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
		}
		if (cartorio != null && bancoConvenio == null) {
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (bancoConvenio != null && cartorio != null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			sql.append("AND v.tipoInstituicao_Instituicao = " + tipoInstituicao.getConstante() + " ");
		}
		sql.append("ORDER BY v.nomeFantasia_Instituicao, v.nomeMunicipio_Municipio, v.id_TituloRemessa ASC");
		Query query = getSession().createQuery(sql.toString());
		query.setDate("dataInicio", dataInicio.toDate());
		query.setDate("dataFim", dataFim.toDate());
		return query.list();
	}

	public List<ViewTitulo> relatorioTitulosPagos(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao, Instituicao bancoConvenio,
			Instituicao cartorio) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT v ");
		sql.append("FROM ViewTitulo v ");
		sql.append("WHERE v.dataRecebimento_Arquivo_Remessa BETWEEN :dataInicio AND :dataFim ");
		sql.append("AND v.situacaoTitulo = '" + TipoOcorrencia.PAGO + "' ");
		
		if (bancoConvenio != null && cartorio == null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
		}
		if (cartorio != null && bancoConvenio == null) {
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (bancoConvenio != null && cartorio != null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			sql.append("AND v.tipoInstituicao_Instituicao = " + tipoInstituicao.getConstante() + " ");
		}
		sql.append("ORDER BY v.nomeFantasia_Instituicao, v.nomeMunicipio_Municipio, v.id_TituloRemessa ASC");
		Query query = getSession().createQuery(sql.toString());
		query.setDate("dataInicio", dataInicio.toDate());
		query.setDate("dataFim", dataFim.toDate());
		return query.list();
	}

	public List<ViewTitulo> relatorioTitulosProtestados(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT v ");
		sql.append("FROM ViewTitulo v ");
		sql.append("WHERE v.dataRecebimento_Arquivo_Remessa BETWEEN :dataInicio AND :dataFim ");
		sql.append("AND v.situacaoTitulo = '" + TipoOcorrencia.PROTESTADO + "' ");
		
		if (bancoConvenio != null && cartorio == null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
		}
		if (cartorio != null && bancoConvenio == null) {
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (bancoConvenio != null && cartorio != null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			sql.append("AND v.tipoInstituicao_Instituicao = " + tipoInstituicao.getConstante() + " ");
		}
		sql.append("ORDER BY v.nomeFantasia_Instituicao, v.nomeMunicipio_Municipio, v.id_TituloRemessa ASC");
		Query query = getSession().createQuery(sql.toString());
		query.setDate("dataInicio", dataInicio.toDate());
		query.setDate("dataFim", dataFim.toDate());
		return query.list();
	}

	public List<ViewTitulo> relatorioTitulosRetiradosDevolvidos(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT v ");
		sql.append("FROM ViewTitulo v ");
		sql.append("WHERE v.dataRecebimento_Arquivo_Remessa BETWEEN :dataInicio AND :dataFim ");
		sql.append("AND ( v.situacaoTitulo = '" + TipoOcorrencia.RETIRADO + "' "
				+ "	OR v.situacaoTitulo = '" + TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS + "' "
				+ "	OR v.situacaoTitulo = '" + TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_COM_CUSTAS + "' "
				+ ") ");
		
		if (bancoConvenio != null && cartorio == null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
		}
		if (cartorio != null && bancoConvenio == null) {
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (bancoConvenio != null && cartorio != null) {
			sql.append("AND v.id_Instituicao_Instituicao = " + bancoConvenio.getId() + " ");
			sql.append("AND v.id_Instituicao_Cartorio = " + cartorio.getId() + " ");
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			sql.append("AND v.tipoInstituicao_Instituicao = " + tipoInstituicao.getConstante() + " ");
		}
		sql.append("ORDER BY v.nomeFantasia_Instituicao, v.nomeMunicipio_Municipio, v.id_TituloRemessa ASC");
		Query query = getSession().createQuery(sql.toString());
		query.setDate("dataInicio", dataInicio.toDate());
		query.setDate("dataFim", dataFim.toDate());
		return query.list();
	}

	public List<ViewTitulo> relatorioTitulosDesistenciaProtesto(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao instituicao, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		TipoInstituicaoCRA tipoInstituicaoParametro = instituicao.getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicaoParametro.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA) || tipoInstituicaoParametro.equals(TipoInstituicaoCRA.CONVENIO)) {

		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoCRA.CARTORIO)) {

		}
		return criteria.list();
	}

	public List<ViewTitulo> relatorioTitulosAutorizacaoCancelamento(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao instituicao, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		TipoInstituicaoCRA tipoInstituicaoParametro = instituicao.getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicaoParametro.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA) || tipoInstituicaoParametro.equals(TipoInstituicaoCRA.CONVENIO)) {

		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoCRA.CARTORIO)) {

		}
		return criteria.list();
	}

	public ViewTitulo relatorioTitulosPendentes(String nossoNumero, String numeroProtocoloCartorio) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT v ");
		sql.append("FROM ViewTitulo v ");
		sql.append("WHERE v.numeroProtocoloCartorio_Confirmacao LIKE :protocolo ");
		sql.append("AND v. LIKE :nossoNumero ");
		sql.append("ORDER BY v.dataRecebimento_Arquivo_Remessa DESC ");
		Query query = getSession().createQuery(sql.toString());
		query.setText("protocolo", "%" + numeroProtocoloCartorio + "%");
		query.setText("nossoNumero", "%" + nossoNumero + "%" );
		query.setMaxResults(1);
		return ViewTitulo.class.cast(query.uniqueResult());
	}
}