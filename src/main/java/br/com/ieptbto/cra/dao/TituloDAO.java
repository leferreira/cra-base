package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.BancoAgenciaCentralizadoraCodigoCartorio;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.TituloException;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public class TituloDAO extends AbstractBaseDAO {

	public Confirmacao buscarConfirmacao(TituloRemessa titulo) {
		Criteria criteria = getCriteria(Confirmacao.class);
		criteria.add(Restrictions.eq("titulo", titulo));
		return Confirmacao.class.cast(criteria.uniqueResult());
	}

	public Retorno buscarRetorno(TituloRemessa titulo) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("titulo", titulo));
		return Retorno.class.cast(criteria.uniqueResult());
	}

	public List<TituloRemessa> buscarTitulos(Usuario usuario, LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio, TituloRemessa titulo) {
		Instituicao instituicaoUsuario = usuario.getInstituicao();

		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		if (!instituicaoUsuario.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("remessa.instituicaoOrigem", instituicaoUsuario),
					Restrictions.eq("remessa.instituicaoDestino", instituicaoUsuario)));
		}

		if (tipoInstituicao != null && bancoConvenio == null) {
			criteria.createAlias("remessa.instituicaoOrigem", "apresentante");
			criteria.createAlias("apresentante.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		if (bancoConvenio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoOrigem", bancoConvenio));
		}
		if (cartorio != null) {
			criteria.add(Restrictions.eq("remessa.instituicaoDestino", cartorio));
		}

		if (titulo.getNossoNumero() != null && titulo.getNossoNumero() != StringUtils.EMPTY) {
			criteria.add(Restrictions.like("nossoNumero", titulo.getNossoNumero(), MatchMode.EXACT));
		}
		if (titulo.getNumeroProtocoloCartorio() != null && titulo.getNumeroProtocoloCartorio() != StringUtils.EMPTY) {
			criteria.createAlias("confirmacao", "confirmacao");
			criteria.add(Restrictions.eq("confirmacao.numeroProtocoloCartorio", titulo.getNumeroProtocoloCartorio()));
		}

		if (titulo.getNumeroTitulo() != null && titulo.getNumeroTitulo() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("numeroTitulo", titulo.getNumeroTitulo(), MatchMode.EXACT));

		if (titulo.getNomeSacadorVendedor() != null && titulo.getNomeSacadorVendedor() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("nomeSacadorVendedor", titulo.getNomeSacadorVendedor(), MatchMode.ANYWHERE));

		if (titulo.getDocumentoSacador() != null && titulo.getDocumentoSacador() != StringUtils.EMPTY)
			criteria.add(Restrictions.like("documentoSacador", titulo.getDocumentoSacador(), MatchMode.ANYWHERE));

		if (titulo.getNomeDevedor() != null && titulo.getNomeDevedor() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE));

		if (titulo.getNumeroIdentificacaoDevedor() != null && titulo.getNumeroIdentificacaoDevedor() != StringUtils.EMPTY)
			criteria.add(Restrictions.like("numeroIdentificacaoDevedor", titulo.getNumeroIdentificacaoDevedor(), MatchMode.EXACT));

		if (dataInicio != null) {
			criteria.add(Restrictions.sqlRestriction("DATE(data_cadastro) >= ?", dataInicio.toDate(), org.hibernate.type.StandardBasicTypes.DATE));
			criteria.add(Restrictions.sqlRestriction("DATE(data_cadastro) <= ?", dataFim.toDate(), org.hibernate.type.StandardBasicTypes.DATE));
		}
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public List<TituloRemessa> consultarProtestos(String documentoDevedor) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("retorno", "retorno");
		criteria.add(Restrictions.ilike("numeroIdentificacaoDevedor", documentoDevedor, MatchMode.EXACT));
		criteria.add(Restrictions.eq("retorno.tipoOcorrencia", "2"));
		return criteria.list();
	}

	public TituloRemessa buscarTituloPorChave(TituloRemessa titulo) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("dataCadastro", titulo.getDataCadastro()));
		criteria.add(Restrictions.eq("codigoPortador", titulo.getCodigoPortador()));
		criteria.add(Restrictions.eq("nossoNumero", titulo.getNossoNumero()));
		if (titulo.getNumeroTitulo() != null) {
			criteria.add(Restrictions.eq("numeroTitulo", titulo.getNumeroTitulo()));
		}
		TituloRemessa tituloRemessa = TituloRemessa.class.cast(criteria.uniqueResult());
		if (tituloRemessa == null) {
			throw new InfraException("O Título [ Nosso Número : " + titulo.getNossoNumero() + " ] não existe na CRA !");
		}
		return tituloRemessa;
	}

	public List<Titulo> carregarTitulosGenerico(Arquivo arquivo) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("arquivo", arquivo));
		List<Remessa> remessas = criteria.list();

		List<Titulo> titulos = new ArrayList<Titulo>();
		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(Titulo.class);
			criteriaTitulo.add(Restrictions.eq("remessa", remessa));
			titulos.addAll(criteriaTitulo.list());
		}
		return titulos;
	}

	public List<Titulo> carregarTitulosGenerico(Remessa remessa) {
		Criteria criteriaTitulo = getCriteria(Titulo.class);
		criteriaTitulo.add(Restrictions.eq("remessa", remessa));
		return criteriaTitulo.list();
	}

	public TituloRemessa salvar(Titulo titulo, List<Exception> erros, Transaction transaction) {
		if (TituloRemessa.class.isInstance(titulo)) {
			TituloRemessa tituloRemessa = TituloRemessa.class.cast(titulo);
			return salvarTituloRemessa(tituloRemessa, erros);
		}
		if (Confirmacao.class.isInstance(titulo)) {
			Confirmacao tituloConfirmacao = Confirmacao.class.cast(titulo);
			return salvarTituloConfirmacao(tituloConfirmacao, erros);
		}
		if (Retorno.class.isInstance(titulo)) {
			Retorno tituloConfirmacao = Retorno.class.cast(titulo);
			return salvarTituloRetorno(tituloConfirmacao, erros);
		}
		return null;
	}

	/**
	 * Salvar título Remessa
	 * 
	 * @param tituloRemessa
	 * @param erros
	 * @return
	 */
	private TituloRemessa salvarTituloRemessa(TituloRemessa tituloRemessa, List<Exception> erros) {

		try {
			tituloRemessa.setDataCadastro(tituloRemessa.getRemessa().getCabecalho().getDataMovimento().toDate());
			TituloRemessa tituloSalvo = save(tituloRemessa);

			if (tituloRemessa.getAnexo() != null) {
				tituloRemessa.getAnexo().setTitulo(tituloSalvo);
				save(tituloRemessa.getAnexo());
			}
			return save(tituloRemessa);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * Salvar título confirmacao
	 * 
	 * @param tituloRemessa
	 * @param erros
	 * @return
	 */
	private TituloRemessa salvarTituloConfirmacao(Confirmacao tituloConfirmacao, List<Exception> erros) {
		TituloRemessa titulo = buscaTituloConfirmacaoSalvo(tituloConfirmacao, erros);

		try {
			BancoAgenciaCentralizadoraCodigoCartorio banco = BancoAgenciaCentralizadoraCodigoCartorio.getBanco(tituloConfirmacao.getCodigoPortador());
			if (banco != null) {
				tituloConfirmacao.setCodigoCartorio(banco.getCodigoCartorio());
			}
			if (tituloConfirmacao.getNumeroProtocoloCartorio() != null) {
				Integer numeroProtocoloCartorio = Integer.valueOf(tituloConfirmacao.getNumeroProtocoloCartorio().trim());
				titulo.setNumeroProtocoloCartorio(numeroProtocoloCartorio.toString());
			}
			TipoOcorrencia tipoOcorrencia = null;
			if (tituloConfirmacao.getTipoOcorrencia() != null) {
				tipoOcorrencia = TipoOcorrencia.getTipoOcorrencia(tituloConfirmacao.getTipoOcorrencia());
				if (!TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.equals(tipoOcorrencia)) {
					tituloConfirmacao.setValorGravacaoEletronica(titulo.getRemessa().getInstituicaoOrigem().getValorConfirmacao());
				}
			}

			if (titulo != null) {
				tituloConfirmacao.setTitulo(titulo);
				titulo.setConfirmacao(save(tituloConfirmacao));
				save(titulo);
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return titulo;
	}

	/**
	 * Salvar título Retorno
	 * 
	 * @param tituloRemessa
	 * @param erros
	 * @return
	 */
	private TituloRemessa salvarTituloRetorno(Retorno tituloRetorno, List<Exception> erros) {
		TituloRemessa titulo = buscaTituloRetornoSalvo(tituloRetorno, erros);

		try {
			BancoAgenciaCentralizadoraCodigoCartorio banco = BancoAgenciaCentralizadoraCodigoCartorio.getBanco(tituloRetorno.getCodigoPortador());
			if (banco != null) {
				tituloRetorno.setCodigoCartorio(banco.getCodigoCartorio());
			}

			if (titulo != null) {
				tituloRetorno.setTitulo(titulo);
				titulo.setRetorno(save(tituloRetorno));
				save(titulo);
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return titulo;
	}

	/**
	 * Buscar Título para víncular confirmacao
	 * 
	 * @param tituloConfirmacao
	 * @param erros
	 * @return
	 */
	public TituloRemessa buscaTituloConfirmacaoSalvo(Confirmacao tituloConfirmacao, List<Exception> erros) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", tituloConfirmacao.getRemessa().getCabecalho().getCodigoMunicipio()));
		criteria.add(Restrictions.like("codigoPortador", tituloConfirmacao.getCodigoPortador().trim(), MatchMode.EXACT));
		criteria.add(Restrictions.like("nossoNumero", tituloConfirmacao.getNossoNumero(), MatchMode.EXACT));
		criteria.add(Restrictions.like("numeroTitulo", tituloConfirmacao.getNumeroTitulo(), MatchMode.EXACT));

		List<TituloRemessa> titulos = criteria.list();
		for (TituloRemessa titulo : titulos) {
			if (titulo.getConfirmacao() == null) {
				return titulo;
			}
		}
		if (!titulos.isEmpty()) {
			erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_CONFIRMACAO_JA_ENVIADO, tituloConfirmacao.getNossoNumero(),
					tituloConfirmacao.getNumeroSequencialArquivo()));
		} else {
			erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_NAO_ENCONTRADO, tituloConfirmacao.getNossoNumero(),
					tituloConfirmacao.getNumeroSequencialArquivo()));
		}
		return null;
	}

	/**
	 * Buscar Título para víncular retorno
	 * 
	 * @param tituloRetorno
	 * @param erros
	 * @return
	 */
	public TituloRemessa buscaTituloRetornoSalvo(Retorno tituloRetorno, List<Exception> erros) {
		Integer numeroProtocolo = Integer.parseInt(tituloRetorno.getNumeroProtocoloCartorio().trim());
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", tituloRetorno.getRemessa().getCabecalho().getCodigoMunicipio()));
		criteria.add(Restrictions.ilike("codigoPortador", tituloRetorno.getCodigoPortador(), MatchMode.EXACT));
		criteria.add(Restrictions.like("nossoNumero", tituloRetorno.getNossoNumero().trim(), MatchMode.EXACT));
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.add(Restrictions.like("confirmacao.numeroProtocoloCartorio", numeroProtocolo.toString(), MatchMode.EXACT));

		List<TituloRemessa> titulos = criteria.list();
		for (TituloRemessa titulo : titulos) {
			if (titulo.getRetorno() == null) {
				return titulo;
			}
		}
		if (!titulos.isEmpty()) {
			erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_RETORNO_JA_ENVIADO, tituloRetorno.getNossoNumero(),
					tituloRetorno.getNumeroSequencialArquivo()));
		} else {
			erros.add(
					new TituloException(CodigoErro.CARTORIO_TITULO_NAO_ENCONTRADO, tituloRetorno.getNossoNumero(), tituloRetorno.getNumeroSequencialArquivo()));
		}
		return null;
	}

	@Transactional
	public Retorno buscarTituloProtestado(String numeroProtocolo, String codigoIBGE) {
		Integer numProtocolo = Integer.parseInt(numeroProtocolo);
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("titulo", "titulo");
		criteria.createAlias("cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", codigoIBGE));
		criteria.add(Restrictions.ilike("numeroProtocoloCartorio", numProtocolo.toString(), MatchMode.EXACT));
		criteria.add(Restrictions.like("tipoOcorrencia", TipoOcorrencia.PROTESTADO.getConstante(), MatchMode.EXACT));
		List<Retorno> retornos = criteria.list();
		for (Retorno retorno : retornos) {
			if (Integer.valueOf(retorno.getCodigoPortador()) < 800) {
				return retorno;
			}
		}
		return null;
	}

	public TituloRemessa buscarTituloDesistenciaProtesto(PedidoDesistencia pedidoDesistenciaCancelamento) {
		Integer numProtocolo = Integer.parseInt(pedidoDesistenciaCancelamento.getNumeroProtocolo());

		Criteria criteria = getCriteria(Confirmacao.class);
		criteria.createAlias("titulo", "titulo");
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio",
				pedidoDesistenciaCancelamento.getDesistenciaProtesto().getCabecalhoCartorio().getCodigoMunicipio()));
		criteria.add(Restrictions.ilike("numeroProtocoloCartorio", numProtocolo.toString(), MatchMode.EXACT));
		criteria.add(Restrictions.eq("dataProtocolo", pedidoDesistenciaCancelamento.getDataProtocolagem()));
		Confirmacao confirmacao = Confirmacao.class.cast(criteria.uniqueResult());

		if (confirmacao == null) {
			return null;
		}
		return confirmacao.getTitulo();
	}

	public TituloRemessa buscarTituloCancelamentoProtesto(PedidoCancelamento pedido) {
		Integer numProtocolo = Integer.parseInt(pedido.getNumeroProtocolo());

		Criteria criteria = getCriteria(Confirmacao.class);
		criteria.createAlias("titulo", "titulo");
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", pedido.getCancelamentoProtesto().getCabecalhoCartorio().getCodigoMunicipio()));
		criteria.add(Restrictions.ilike("numeroProtocoloCartorio", numProtocolo.toString(), MatchMode.EXACT));
		criteria.add(Restrictions.eq("dataProtocolo", pedido.getDataProtocolagem()));
		Confirmacao confirmacao = Confirmacao.class.cast(criteria.uniqueResult());

		if (confirmacao == null) {
			return null;
		}
		return confirmacao.getTitulo();
	}

	public TituloRemessa buscarTituloAutorizacaoCancelamento(PedidoAutorizacaoCancelamento pedido) {
		Long numProtocolo = Long.parseLong(pedido.getNumeroProtocolo());

		Criteria criteria = getCriteria(Confirmacao.class);
		criteria.createAlias("titulo", "titulo");
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", pedido.getAutorizacaoCancelamento().getCabecalhoCartorio().getCodigoMunicipio()));
		criteria.add(Restrictions.ilike("numeroProtocoloCartorio", numProtocolo.toString(), MatchMode.EXACT));
		criteria.add(Restrictions.eq("dataProtocolo", pedido.getDataProtocolagem()));
		Confirmacao confirmacao = Confirmacao.class.cast(criteria.uniqueResult());

		if (confirmacao == null) {
			return null;
		}
		return confirmacao.getTitulo();
	}

	public List<TituloRemessa> carregarTitulos(Remessa remessa) {
		Criteria criteria = getCriteria(TituloRemessa.class);

		if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			criteria.add(Restrictions.eq("remessa", remessa));
		} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			criteria.createAlias("confirmacao", "confirmacao");
			criteria.add(Restrictions.eq("confirmacao.remessa", remessa));
		} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			criteria.createAlias("retorno", "retorno");
			criteria.add(Restrictions.eq("retorno.remessa", remessa));
		}
		return criteria.list();
	}

	public List<TituloRemessa> carregarTitulosRemessaComDocumentosAnexos(Remessa remessa) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("remessa", remessa));
		criteria.createAlias("anexo", "anexo");
		return criteria.list();
	}

	public TituloRemessa buscarTituloRemessaPorDadosRetorno(Retorno tituloRetorno) {
		Integer numeroProtocolo = Integer.parseInt(tituloRetorno.getNumeroProtocoloCartorio().trim());
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", tituloRetorno.getRemessa().getCabecalho().getCodigoMunicipio()));
		criteria.add(Restrictions.ilike("codigoPortador", tituloRetorno.getCodigoPortador(), MatchMode.EXACT));
		criteria.add(Restrictions.like("nossoNumero", tituloRetorno.getNossoNumero().trim(), MatchMode.EXACT));
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.add(Restrictions.like("confirmacao.numeroProtocoloCartorio", numeroProtocolo.toString(), MatchMode.EXACT));
		return TituloRemessa.class.cast(criteria.uniqueResult());
	}
}
