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
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.BancoAgenciaCentralizadoraCodigoCartorio;
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

    public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Municipio pracaProtesto, Usuario user) {
	Instituicao instituicaoUsuario = user.getInstituicao();

	Criteria criteria = getCriteria(TituloRemessa.class);
	criteria.createAlias("remessa", "remessa");
	if (!instituicaoUsuario.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
	    criteria.add(Restrictions.or(Restrictions.eq("remessa.instituicaoOrigem", instituicaoUsuario), Restrictions.eq("remessa.instituicaoDestino", instituicaoUsuario)));
	}

	if (titulo.getCodigoPortador() != StringUtils.EMPTY) {
	    criteria.add(Restrictions.ilike("codigoPortador", titulo.getCodigoPortador(), MatchMode.ANYWHERE));
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
	    criteria.add(Restrictions.ilike("documentoSacador", titulo.getDocumentoSacador(), MatchMode.ANYWHERE));

	if (titulo.getNomeDevedor() != null && titulo.getNomeDevedor() != StringUtils.EMPTY)
	    criteria.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE));

	if (titulo.getNumeroIdentificacaoDevedor() != null
		&& titulo.getNumeroIdentificacaoDevedor() != StringUtils.EMPTY)
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

    public TituloRemessa buscarTituloPorChave(TituloRemessa titulo) {
	Criteria criteria = getCriteria(TituloRemessa.class);
	criteria.add(Restrictions.eq("dataCadastro", titulo.getDataCadastro()));
	criteria.add(Restrictions.eq("codigoPortador", titulo.getCodigoPortador()));
	criteria.add(Restrictions.eq("nossoNumero", titulo.getNossoNumero()));
	if (titulo.getNumeroTitulo() != null) {
	    criteria.add(Restrictions.eq("numeroTitulo", titulo.getNumeroTitulo()));
	}
	TituloRemessa tituloRemessa = TituloRemessa.class.cast(criteria.uniqueResult());
	if (tituloRemessa == null)
	    throw new InfraException("O Título [ Nosso Número : " + titulo.getNossoNumero() + " ] não existe na CRA !");
	return tituloRemessa;
    }

    public List<Titulo> carregarTitulosGenerico(Remessa remessa) {
	Criteria criteriaTitulo = getCriteria(Titulo.class);
	criteriaTitulo.add(Restrictions.eq("remessa", remessa));
	return criteriaTitulo.list();
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
	    throw new InfraException("O título com o Nosso Número " + tituloRetorno.getNossoNumero() + " e Protocolo "
		    + tituloRetorno.getNumeroProtocoloCartorio()
		    + ", não foi localizado na CRA. Verifique se já enviou a CONFIRMAÇÃO !");
	}
	if (titulo.getPedidosDesistencia() != null) {
	    for (PedidoDesistencia pedido : titulo.getPedidosDesistencia()) {

		LocalDate dataOcorrenciaRetorno = tituloRetorno.getDataOcorrencia();
		LocalDate dataMovimentoDesistencia = pedido.getDesistenciaProtesto().getRemessaDesistenciaProtesto().getCabecalho().getDataMovimento();
		if (tituloRetorno.getTipoOcorrencia().equals(TipoOcorrencia.PROTESTADO.getConstante())) {
		    if (dataOcorrenciaRetorno.isAfter(dataMovimentoDesistencia)
			    || dataOcorrenciaRetorno.equals(dataMovimentoDesistencia)) {
			throw new InfraException("PROTESTO INDEVIDO! O título com o protocolo "
				+ tituloRetorno.getNumeroProtocoloCartorio() + ", protestado em "
				+ DataUtil.localDateToString(tituloRetorno.getDataOcorrencia())
				+ ", já contém um pedido de desistência. Faça o CANCELAMENTO!");
		    }
		}
	    }
	}
	if (tituloRetorno.getTipoOcorrencia() != null) {
	    if (tituloRetorno.getNumeroProtocoloCartorio() != null) {
		if (!StringUtils.isBlank(tituloRetorno.getNumeroProtocoloCartorio().trim())) {
		    Integer numeroProtocolo = Integer.valueOf(tituloRetorno.getNumeroProtocoloCartorio().trim());
		    tituloRetorno.setNumeroProtocoloCartorio(numeroProtocolo.toString());
		    if (numeroProtocolo.equals(0)) {
			if (!tituloRetorno.getTipoOcorrencia().equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
			    throw new InfraException("O título com o Nosso Número " + tituloRetorno.getNossoNumero()
				    + ", está com o número de protocolo vazio e está sem o código de ocorrência!");
			}
			if (tituloRetorno.getCodigoIrregularidade() != null) {
			    if (StringUtils.isBlank(tituloRetorno.getCodigoIrregularidade().trim())
				    || tituloRetorno.getCodigoIrregularidade().equals("00")) {
				throw new InfraException("O título [Nosso número =" + tituloRetorno.getNossoNumero()
					+ "] foi devolvido e não contém o código de irregularidade!");
			    }
			}
		    }
		}
	    }
	}
	BancoAgenciaCentralizadoraCodigoCartorio banco = BancoAgenciaCentralizadoraCodigoCartorio.getBancoAgenciaCodigoCartorio(tituloRetorno.getCodigoPortador());
	if (banco != null) {
	    tituloRetorno.setCodigoCartorio(banco.getCodigoCartorio());
	}

	try {
	    tituloRetorno.setTitulo(titulo);
	    titulo.setRetorno(save(tituloRetorno));
	    save(titulo);
	    return titulo;

	} catch (Exception ex) {
	    logger.error(ex.getMessage(), ex.getCause());
	    new InfraException("Não foi possível salvar o título do arquivo de retorno! Entre em contato com a CRA !");
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
	criteria.add(Restrictions.like("confirmacao.numeroProtocoloCartorio", numeroProtocolo.toString(), MatchMode.EXACT));

	List<TituloRemessa> titulos = criteria.list();
	for (TituloRemessa titulo : titulos) {
	    if (titulo.getRetorno() == null) {
		return titulo;
	    } else {
		logger.error(new InfraException("Titulo nº" + titulo.getNumeroTitulo() + " já tem retorno!"));
		throw new InfraException("O título com o \b Nosso Número " + tituloRetorno.getNossoNumero()
			+ "\b e Protocolo " + tituloRetorno.getNumeroProtocoloCartorio()
			+ ", já foi enviado em outro arquivo de retorno!");
	    }
	}
	return null;
    }

    private TituloRemessa salvarTituloConfirmacao(Confirmacao tituloConfirmacao) {
	TituloRemessa titulo = buscaTituloConfirmacaoSalvo(tituloConfirmacao);

	if (titulo == null) {
	    throw new InfraException("O título [Nosso número =" + tituloConfirmacao.getNossoNumero()
		    + "] não existe em nossa base de dados.");
	}
	if (tituloConfirmacao.getTipoOcorrencia() != null) {
	    if (tituloConfirmacao.getTipoOcorrencia().equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())
		    && tituloConfirmacao.getCodigoIrregularidade().equals("00")) {
		throw new InfraException("O título [Nosso número =" + tituloConfirmacao.getNossoNumero()
			+ "] foi devolvido e não contém código irregularidade!");
	    }
	    if (tituloConfirmacao.getNumeroProtocoloCartorio() != null) {
		if (!StringUtils.isBlank(tituloConfirmacao.getNumeroProtocoloCartorio().trim())) {
		    Integer numeroProtocolo = Integer.valueOf(tituloConfirmacao.getNumeroProtocoloCartorio().trim());
		    tituloConfirmacao.setNumeroProtocoloCartorio(numeroProtocolo.toString());
		    if (numeroProtocolo.equals(0)) {
			if (!tituloConfirmacao.getTipoOcorrencia().equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
			    throw new InfraException("O título [Nosso número =" + tituloConfirmacao.getNossoNumero()
				    + "] está com o número de protocolo vazio e não está com a ocorrência de devolvido!");
			}
			if (tituloConfirmacao.getCodigoIrregularidade() != null) {
			    if (StringUtils.isBlank(tituloConfirmacao.getCodigoIrregularidade().trim())
				    || tituloConfirmacao.getCodigoIrregularidade().equals("00")) {
				throw new InfraException("O título [Nosso número =" + tituloConfirmacao.getNossoNumero()
					+ "] foi devolvido e não contém o código da irregularidade!");
			    }
			}
		    }
		}
	    }

	    if (!tituloConfirmacao.getTipoOcorrencia().equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
		tituloConfirmacao.setValorGravacaoEletronica(titulo.getRemessa().getInstituicaoOrigem().getValorConfirmacao());
	    }
	}
	BancoAgenciaCentralizadoraCodigoCartorio banco = BancoAgenciaCentralizadoraCodigoCartorio.getBancoAgenciaCodigoCartorio(tituloConfirmacao.getCodigoPortador());
	if (banco != null) {
	    tituloConfirmacao.setCodigoCartorio(banco.getCodigoCartorio());
	}

	try {
	    tituloConfirmacao.setTitulo(titulo);
	    titulo.setConfirmacao(save(tituloConfirmacao));
	    save(titulo);

	} catch (Exception ex) {
	    logger.error(ex.getMessage(), ex.getCause());
	    new InfraException("O título [Nosso número =" + tituloConfirmacao.getNossoNumero()
		    + "] não existe em nossa base de dados.");
	}
	return titulo;
    }

    public TituloRemessa buscaTituloConfirmacaoSalvo(Confirmacao tituloConfirmacao) {
	Criteria criteria = getCriteria(TituloRemessa.class);
	criteria.add(Restrictions.like("codigoPortador", tituloConfirmacao.getCodigoPortador().trim(), MatchMode.EXACT));
	criteria.add(Restrictions.like("nossoNumero", tituloConfirmacao.getNossoNumero(), MatchMode.EXACT));
	criteria.add(Restrictions.like("numeroTitulo", tituloConfirmacao.getNumeroTitulo(), MatchMode.EXACT));
	criteria.add(Restrictions.like("agenciaCodigoCedente", tituloConfirmacao.getAgenciaCodigoCedente(), MatchMode.EXACT));

	List<TituloRemessa> titulos = criteria.list();
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
	    TituloRemessa tituloSalvo = save(tituloRemessa);

	    if (tituloRemessa.getAnexo() != null) {
		tituloRemessa.getAnexo().setTitulo(tituloSalvo);
		save(tituloRemessa.getAnexo());
	    }
	    return save(tituloRemessa);
	} catch (Exception ex) {
	    if (PSQLException.class.isInstance(ex)) {
		logger.error(ex.getMessage(), ex.getCause());
		new InfraException("O Título número: " + tituloRemessa.getNumeroTitulo()
			+ " já existe na base de dados.");
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

    public TituloRemessa buscarTituloDesistenciaProtesto(PedidoDesistencia pedidoDesistenciaCancelamento) {
	Integer numProtocolo = Integer.parseInt(pedidoDesistenciaCancelamento.getNumeroProtocolo());

	Criteria criteria = getCriteria(Confirmacao.class);
	criteria.createAlias("titulo", "titulo");
	criteria.createAlias("remessa", "remessa");
	criteria.createAlias("remessa.cabecalho", "cabecalho");
	criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", pedidoDesistenciaCancelamento.getDesistenciaProtesto().getCabecalhoCartorio().getCodigoMunicipio()));
	criteria.add(Restrictions.ilike("numeroTitulo", pedidoDesistenciaCancelamento.getNumeroTitulo(), MatchMode.EXACT));
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
	criteria.add(Restrictions.ilike("numeroTitulo", pedido.getNumeroTitulo(), MatchMode.EXACT));
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
	criteria.add(Restrictions.ilike("numeroTitulo", pedido.getNumeroTitulo(), MatchMode.EXACT));
	criteria.add(Restrictions.ilike("numeroProtocoloCartorio", numProtocolo.toString(), MatchMode.EXACT));
	criteria.add(Restrictions.eq("dataProtocolo", pedido.getDataProtocolagem()));
	Confirmacao confirmacao = Confirmacao.class.cast(criteria.uniqueResult());

	if (confirmacao == null) {
	    return null;
	}
	return confirmacao.getTitulo();
    }
}
