package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author thasso
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class ConfirmacaoDAO extends AbstractBaseDAO {

	/**
	 * Salva os arquivos de confirmações gerados pela a CRA para as Instituições
	 * @param usuario
	 * @param confirmacoes
	 * @return
	 */
	public List<Arquivo> salvarArquivosDeConfirmacaoGerados(Usuario usuario, List<Arquivo> confirmacoes) {
		Transaction transaction = getBeginTransation();

		try {
			for (Arquivo arquivo : confirmacoes) {
				StatusArquivo status = new StatusArquivo();
				status.setData(new LocalDateTime());
				status.setStatusDownload(StatusDownload.AGUARDANDO);

				arquivo.setUsuarioEnvio(usuario);
				arquivo.setStatusArquivo(save(status));
				save(arquivo);

				for (Remessa remessa : arquivo.getRemessas()) {
					remessa.setSituacao(true);
					remessa.setArquivoGeradoProBanco(arquivo);
					update(remessa);
				}
			}
			transaction.commit();
			logger.info("As confirmações foram geradas pelo usuário  " + usuario.getLogin() + "e foram inseridas na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível gerar os arquivos de confirmação.");
		}
		return confirmacoes;
	}

	public List<Remessa> buscarConfirmacoesPendentesDeEnvio() {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoFebraban.CONFIRMACAO));
		criteria.add(Restrictions.eq("situacao", false));
		criteria.addOrder(Order.asc("instituicaoDestino"));
		return criteria.list();
	}

	public List<Remessa> buscarConfirmacoesPendentesPorInstituicao(Instituicao instituicaoDestino) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoFebraban.CONFIRMACAO));
		criteria.add(Restrictions.eq("situacao", false));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicaoDestino));
		return criteria.list();
	}

	public Boolean verificarArquivoConfirmacaoCra(Instituicao cra) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("dataEnvio", new LocalDate()));
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoFebraban.CONFIRMACAO));
		criteria.add(Restrictions.eq("instituicaoEnvio", cra));

		List<Arquivo> arquivosRetornoCRA = criteria.list();
		if (arquivosRetornoCRA.isEmpty()) {
			return false;
		}
		return true;
	}
}
