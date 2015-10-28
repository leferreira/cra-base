package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public class TituloDAO extends AbstractBaseDAO {

	private static final String DEVOLVIDO = "5";
	@Autowired
	TituloSemTaxaCraDAO tituloSemTaxaCraDAO;

	public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Municipio pracaProtesto, Usuario user) {
		Instituicao instituicaoUsuario = user.getInstituicao();

		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		if (!instituicaoUsuario.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("remessa.instituicaoOrigem", instituicaoUsuario),
			        Restrictions.eq("remessa.instituicaoDestino", instituicaoUsuario)));
		}

		if (titulo.getCodigoPortador() != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike("codigoPortador", titulo.getCodigoPortador(), MatchMode.ANYWHERE));
		}

		if (titulo.getNumeroProtocoloCartorio() != null && titulo.getNumeroProtocoloCartorio() != StringUtils.EMPTY) {
			criteria.createAlias("confirmacao", "confirmacao");
			criteria.add(
			        Restrictions.ilike("confirmacao.numeroProtocoloCartorio", titulo.getNumeroProtocoloCartorio(), MatchMode.ANYWHERE));
		}

		if (titulo.getNumeroTitulo() != null && titulo.getNumeroTitulo() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("numeroTitulo", titulo.getNumeroTitulo(), MatchMode.EXACT));

		if (titulo.getNomeSacadorVendedor() != null && titulo.getNomeSacadorVendedor() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("nomeSacadorVendedor", titulo.getNomeSacadorVendedor(), MatchMode.ANYWHERE));

		if (titulo.getDocumentoSacador() != null && titulo.getDocumentoSacador() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("documentoSacador", titulo.getDocumentoSacador(), MatchMode.ANYWHERE));

		if (titulo.getNomeDevedor() != null && titulo.getNomeDevedor() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE));

		if (titulo.getNumeroIdentificacaoDevedor() != null && titulo.getNumeroIdentificacaoDevedor() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("numeroIdentificacaoDevedor", titulo.getNumeroIdentificacaoDevedor(), MatchMode.ANYWHERE));

		if (titulo.getDataCadastro() != null)
			criteria.add(Restrictions.between("dataCadastro", titulo.getDataCadastro(), titulo.getDataCadastro()));

		if (titulo.getDataOcorrencia() != null)
			criteria.add(Restrictions.between("dataOcorrencia", titulo.getDataOcorrencia(), titulo.getDataEmissaoTitulo()));

		if (titulo.getNossoNumero() != null && titulo.getNossoNumero() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("nossoNumero", titulo.getNossoNumero(), MatchMode.ANYWHERE));

		if (pracaProtesto != null) {
			criteria.createAlias("remessa.cabecalho", "cabecalho");
			criteria.add(Restrictions.ilike("cabecalho.codigoMunicipio", pracaProtesto.getCodigoIBGE()));
		}

		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}

	public List<TituloRemessa> buscarTitulosPorRemessa(Remessa remessa, Instituicao instituicaoCorrente) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.arquivo", "arquivo");

		if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			criteria.add(Restrictions.eq("remessa", remessa));
			criteria.addOrder(Order.asc("codigoPortador")).addOrder(Order.asc("pracaProtesto")).addOrder(Order.asc("nomeDevedor"));
		} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			criteria.createAlias("confirmacao", "confirmacao");
			criteria.add(Restrictions.eq("confirmacao.remessa", remessa));
			criteria.addOrder(Order.asc("codigoPortador")).addOrder(Order.asc("pracaProtesto"))
			        .addOrder(Order.asc("confirmacao.tipoOcorrencia")).addOrder(Order.asc("nomeDevedor"));
		} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			criteria.createAlias("retorno", "retorno");
			criteria.add(Restrictions.eq("retorno.remessa", remessa));
			criteria.addOrder(Order.asc("codigoPortador")).addOrder(Order.asc("pracaProtesto"))
			        .addOrder(Order.asc("retorno.tipoOcorrencia")).addOrder(Order.asc("nomeDevedor"));
		}

		if (!instituicaoCorrente.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("remessa.instituicaoOrigem", instituicaoCorrente),
			        Restrictions.eq("remessa.instituicaoDestino", instituicaoCorrente)));
		}
		return criteria.list();
	}

	@Transactional(readOnly = true)
	public List<TituloRemessa> buscarTitulosPorArquivo(Arquivo arquivo, Instituicao instituicao) {
		Criteria criteria = getCriteria(Remessa.class);
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(
			        Restrictions.or(Restrictions.eq("instituicaoOrigem", instituicao), Restrictions.eq("instituicaoDestino", instituicao)));
		}
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			criteria.add(Restrictions.eq("arquivo", arquivo));
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			criteria.add(Restrictions.eq("arquivoGeradoProBanco", arquivo));
		}
		List<TituloRemessa> listaTitulos = new ArrayList<TituloRemessa>();
		List<Remessa> remessas = criteria.list();
		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(TituloRemessa.class);
			List<TituloRemessa> titulos = new ArrayList<TituloRemessa>();

			if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
				criteriaTitulo.createAlias("remessa", "remessa");
				criteriaTitulo.add(Restrictions.eq("remessa", remessa));
			} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				criteriaTitulo.createAlias("confirmacao", "confirmacao");
				criteriaTitulo.add(Restrictions.eq("confirmacao.remessa", remessa));
			} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				criteriaTitulo.createAlias("retorno", "retorno");
				criteriaTitulo.add(Restrictions.eq("retorno.remessa", remessa));
			}
			if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)
			        || instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CONVENIO)) {
				criteria.addOrder(Order.asc("pracaProtesto")).addOrder(Order.asc("nomeDevedor"));
			} else {
				criteria.addOrder(Order.asc("codigoPortador")).addOrder(Order.asc("nomeDevedor"));
			}

			titulos = criteriaTitulo.list();
			listaTitulos.addAll(titulos);
		}
		return listaTitulos;
	}

	public TituloRemessa buscarTituloPorChave(TituloRemessa titulo) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("dataCadastro", titulo.getDataCadastro()));
		criteria.add(Restrictions.eq("codigoPortador", titulo.getCodigoPortador()));
		criteria.add(Restrictions.eq("nossoNumero", titulo.getNossoNumero()));
		if (titulo.getNumeroTitulo() != null)
			criteria.add(Restrictions.eq("numeroTitulo", titulo.getNumeroTitulo()));

		TituloRemessa tituloRemessa = TituloRemessa.class.cast(criteria.uniqueResult());
		if (tituloRemessa == null)
			throw new InfraException("O Título [ Nosso Número : " + titulo.getNossoNumero() + " ] não existe na CRA !");
		return tituloRemessa;
	}

	public TituloRemessa salvar(Titulo titulo, Transaction transaction) {
		if (TituloRemessa.class.isInstance(titulo)) {
			TituloRemessa tituloRemessa = TituloRemessa.class.cast(titulo);
			return salvarTituloRemessa(tituloRemessa, transaction);
		}
		if (Confirmacao.class.isInstance(titulo)) {
			Confirmacao tituloConfirmacao = Confirmacao.class.cast(titulo);
			return salvarTituloConfirmacao(tituloConfirmacao);
		}
		if (Retorno.class.isInstance(titulo)) {
			Retorno tituloConfirmacao = Retorno.class.cast(titulo);
			return salvarTituloRetorno(tituloConfirmacao, transaction);
		}
		return null;
	}

	private TituloRemessa salvarTituloRetorno(Retorno tituloRetorno, Transaction transaction) {
		TituloRemessa titulo = buscaTituloRetornoSalvo(tituloRetorno);

		if (titulo == null) {
			throw new InfraException("O título [Nosso número =" + tituloRetorno.getNossoNumero() + "] não existe em nossa base de dados.");
		} else {
			if (titulo.getPedidoDesistencia() != null) {
				if (tituloRetorno.getTipoOcorrencia().equals(TipoOcorrencia.PROTESTADO.getConstante())) {
					if (tituloRetorno.getDataOcorrencia()
					        .isAfter(titulo.getPedidoDesistencia().getDesistenciaProtesto().getRemessaDesistenciaProtesto().getCabecalho()
					                .getDataMovimento())
					        || tituloRetorno.getDataOcorrencia().equals(titulo.getPedidoDesistencia().getDesistenciaProtesto()
					                .getRemessaDesistenciaProtesto().getCabecalho().getDataMovimento())) {
						throw new InfraException("PROTESTO INDEVIDO ! O título " + titulo.getNumeroTitulo() + " com o protocolo "
						        + tituloRetorno.getNumeroProtocoloCartorio() + " protestado em "
						        + DataUtil.localDateToString(tituloRetorno.getDataOcorrencia())
						        + " ,já contém um pedido de desistência no arquivo " + titulo.getPedidoDesistencia()
						                .getDesistenciaProtesto().getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()
						        + ". Faça o CANCELAMENTO!");
					}
				}
			}
		}
		try {
			tituloRetorno.setTitulo(titulo);
			titulo.setRetorno(save(tituloRetorno));
			save(titulo);
			logger.info("Retorno salvo com sucesso");
			return titulo;

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			new InfraException("O título [Nosso número =" + tituloRetorno.getNossoNumero() + "] não existe em nossa base de dados.");
		}
		return null;
	}

	public TituloRemessa buscaTituloRetornoSalvo(Retorno tituloRetorno) {
		Integer numeroProtocolo = Integer.parseInt(tituloRetorno.getNumeroProtocoloCartorio().trim());
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.ilike("codigoPortador", tituloRetorno.getCodigoPortador(), MatchMode.EXACT));
		criteria.add(Restrictions.ilike("agenciaCodigoCedente", tituloRetorno.getAgenciaCodigoCedente(), MatchMode.EXACT));
		criteria.add(Restrictions.like("nossoNumero", tituloRetorno.getNossoNumero().trim(), MatchMode.EXACT));
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.add(Restrictions.like("confirmacao.numeroProtocoloCartorio", numeroProtocolo.toString(), MatchMode.ANYWHERE));

		List<TituloRemessa> titulos = criteria.list();
		for (TituloRemessa titulo : titulos) {
			if (titulo.getRetorno() == null) {
				return titulo;
			} else {
				logger.error(new InfraException("Titulo nº" + titulo.getNumeroTitulo() + " já tem retorno"));
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	private TituloRemessa salvarTituloConfirmacao(Confirmacao tituloConfirmacao) {
		TituloRemessa titulo = buscaTituloConfirmacaoSalvo(tituloConfirmacao);

		if (tituloConfirmacao.getTipoOcorrencia() != null) {
			if (tituloConfirmacao.getTipoOcorrencia().equals(DEVOLVIDO)) {
				tituloConfirmacao.setValorGravacaoEletronica(BigDecimal.ZERO);
			} else {
				tituloConfirmacao.setValorGravacaoEletronica(tituloConfirmacao.getRemessa().getInstituicaoDestino().getValorConfirmacao());
			}
		}

		if (titulo == null) {
			throw new InfraException(
			        "O título [Nosso número =" + tituloConfirmacao.getNossoNumero() + "] não existe em nossa base de dados.");
		}
		try {
			tituloConfirmacao.setTitulo(titulo);
			titulo.setConfirmacao(save(tituloConfirmacao));
			save(titulo);
			logger.info("Confirmação salva com sucesso");

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			new InfraException("O título [Nosso número =" + tituloConfirmacao.getNossoNumero() + "] não existe em nossa base de dados.");
		}
		return titulo;
	}

	public TituloRemessa buscaTituloConfirmacaoSalvo(Confirmacao tituloConfirmacao) {
		List<TituloRemessa> titulos = new ArrayList<TituloRemessa>();
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.like("codigoPortador", tituloConfirmacao.getCodigoPortador().trim(), MatchMode.EXACT));
		criteria.add(Restrictions.like("nossoNumero", tituloConfirmacao.getNossoNumero(), MatchMode.EXACT));
		criteria.add(Restrictions.like("numeroTitulo", tituloConfirmacao.getNumeroTitulo(), MatchMode.EXACT));
		criteria.add(Restrictions.like("agenciaCodigoCedente", tituloConfirmacao.getAgenciaCodigoCedente(), MatchMode.EXACT));

		titulos = criteria.list();
		for (TituloRemessa titulo : titulos) {
			if (titulo.getConfirmacao() == null) {
				return titulo;
			} else {
				logger.error(new InfraException("Titulo nº" + titulo.getNumeroTitulo() + " já tem confirmação"));
			}
		}
		return null;
	}

	private TituloRemessa salvarTituloRemessa(TituloRemessa tituloRemessa, Transaction transaction) {
		try {
			tituloRemessa.setDataCadastro(tituloRemessa.getRemessa().getCabecalho().getDataMovimento().toDate());
			return save(tituloRemessa);
		} catch (Exception ex) {
			if (PSQLException.class.isInstance(ex)) {
				logger.error(ex.getMessage(), ex.getCause());
				new InfraException("O Título número: " + tituloRemessa.getNumeroTitulo() + " já existe na base de dados.");
				return null;
			} else {
				transaction.rollback();
				logger.error(ex.getMessage(), ex.getCause());
				throw new InfraException("O Título número: " + tituloRemessa.getNumeroTitulo()
				        + " não pode ser inserido ! O Título já existe na base ou está duplicado no arquivo !");
			}
		}
	}

	public Retorno buscarTituloProtestado(String numeroProtocolo, String codigoIBGE) {
		Integer numProtocolo = Integer.parseInt(numeroProtocolo);
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("titulo", "titulo");
		criteria.createAlias("cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", codigoIBGE));
		criteria.add(Restrictions.ilike("numeroProtocoloCartorio", numProtocolo.toString(), MatchMode.EXACT));
		criteria.setMaxResults(1);
		return Retorno.class.cast(criteria.uniqueResult());
	}

	public TituloRemessa carregarTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("dataCadastro", titulo.getDataCadastro()));
		criteria.add(Restrictions.eq("codigoPortador", titulo.getCodigoPortador()));
		criteria.add(Restrictions.eq("nossoNumero", titulo.getNossoNumero()));

		criteria.setMaxResults(1);
		return TituloRemessa.class.cast(criteria.uniqueResult());
	}

	public TituloRemessa carregarDadosHistoricoTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("dataCadastro", titulo.getDataCadastro()));
		criteria.add(Restrictions.eq("numeroTitulo", titulo.getNumeroTitulo()));
		criteria.add(Restrictions.eq("nossoNumero", titulo.getNossoNumero()));
		criteria.add(Restrictions.eq("codigoPortador", titulo.getCodigoPortador()));
		criteria.add(Restrictions.eq("numeroIdentificacaoDevedor", titulo.getNumeroIdentificacaoDevedor()));
		return TituloRemessa.class.cast(criteria.uniqueResult());
	}

	public TituloRemessa buscarTituloDesistenciaProtesto(String numeroProtocolo, String numeroTitulo, LocalDate dataProtocolagem,
	        BigDecimal valorTitulo) {
		Integer numProtocolo = Integer.parseInt(numeroProtocolo);

		Criteria criteria = getCriteria(Confirmacao.class);
		criteria.add(Restrictions.ilike("numeroTitulo", numeroTitulo, MatchMode.EXACT));
		criteria.add(Restrictions.ilike("numeroProtocoloCartorio", numProtocolo.toString(), MatchMode.EXACT));
		criteria.add(Restrictions.eq("dataProtocolo", dataProtocolagem));
		criteria.createAlias("titulo", "titulo");
		Confirmacao confirmacao = Confirmacao.class.cast(criteria.uniqueResult());

		if (confirmacao == null) {
			return null;
		}

		return confirmacao.getTitulo();
	}
}
