package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class AdministracaoDAO extends AbstractBaseDAO {

	@Autowired
	private InstituicaoDAO instituicaoDAO;

	/**
	 * Consultar arquivos para remover na entidade Arquivo
	 * 
	 * @param arquivo
	 * @param tiposArquivo
	 * @param municipio
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Arquivo> consultarArquivosParaRemover(Arquivo arquivo, ArrayList<TipoArquivoFebraban> tiposArquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim) {
		Criteria criteria = getCriteria(Arquivo.class);
		if (arquivo.getInstituicaoEnvio() != null) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", arquivo.getInstituicaoEnvio()),
					Restrictions.eq("instituicaoRecebe", arquivo.getInstituicaoEnvio())));
		}
		if (arquivo.getNomeArquivo() != null && arquivo.getNomeArquivo() != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike("nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
		}
		if (!tiposArquivo.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			for (TipoArquivoFebraban tipoArquivo : tiposArquivo) {
				disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipoArquivo));
			}
			criteria.add(disjunction);
		}
		if (municipio != null) {
			Instituicao cartorioProtesto = instituicaoDAO.buscarCartorioPorMunicipio(municipio.getNomeMunicipio());
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", cartorioProtesto), Restrictions.eq("instituicaoRecebe", cartorioProtesto)));
		}
		if (dataInicio != null) {
			criteria.add(Restrictions.between("dataEnvio", dataInicio, dataFim));
		}
		criteria.createAlias("tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO));
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO));
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO));
		criteria.addOrder(Order.desc("dataEnvio"));
		return criteria.list();
	}

	/**
	 * Remover arquivo de Remessa enviado pelas instituições
	 * 
	 * @param arquivo
	 */
	public Arquivo removerRemessa(Arquivo arquivo) {
		
		try {
			Query query = createSQLQuery("DELETE FROM tb_anexo AS ane " + "WHERE ane.titulo_id IN ( " + "	SELECT id_titulo FROM tb_titulo AS tit "
					+ "	INNER JOIN tb_remessa AS rem ON tit.remessa_id=rem.id_remessa "
					+ "	INNER JOIN tb_arquivo AS arq ON rem.arquivo_id=arq.id_arquivo " + "	WHERE arq.id_arquivo=" + arquivo.getId() + ");"
					+ "DELETE FROM tb_titulo AS tit " + "WHERE remessa_id IN ( " + "	SELECT id_remessa FROM tb_remessa AS rem "
					+ "	INNER JOIN tb_arquivo AS arq ON rem.arquivo_id=arq.id_arquivo " + "	WHERE arq.id_arquivo=" + arquivo.getId() + ");"
					+ "DELETE FROM tb_remessa AS rem " + "WHERE id_remessa in ( " + "	SELECT id_remessa FROM tb_remessa AS rem "
					+ "	INNER JOIN tb_arquivo AS arq ON rem.arquivo_id=arq.id_arquivo " + "	WHERE arq.id_arquivo=" + arquivo.getId() + ");"
					+ "DELETE FROM tb_arquivo AS arq WHERE arq.id_arquivo=" + arquivo.getId() + ";");
			query.executeUpdate();
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			throw new InfraException("Não foi possível remover o arquivo! Verifique as informações vínculadas a ele...");
		}
		return arquivo;
	}

	/**
	 * Remover Arquivo de Confirmação gerado pela CRA e marcar os recebidos pelo cartório como não liberados
	 * 
	 * @param arquivo
	 */
	public Arquivo removerConfirmacaoCRA(Arquivo arquivo) {
		
		try {
			Query query = createSQLQuery("UPDATE tb_remessa AS rem_2 " + "SET situacao=false, arquivo_gerado_banco_id=rem_2.arquivo_id "
					+ "WHERE rem_2.id_remessa IN ( " + "SELECT rem.id_remessa FROM tb_remessa AS rem " + " WHERE rem.arquivo_gerado_banco_id="
					+ arquivo.getId() + ");" + " DELETE FROM tb_arquivo AS arq WHERE arq.id_arquivo=" + arquivo.getId() + ";");
			query.executeUpdate();
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			throw new InfraException("Não foi possível remover o arquivo! Verifique as informações vínculadas a ele...");
		}
		return arquivo;
	}

	/**
	 * Removar arquivo de Confirmações enviado pelos cartórios
	 * 
	 * @param arquivo
	 */
	public Arquivo removerConfirmacaoCartorio(Arquivo arquivo) {
		
		try {
			Query query = createSQLQuery("DELETE FROM tb_confirmacao AS conf " + "WHERE remessa_id IN ( " + "	SELECT id_remessa FROM tb_remessa AS rem "
							+ "	INNER JOIN tb_arquivo AS arq ON rem.arquivo_id=arq.id_arquivo " + "	WHERE arq.id_arquivo=" + arquivo.getId() + "); "
							+ "DELETE FROM tb_remessa AS rem " + "WHERE id_remessa in ( " + "	SELECT id_remessa FROM tb_remessa AS rem "
							+ "	INNER JOIN tb_arquivo AS arq ON rem.arquivo_id=arq.id_arquivo " + "	WHERE arq.id_arquivo=" + arquivo.getId() + "); "
							+ "DELETE FROM tb_arquivo AS arq WHERE arq.id_arquivo=" + arquivo.getId() + ";");
			query.executeUpdate();
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			throw new InfraException("Não foi possível remover o arquivo! Verifique as informações vínculadas a ele...");
		}
		return arquivo;
	}

	/**
	 * Remover Arquivo de Retorno gerado pela CRA e marcar os recebidos pelo cartório como não liberados
	 * 
	 * @param arquivo
	 */
	public Arquivo removerRetornoCRA(Arquivo arquivo) {
		
		try {
			Query query = createSQLQuery("UPDATE tb_remessa AS rem_2 " + "SET situacao=false, arquivo_gerado_banco_id=rem_2.arquivo_id "
					+ "WHERE rem_2.id_remessa IN ( " + "SELECT rem.id_remessa FROM tb_remessa AS rem " + " WHERE rem.arquivo_gerado_banco_id="
					+ arquivo.getId() + ");" + " DELETE FROM tb_arquivo AS arq WHERE arq.id_arquivo=" + arquivo.getId() + ";");
			query.executeUpdate();
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			throw new InfraException("Não foi possível remover o arquivo! Verifique as informações vínculadas a ele...");
		}
		return arquivo;
	}

	/**
	 * Removar arquivo de Retonro enviado pelos cartórios
	 * 
	 * @param arquivo
	 */
	public Arquivo removerRetornoCartorio(Arquivo arquivo) {
		try {
			Query query = createSQLQuery("DELETE FROM tb_retorno AS ret " + "WHERE remessa_id IN (" + "	SELECT id_remessa FROM tb_remessa AS rem "
					+ "	INNER JOIN tb_arquivo AS arq ON rem.arquivo_id=arq.id_arquivo " + "	WHERE arq.id_arquivo=" + arquivo.getId() + "); "
					+ "DELETE FROM tb_remessa AS rem " + " WHERE id_remessa in (" + "	SELECT id_remessa FROM tb_remessa AS rem "
					+ "	INNER JOIN tb_arquivo AS arq ON rem.arquivo_id=arq.id_arquivo " + "	WHERE arq.id_arquivo=" + arquivo.getId() + ");"
					+ "DELETE FROM tb_arquivo AS arq WHERE arq.id_arquivo=" + arquivo.getId() + ";");
			query.executeUpdate();
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			throw new InfraException("Não foi possível remover o arquivo! Verifique as informações vínculadas a ele...");
		}
		return arquivo;
	}
}