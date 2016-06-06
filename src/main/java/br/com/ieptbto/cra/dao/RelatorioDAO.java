package br.com.ieptbto.cra.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class RelatorioDAO extends AbstractBaseDAO {

	public List<TituloRemessa> relatorioTitulosGeral(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("confirmacao", "confirmacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("retorno", "retorno", JoinType.LEFT_OUTER_JOIN);

		if (bancoConvenio != null && cartorio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.createAlias("remessa.instituicaoDestino", "instituicaoDestino");
			criteria.createAlias("instituicaoDestino.municipio", "municipio");
			criteria.addOrder(Order.asc("municipio.nomeMunicipio")).addOrder(Order.asc("id"));
		}
		if (cartorio != null && bancoConvenio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
			criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("id"));
		}
		if (bancoConvenio != null && cartorio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.addOrder(Order.asc("id"));
		}
		if (tipoInstituicao != null) {
			if (bancoConvenio == null && cartorio == null) {
				criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
				criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("municipio.nomeMunicipio"))
						.addOrder(Order.asc("id"));
			}
			criteria.createAlias("instituicaoOrigem.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataFim));
		return criteria.list();
	}

	public List<TituloRemessa> relatorioTitulosSemConfirmacao(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("confirmacao", "confirmacao", JoinType.LEFT_OUTER_JOIN);

		if (bancoConvenio != null && cartorio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.createAlias("remessa.instituicaoDestino", "instituicaoDestino");
			criteria.createAlias("instituicaoDestino.municipio", "municipio");
			criteria.addOrder(Order.asc("municipio.nomeMunicipio")).addOrder(Order.asc("id"));
		}
		if (cartorio != null && bancoConvenio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
			criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("id"));
		}
		if (bancoConvenio != null && cartorio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.addOrder(Order.asc("id"));
		}
		if (tipoInstituicao != null) {
			if (bancoConvenio == null && cartorio == null) {
				criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
				criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("municipio.nomeMunicipio"))
						.addOrder(Order.asc("id"));
			}
			criteria.createAlias("instituicaoOrigem.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		criteria.add(Restrictions.isNull("confirmacao.id"));
		criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataFim));
		return criteria.list();
	}

	public List<TituloRemessa> relatorioTitulosConfirmadosSemRetorno(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.createAlias("retorno", "retorno", JoinType.LEFT_OUTER_JOIN);

		if (bancoConvenio != null && cartorio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.createAlias("remessa.instituicaoDestino", "instituicaoDestino");
			criteria.createAlias("instituicaoDestino.municipio", "municipio");
			criteria.addOrder(Order.asc("municipio.nomeMunicipio")).addOrder(Order.asc("id"));
		}
		if (cartorio != null && bancoConvenio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
			criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("id"));
		}
		if (bancoConvenio != null && cartorio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.addOrder(Order.asc("id"));
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			if (cartorio == null) {
				criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
				criteria.createAlias("remessa.instituicaoDestino", "instituicaoDestino");
				criteria.createAlias("instituicaoDestino.municipio", "municipio");
				criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("municipio.nomeMunicipio"))
						.addOrder(Order.asc("id"));
			}
			criteria.createAlias("instituicaoOrigem.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		criteria.add(Restrictions.isNull("retorno.id"));
		criteria.add(Restrictions.ne("confirmacao.tipoOcorrencia", TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante()));
		criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataFim));
		return criteria.list();
	}

	public List<TituloRemessa> relatorioTitulosRetorno(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.createAlias("retorno", "retorno");

		if (bancoConvenio != null && cartorio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.createAlias("remessa.instituicaoDestino", "instituicaoDestino");
			criteria.createAlias("instituicaoDestino.municipio", "municipio");
			criteria.addOrder(Order.asc("municipio.nomeMunicipio")).addOrder(Order.asc("id"));
		}
		if (cartorio != null && bancoConvenio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
			criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("id"));
		}
		if (bancoConvenio != null && cartorio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.addOrder(Order.asc("id"));
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			if (cartorio == null) {
				criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
				criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("municipio.nomeMunicipio"))
						.addOrder(Order.asc("id"));
			}
			criteria.createAlias("instituicaoOrigem.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataFim));
		return criteria.list();
	}

	public List<TituloRemessa> relatorioTitulosPagos(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.createAlias("retorno", "retorno");

		if (bancoConvenio != null && cartorio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.createAlias("remessa.instituicaoDestino", "instituicaoDestino");
			criteria.createAlias("instituicaoDestino.municipio", "municipio");
			criteria.addOrder(Order.asc("municipio.nomeMunicipio")).addOrder(Order.asc("id"));
		}
		if (cartorio != null && bancoConvenio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
			criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("id"));
		}
		if (bancoConvenio != null && cartorio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.addOrder(Order.asc("id"));
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			if (cartorio == null) {
				criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
				criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("municipio.nomeMunicipio"))
						.addOrder(Order.asc("id"));
			}
			criteria.createAlias("instituicaoOrigem.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		criteria.add(Restrictions.eq("retorno.tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataFim));
		return criteria.list();
	}

	public List<TituloRemessa> relatorioTitulosProtestados(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.createAlias("retorno", "retorno");

		if (bancoConvenio != null && cartorio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.createAlias("remessa.instituicaoDestino", "instituicaoDestino");
			criteria.createAlias("instituicaoDestino.municipio", "municipio");
			criteria.addOrder(Order.asc("municipio.nomeMunicipio")).addOrder(Order.asc("id"));
		}
		if (cartorio != null && bancoConvenio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
			criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("id"));
		}
		if (bancoConvenio != null && cartorio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.addOrder(Order.asc("id"));
		}
		if (tipoInstituicao != null) {
			if (bancoConvenio == null && cartorio == null) {
				criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
				criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("municipio.nomeMunicipio"))
						.addOrder(Order.asc("id"));
			}
			criteria.createAlias("instituicaoOrigem.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		criteria.add(Restrictions.eq("retorno.tipoOcorrencia", TipoOcorrencia.PROTESTADO.getConstante()));
		criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataFim));
		return criteria.list();
	}

	public List<TituloRemessa> relatorioTitulosRetiradosDevolvidos(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.createAlias("retorno", "retorno");

		if (bancoConvenio != null && cartorio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.createAlias("remessa.instituicaoDestino", "instituicaoDestino");
			criteria.createAlias("instituicaoDestino.municipio", "municipio");
			criteria.addOrder(Order.asc("municipio.nomeMunicipio")).addOrder(Order.asc("id"));
		}
		if (cartorio != null && bancoConvenio == null) {
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
			criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("id"));
		}
		if (bancoConvenio != null && cartorio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
			criteria.addOrder(Order.asc("id"));
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			if (cartorio == null) {
				criteria.createAlias("remessa.instituicaoOrigem", "instituicaoOrigem");
				criteria.addOrder(Order.asc("instituicaoOrigem.nomeFantasia")).addOrder(Order.asc("municipio.nomeMunicipio"))
						.addOrder(Order.asc("id"));
			}
			criteria.createAlias("instituicaoOrigem.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		Disjunction dijuncao = Restrictions.disjunction();
		dijuncao.add(Restrictions.eq("retorno.tipoOcorrencia", TipoOcorrencia.RETIRADO.getConstante()));
		dijuncao.add(Restrictions.eq("retorno.tipoOcorrencia", TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante()));
		dijuncao.add(Restrictions.eq("retorno.tipoOcorrencia", TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_COM_CUSTAS.getConstante()));
		criteria.add(dijuncao);
		criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataFim));
		return criteria.list();
	}

	public List<TituloRemessa> relatorioTitulosDesistenciaProtesto(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao instituicao, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		TipoInstituicaoCRA tipoInstituicaoParametro = instituicao.getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicaoParametro.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoCRA.CONVENIO)) {

		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoCRA.CARTORIO)) {

		}
		return criteria.list();
	}

	public List<TituloRemessa> relatorioTitulosAutorizacaoCancelamento(LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao instituicao, Instituicao cartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		TipoInstituicaoCRA tipoInstituicaoParametro = instituicao.getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicaoParametro.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoCRA.CONVENIO)) {

		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoCRA.CARTORIO)) {

		}
		return criteria.list();
	}

	public TituloRemessa relatorioTitulosPendentes(String nossoNumero, String numeroProtocoloCartorio) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.createAlias("retorno", "retorno", JoinType.LEFT_OUTER_JOIN);

		if (numeroProtocoloCartorio != null && numeroProtocoloCartorio != StringUtils.EMPTY) {
			criteria.add(Restrictions.eq("confirmacao.numeroProtocoloCartorio", numeroProtocoloCartorio));
		}
		if (nossoNumero != null && nossoNumero != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike("nossoNumero", nossoNumero, MatchMode.ANYWHERE));
		}
		return TituloRemessa.class.cast(criteria.uniqueResult());
	}

}
