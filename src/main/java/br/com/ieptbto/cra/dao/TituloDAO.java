package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.beans.TituloBean;
import br.com.ieptbto.cra.entidade.Anexo;
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
import br.com.ieptbto.cra.entidade.TituloRetornoCancelamento;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.view.ViewTitulo;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.regra.RegraAgenciaCentralizadoraCodigoCartorio;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.TituloException;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public class TituloDAO extends AbstractBaseDAO {

    @Autowired
    DesistenciaProtestoMediator desistenciaProtestoMediator;

    /**
     * Buscar Confirmação por títulos
     * 
     * @param titulo
     * @return
     */
    public Confirmacao buscarConfirmacaoPorTitulo(TituloRemessa titulo) {
        Criteria criteria = getCriteria(Confirmacao.class);
        criteria.add(Restrictions.eq("titulo", titulo));
        return Confirmacao.class.cast(criteria.uniqueResult());
    }

    /**
     * Buscar Retorno por Título
     * 
     * @param titulo
     * @return
     */
    public Retorno buscarRetornoPorTitulo(TituloRemessa titulo) {
        Criteria criteria = getCriteria(Retorno.class);
        criteria.add(Restrictions.eq("titulo", titulo));
        return Retorno.class.cast(criteria.uniqueResult());
    }

    /**
     * Busca o título principal para outros devedores
     * 
     * @param confirmacao
     * @return
     */
    public Retorno buscarRetornoTituloDevedorPrincipal(Confirmacao confirmacao) {
        Integer numeroProtocolo = Integer.parseInt(confirmacao.getNumeroProtocoloCartorio().trim());

        Criteria criteria = getCriteria(Retorno.class);
        criteria.createAlias("remessa", "remessa");
        criteria.createAlias("remessa.cabecalho", "cabecalho");
        criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", confirmacao.getRemessa().getCabecalho().getCodigoMunicipio()));
        criteria.add(Restrictions.ilike("codigoPortador", confirmacao.getCodigoPortador(), MatchMode.EXACT));
        criteria.add(Restrictions.like("nossoNumero", confirmacao.getNossoNumero().trim(), MatchMode.EXACT));
        criteria.add(Restrictions.like("numeroProtocoloCartorio", numeroProtocolo.toString(), MatchMode.EXACT));
        criteria.setMaxResults(1);
        return Retorno.class.cast(criteria.uniqueResult());
    }

    public List<TituloRemessa> buscarTitulos(Usuario usuario, LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
            Instituicao bancoConvenio, Instituicao cartorio, TituloBean titulo) {
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
            criteria.add(Restrictions.like("nossoNumero", titulo.getNossoNumero(), MatchMode.ANYWHERE));
        }
        if (titulo.getNumeroProtocoloCartorio() != null && titulo.getNumeroProtocoloCartorio() != StringUtils.EMPTY) {
            criteria.createAlias("confirmacao", "confirmacao");
            criteria.add(Restrictions.like("confirmacao.numeroProtocoloCartorio", titulo.getNumeroProtocoloCartorio(), MatchMode.EXACT));
        }

        if (titulo.getNumeroTitulo() != null && titulo.getNumeroTitulo() != StringUtils.EMPTY)
            criteria.add(Restrictions.ilike("numeroTitulo", titulo.getNumeroTitulo(), MatchMode.EXACT));

        if (titulo.getNomeCredor() != null && titulo.getNomeCredor() != StringUtils.EMPTY)
            criteria.add(Restrictions.ilike("nomeSacadorVendedor", titulo.getNomeCredor(), MatchMode.ANYWHERE));

        if (titulo.getDocumentoCredor() != null && titulo.getDocumentoCredor() != StringUtils.EMPTY)
            criteria.add(Restrictions.eq("documentoSacador", titulo.getDocumentoCredor()));

        if (titulo.getNomeDevedor() != null && titulo.getNomeDevedor() != StringUtils.EMPTY)
            criteria.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE));

        if (titulo.getNumeroIdentificacaoDevedor() != null && titulo.getNumeroIdentificacaoDevedor() != StringUtils.EMPTY)
            criteria.add(Restrictions.eq("numeroIdentificacaoDevedor", titulo.getNumeroIdentificacaoDevedor()));

        if (dataInicio != null) {
            criteria.add(Restrictions.sqlRestriction("DATE(data_cadastro) >= ?", dataInicio.toDate(),
                    org.hibernate.type.StandardBasicTypes.DATE));
            criteria.add(
                    Restrictions.sqlRestriction("DATE(data_cadastro) <= ?", dataFim.toDate(), org.hibernate.type.StandardBasicTypes.DATE));
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

    public List<ViewTitulo> consultarViewTitulosPorIdRemessa(Integer id) {
        Query query = getSession().getNamedQuery("findTitulosPorIdRemessaRemessa");
        query.setParameter("id", id);
        return query.list();
    }

    public List<ViewTitulo> consultarViewTitulosConfirmacaoPorIdRemessa(Integer id) {
        Query query = getSession().getNamedQuery("findTitulosPorIdRemessaConfirmacao");
        query.setParameter("id", id);
        return query.list();
    }

    public List<ViewTitulo> consultarViewTitulosRetornoPorIdRemessa(Integer id) {
        Query query = getSession().getNamedQuery("findTitulosPorIdRemessaRetorno");
        query.setParameter("id", id);
        return query.list();
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

    public TituloRemessa salvar(Instituicao instituicao, Titulo titulo, List<Exception> erros, Transaction transaction) {
        if (TituloRemessa.class.isInstance(titulo)) {
            TituloRemessa tituloRemessa = TituloRemessa.class.cast(titulo);
            return salvarTituloRemessa(tituloRemessa, erros);
        }
        if (Confirmacao.class.isInstance(titulo)) {
            Confirmacao tituloConfirmacao = Confirmacao.class.cast(titulo);
            return salvarTituloConfirmacao(instituicao, tituloConfirmacao, erros);
        }
        if (Retorno.class.isInstance(titulo)) {
            Retorno tituloRetorno = Retorno.class.cast(titulo);
            return salvarTituloRetorno(instituicao, tituloRetorno, erros);
        }
        return null;
    }

    /**
     * Salvar título Remessa
     * 
     * 
     * @param tituloRemessa
     * @param erros
     * @return
     */
    private TituloRemessa salvarTituloRemessa(TituloRemessa tituloRemessa, List<Exception> erros) {

        try {
            tituloRemessa.setDataCadastro(tituloRemessa.getRemessa().getCabecalho().getDataMovimento().toDate());
            TituloRemessa tituloSalvo = save(tituloRemessa);

            if (tituloRemessa.getAnexos() != null) {
                for (Anexo anexo : tituloRemessa.getAnexos()) {

                    anexo.setTitulo(tituloSalvo);
                    save(anexo);
                }
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
    private TituloRemessa salvarTituloConfirmacao(Instituicao instituicao, Confirmacao tituloConfirmacao, List<Exception> erros) {
        TituloRemessa titulo = buscarTituloConfirmacaoSalvo(instituicao, tituloConfirmacao, erros);

        try {
            RegraAgenciaCentralizadoraCodigoCartorio banco = RegraAgenciaCentralizadoraCodigoCartorio
                    .getBanco(tituloConfirmacao.getCodigoPortador());
            if (banco != null) {
                if (banco.equals(RegraAgenciaCentralizadoraCodigoCartorio.ITAU)) {
                    Integer codigoCartorio = banco.getCodigoCartorio(titulo.getRemessa().getCabecalho().getCodigoMunicipio());
                    if (codigoCartorio != 0) {
                        tituloConfirmacao.setCodigoCartorio(codigoCartorio);
                    } else {
                        Integer numeroProtocolo = Integer.valueOf(tituloConfirmacao.getNumeroProtocoloCartorio().trim());
                        erros.add(new TituloException(CodigoErro.CARTORIO_CODIGO_CARTORIO_APRESENTANTE_INVALIDO,
                                tituloConfirmacao.getNossoNumero(), numeroProtocolo, tituloConfirmacao.getNumeroSequencialArquivo()));
                    }
                } else {
                    tituloConfirmacao.setCodigoCartorio(banco.getCodigoCartorio());
                }
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
    private TituloRemessa salvarTituloRetorno(Instituicao instituicao, Retorno tituloRetorno, List<Exception> erros) {
        TituloRemessa titulo = buscarTituloRetornoSalvo(instituicao, tituloRetorno, erros);

        try {
            RegraAgenciaCentralizadoraCodigoCartorio banco = RegraAgenciaCentralizadoraCodigoCartorio
                    .getBanco(tituloRetorno.getCodigoPortador());
            if (banco != null) {
                if (banco.equals(RegraAgenciaCentralizadoraCodigoCartorio.ITAU)) {
                    Integer codigoCartorio = banco.getCodigoCartorio(titulo.getRemessa().getCabecalho().getCodigoMunicipio());
                    if (codigoCartorio != 0) {
                        tituloRetorno.setCodigoCartorio(codigoCartorio);
                    } else {
                        Integer numeroProtocolo = Integer.valueOf(tituloRetorno.getNumeroProtocoloCartorio().trim());
                        erros.add(new TituloException(CodigoErro.CARTORIO_CODIGO_CARTORIO_APRESENTANTE_INVALIDO,
                                tituloRetorno.getNossoNumero(), numeroProtocolo, tituloRetorno.getNumeroSequencialArquivo()));
                    }
                } else {
                    tituloRetorno.setCodigoCartorio(banco.getCodigoCartorio());
                }
            }

            // Se o título for encontrado, ele é vinculado com o retorno e salvo
            // na base
            if (titulo != null) {
                verificarProtestoIndevidoERetirado(titulo, tituloRetorno, erros);
                salvarTitulo(tituloRetorno, titulo);
            }
            // Se o título remessa não for encontrado, o título retorno
            // cancelamento é salvo na base
            else {
                salvarTituloRetornoCancelamento(tituloRetorno, null);
                logger.info("Título remessa não encontrado, porém o retorno cancelamento foi salvo.");
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return titulo;
    }

    /**
     * Verifica se o título é um cancelamendo e salva como
     * TituloRetornoCancelamento
     * 
     * @param tituloRetorno
     * @return
     */
    private void salvarTitulo(Retorno tituloRetorno, TituloRemessa titulo) {
        tituloRetorno.setTitulo(titulo);
        if (isTituloRetornoCancelamento(tituloRetorno)) {
            salvarTituloRetornoCancelamento(tituloRetorno, titulo);
        } else {
            titulo.setRetorno(save(tituloRetorno));
            save(titulo);
        }
    }

    /**
     * Vincula um retorno cancelamento ao um título e salva na base de dados
     * 
     * @param tituloRetorno
     * @param tituloRemessa
     * @return
     */
    private Retorno salvarTituloRetornoCancelamento(Retorno tituloRetorno, TituloRemessa tituloRemessa) {
        TituloRetornoCancelamento tituloRetornoCancelamento = new TituloRetornoCancelamento();
        tituloRetornoCancelamento.parse(tituloRetorno);
        tituloRetornoCancelamento = save(tituloRetornoCancelamento);
        tituloRetorno.setId(tituloRetornoCancelamento.getId());
        return tituloRetorno;
    }

    /**
     * verifca se o título é um retorno de cancelamento, campo tipoOrigem = A
     * 
     * @param tituloRetorno
     * 
     * @return boolean true se for retorno de cancelamento e false para retorno
     *         normal
     * 
     */
    private boolean isTituloRetornoCancelamento(Retorno tituloRetorno) {
        if (StringUtils.isBlank(tituloRetorno.getTipoOcorrencia())) {
            return false;
        }
        return TipoOcorrencia.PROTESTO_DO_BANCO_CANCELADO.constante.equals(tituloRetorno.getTipoOcorrencia());
    }

    private void verificarProtestoIndevidoERetirado(TituloRemessa titulo, Retorno tituloRetorno, List<Exception> erros) {
        TipoOcorrencia tipoOcorrencia = TipoOcorrencia.getTipoOcorrencia(tituloRetorno.getTipoOcorrencia());

        if (!titulo.getRemessa().getInstituicaoOrigem().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CONVENIO)) {
            if (TipoOcorrencia.PROTESTADO.equals(tipoOcorrencia) || TipoOcorrencia.RETIRADO.equals(tipoOcorrencia)) {
                List<PedidoDesistencia> pedidosDesistencia = desistenciaProtestoMediator.buscarPedidosDesistenciaProtestoPorTitulo(titulo);

                if (TipoOcorrencia.PROTESTADO.equals(tipoOcorrencia)) {
                    for (PedidoDesistencia pedido : pedidosDesistencia) {
                        LocalDate dataOcorrenciaProtesto = tituloRetorno.getDataOcorrencia();
                        LocalDate dataEnvioDesistencia = pedido.getDesistenciaProtesto().getRemessaDesistenciaProtesto().getCabecalho()
                                .getDataMovimento();
                        if (dataOcorrenciaProtesto.isAfter(dataEnvioDesistencia) || dataOcorrenciaProtesto.equals(dataEnvioDesistencia)) {
                            Integer numeroProtocolo = Integer.parseInt(tituloRetorno.getNumeroProtocoloCartorio().trim());
                            erros.add(new TituloException(CodigoErro.CARTORIO_PROTESTO_INDEVIDO, tituloRetorno.getNossoNumero(),
                                    numeroProtocolo, tituloRetorno.getNumeroSequencialArquivo()));
                        }
                    }
                }

                if (TipoOcorrencia.RETIRADO.equals(tipoOcorrencia)) {
                    if (pedidosDesistencia.isEmpty()) {
                        titulo.setTipoOcorrencia(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante());
                        titulo.setCodigoIrregularidade(CodigoIrregularidade.IRREGULARIDADE_22.getCodigoIrregularidade());
                    }
                }
            }
        }

    }

    /**
     * Buscar Título para víncular confirmacao
     * 
     * @param tituloConfirmacao
     * @param erros
     * @return
     */
    public TituloRemessa buscarTituloConfirmacaoSalvo(Instituicao instituicao, Confirmacao tituloConfirmacao, List<Exception> erros) {
        Criteria criteria = getCriteria(TituloRemessa.class);
        criteria.createAlias("remessa", "remessa");
        criteria.add(Restrictions.eq("remessa.instituicaoDestino", instituicao));
        criteria.add(Restrictions.like("codigoPortador", tituloConfirmacao.getCodigoPortador().trim(), MatchMode.EXACT));
        criteria.add(Restrictions.like("nossoNumero", tituloConfirmacao.getNossoNumero(), MatchMode.EXACT));
        criteria.add(Restrictions.like("numeroTitulo", tituloConfirmacao.getNumeroTitulo().trim(), MatchMode.EXACT));
        criteria.add(Restrictions.eq("numeroControleDevedor", tituloConfirmacao.getNumeroControleDevedor()));

        List<TituloRemessa> titulos = criteria.list();
        for (TituloRemessa titulo : titulos) {
            if (titulo.getConfirmacao() == null) {
                return titulo;
            }
        }

        Integer numeroProtocolo = Integer.parseInt(tituloConfirmacao.getNumeroProtocoloCartorio().trim());
        if (!titulos.isEmpty()) {
            erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_CONFIRMACAO_JA_ENVIADO, tituloConfirmacao.getNossoNumero(),
                    numeroProtocolo, tituloConfirmacao.getNumeroSequencialArquivo()));
        } else {
            erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_NAO_ENCONTRADO, tituloConfirmacao.getNossoNumero(), numeroProtocolo,
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
    public TituloRemessa buscarTituloRetornoSalvo(Instituicao instituicao, Retorno tituloRetorno, List<Exception> erros) {
        Integer numeroProtocolo = Integer.parseInt(tituloRetorno.getNumeroProtocoloCartorio().trim());
        Criteria criteria = getCriteria(TituloRemessa.class);
        criteria.createAlias("remessa", "remessa");
        criteria.add(Restrictions.eq("remessa.instituicaoDestino", instituicao));
        criteria.add(Restrictions.ilike("codigoPortador", tituloRetorno.getCodigoPortador(), MatchMode.EXACT));
        criteria.add(Restrictions.like("nossoNumero", tituloRetorno.getNossoNumero().trim(), MatchMode.EXACT));
        criteria.createAlias("confirmacao", "confirmacao");
        criteria.add(Restrictions.like("confirmacao.numeroProtocoloCartorio", numeroProtocolo.toString(), MatchMode.EXACT));

        List<TituloRemessa> titulos = criteria.list();
        for (TituloRemessa titulo : titulos) {
            if (titulo.isDevedorPrincipal()) {
                if (TipoOcorrencia.PROTESTO_DO_BANCO_CANCELADO.constante == tituloRetorno.getTipoOcorrencia()
                        || titulo.getRetorno() == null) {
                    return titulo;
                }
            }
        }
        if (!titulos.isEmpty()) {
            erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_RETORNO_JA_ENVIADO, tituloRetorno.getNossoNumero(), numeroProtocolo,
                    tituloRetorno.getNumeroSequencialArquivo()));
        } else if (TipoOcorrencia.PROTESTO_DO_BANCO_CANCELADO.constante != tituloRetorno.getTipoOcorrencia()) {
            erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_NAO_ENCONTRADO, tituloRetorno.getNossoNumero(), numeroProtocolo,
                    tituloRetorno.getNumeroSequencialArquivo()));
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
        criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PROTESTADO.getConstante()));
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

		Query query = getSession().createQuery("SELECT c FROM Confirmacao c JOIN c.remessa r JOIN r.cabecalho ca "
				+ "WHERE ca.codigoMunicipio = :codMunicipio "
				+ "AND c.numeroProtocoloCartorio = :protocolo "
				+ "AND c.numeroTitulo = :numeroTitulo"
		);
		query.setString("codMunicipio", pedidoDesistenciaCancelamento.getDesistenciaProtesto().getCabecalhoCartorio().getCodigoMunicipio());
		query.setString("protocolo", Long.toString(numProtocolo));
		query.setString("numeroTitulo", pedidoDesistenciaCancelamento.getNumeroTitulo().trim());
		List<Confirmacao> results = query.list();
		for (Confirmacao confirmacao : results) {
			if (confirmacao.getTitulo().getNumeroControleDevedor() == 1) {
				return confirmacao.getTitulo();
			}
		}
		return null;
	}

    public TituloRemessa buscarTituloCancelamentoProtesto(PedidoCancelamento pedido) {
    	Long numProtocolo = Long.parseLong(pedido.getNumeroProtocolo());

		Query query = getSession().createQuery("SELECT c FROM Confirmacao c JOIN c.remessa r JOIN r.cabecalho ca "
				+ "WHERE ca.codigoMunicipio = :codMunicipio "
				+ "AND c.numeroProtocoloCartorio = :protocolo "
				+ "AND c.numeroTitulo = :numeroTitulo"
		);
		query.setString("codMunicipio", pedido.getCancelamentoProtesto().getCabecalhoCartorio().getCodigoMunicipio());
		query.setString("protocolo", Long.toString(numProtocolo));
		query.setString("numeroTitulo", pedido.getNumeroTitulo().trim());
		List<Confirmacao> results = query.list();
		for (Confirmacao confirmacao : results) {
			if (confirmacao.getTitulo().getNumeroControleDevedor() == 1) {
				return confirmacao.getTitulo();
			}
		}
		return null;
    }

    public TituloRemessa buscarTituloAutorizacaoCancelamento(PedidoAutorizacaoCancelamento pedido) {
    	Long numProtocolo = Long.parseLong(pedido.getNumeroProtocolo());

		Query query = getSession().createQuery("SELECT c FROM Confirmacao c JOIN c.remessa r JOIN r.cabecalho ca "
				+ "WHERE ca.codigoMunicipio = :codMunicipio "
				+ "AND c.numeroProtocoloCartorio = :protocolo "
				+ "AND c.numeroTitulo = :numeroTitulo"
		);
		query.setString("codMunicipio", pedido.getAutorizacaoCancelamento().getCabecalhoCartorio().getCodigoMunicipio());
		query.setString("protocolo", Long.toString(numProtocolo));
		query.setString("numeroTitulo", pedido.getNumeroTitulo().trim());
		List<Confirmacao> results = query.list();
		for (Confirmacao confirmacao : results) {
			if (confirmacao.getTitulo().getNumeroControleDevedor() == 1) {
				return confirmacao.getTitulo();
			}
		}
		return null;
    }

    public List<TituloRemessa> carregarTitulos(Remessa remessa) {
        Criteria criteria = getCriteria(TituloRemessa.class);

        if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.REMESSA)) {
            criteria.add(Restrictions.eq("remessa", remessa));
        } else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.CONFIRMACAO)) {
            criteria.createAlias("confirmacao", "confirmacao");
            criteria.add(Restrictions.eq("confirmacao.remessa", remessa));
        } else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.RETORNO)) {
            criteria.createAlias("retorno", "retorno");
            criteria.add(Restrictions.eq("retorno.remessa", remessa));
        }
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

    public Anexo buscarAnexo(TituloRemessa tituloRemessa) {
        Criteria criteria = getCriteria(Anexo.class);
        criteria.setMaxResults(1);
        criteria.add(Restrictions.eq("titulo", tituloRemessa));
        return Anexo.class.cast(criteria.uniqueResult());
    }
}