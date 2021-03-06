package br.com.ieptbto.cra.dao;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.DesistenciaCancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class AutorizacaoCancelamentoDAO extends AbstractBaseDAO {

	@Autowired
	private TituloDAO tituloDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;

	@SuppressWarnings("unchecked")
	public List<PedidoAutorizacaoCancelamento> buscarPedidosAutorizacaoCancelamentoPorTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(PedidoAutorizacaoCancelamento.class);
		criteria.add(Restrictions.eq("titulo", titulo));
		return criteria.list();
	}

	public Arquivo salvarAutorizacao(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		Arquivo arquivoSalvo = new Arquivo();
		Transaction transaction = getSession().beginTransaction();

		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString()));

			arquivoSalvo = save(arquivo);
			List<PedidoAutorizacaoCancelamento> pedidosAutorizacaoErros = new ArrayList<PedidoAutorizacaoCancelamento>();
			List<AutorizacaoCancelamento> autorizacoesCancelamentos = new ArrayList<AutorizacaoCancelamento>();
			BigDecimal valorTotalAutorizacao = BigDecimal.ZERO;
			int totalAutorizacaoArquivo = 0;

			for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
				List<PedidoAutorizacaoCancelamento> pedidosAutorizacao = new ArrayList<PedidoAutorizacaoCancelamento>();
				int quantidadeAutorizacaoCartorio = 0;
				ac.setRemessaAutorizacaoCancelamento(arquivo.getRemessaAutorizacao());
				ac.setDownload(false);

				for (PedidoAutorizacaoCancelamento pedido : ac.getAutorizacoesCancelamentos()) {
					pedido.setAutorizacaoCancelamento(ac);
					pedido.setTitulo(tituloDAO.buscarTituloAutorizacaoCancelamento(pedido));

					if (pedido.getTitulo() != null) {
						pedido.setCodigoErroProcessamento(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO);
						pedidosAutorizacao.add(pedido);
						quantidadeAutorizacaoCartorio = quantidadeAutorizacaoCartorio + 1;
						valorTotalAutorizacao = valorTotalAutorizacao.add(pedido.getValorTitulo());
						totalAutorizacaoArquivo = totalAutorizacaoArquivo + 1;
					} else if (pedido.getDataProtocolagem().isAfter(DataUtil.stringToLocalDate("dd/MM/yyyy", "01/12/2015"))
							|| pedido.getDataProtocolagem().equals(DataUtil.stringToLocalDate("dd/MM/yyyy", "01/12/2015"))) {
						pedido.setCodigoErroProcessamento(CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO);
						pedidosAutorizacaoErros.add(pedido);
					} else {
						pedido.setCodigoErroProcessamento(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO);
						pedidosAutorizacao.add(pedido);
						quantidadeAutorizacaoCartorio = quantidadeAutorizacaoCartorio + 1;
						valorTotalAutorizacao = valorTotalAutorizacao.add(pedido.getValorTitulo());
						totalAutorizacaoArquivo = totalAutorizacaoArquivo + 1;
					}
				}

				if (pedidosAutorizacaoErros.isEmpty()) {
					ac.getCabecalhoCartorio().setQuantidadeDesistencia(quantidadeAutorizacaoCartorio);
					ac.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(quantidadeAutorizacaoCartorio * 2);
					ac.setAutorizacoesCancelamentos(pedidosAutorizacao);
					autorizacoesCancelamentos.add(ac);
				} else {
					String descricao = StringUtils.EMPTY;
					String codigoMunicipio = StringUtils.EMPTY;
					for (PedidoAutorizacaoCancelamento pedidoAutorizacao : pedidosAutorizacaoErros) {
						descricao = descricao + "Protocolo Inválido (" + pedidoAutorizacao.getNumeroProtocolo() + ").";
						codigoMunicipio = pedidoAutorizacao.getAutorizacaoCancelamento().getCabecalhoCartorio().getCodigoMunicipio();
					}
					erros.add(
							new DesistenciaCancelamentoException(descricao, codigoMunicipio, CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO));
					pedidosAutorizacaoErros.clear();
				}
			}
			arquivo.getRemessaAutorizacao().getCabecalho().setQuantidadeDesistencia(totalAutorizacaoArquivo);
			arquivo.getRemessaAutorizacao().getCabecalho().setQuantidadeRegistro(totalAutorizacaoArquivo);
			arquivo.getRemessaAutorizacao().getRodape().setQuantidadeDesistencia(totalAutorizacaoArquivo);
			arquivo.getRemessaAutorizacao().getRodape().setSomatorioValorTitulo(valorTotalAutorizacao);
			arquivo.getRemessaAutorizacao().setAutorizacaoCancelamento(autorizacoesCancelamentos);
			arquivo.getRemessaAutorizacao().setCabecalho(save(arquivo.getRemessaAutorizacao().getCabecalho()));
			arquivo.getRemessaAutorizacao().setRodape(save(arquivo.getRemessaAutorizacao().getRodape()));
			save(arquivo.getRemessaAutorizacao());

			for (AutorizacaoCancelamento ac : autorizacoesCancelamentos) {
				ac.getCabecalhoCartorio().setQuantidadeDesistencia(ac.getAutorizacoesCancelamentos().size());
				ac.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(totalAutorizacaoArquivo);
				ac.setCabecalhoCartorio(save(ac.getCabecalhoCartorio()));
				ac.setRodapeCartorio(save(ac.getRodapeCartorio()));
				ac.setRemessaAutorizacaoCancelamento(ac.getRemessaAutorizacaoCancelamento());
				save(ac);
				for (PedidoAutorizacaoCancelamento pedido : ac.getAutorizacoesCancelamentos()) {
					save(pedido);
				}
			}
			transaction.commit();
			loggerCra.sucess(usuario, CraAcao.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO,
					"Arquivo " + arquivo.getNomeArquivo() + ", enviado por " + arquivo.getInstituicaoEnvio().getNomeFantasia()
							+ ", recebido com sucesso via aplicação.");

		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
		}
		return arquivoSalvo;
	}

	@SuppressWarnings("unchecked")
	public List<AutorizacaoCancelamento> consultarAutorizacoes(String nomeArquivo, Instituicao portador, Instituicao cartorio,
			LocalDate dataInicio, LocalDate dataFim, List<TipoArquivoFebraban> tiposArquivo, Usuario usuario) {
		Criteria criteria = getCriteria(AutorizacaoCancelamento.class);
		criteria.createAlias("remessaAutorizacaoCancelamento", "remessa");
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
	public List<AutorizacaoCancelamento> buscarRemessaAutorizacaoCancelamentoPendenteDownload(Instituicao instituicao) {
		Criteria criteria = getCriteria(AutorizacaoCancelamento.class);
		criteria.createAlias("cabecalhoCartorio", "cabecalho");

		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", instituicao.getMunicipio().getCodigoIBGE()));
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			criteria.createAlias("remessaAutorizacaoCancelamento", "remessaAutorizacaoCancelamento");
			criteria.createAlias("remessaAutorizacaoCancelamento.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", instituicao.getCodigoCompensacao()));
		}
		criteria.add(Restrictions.eq("download", false));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public RemessaAutorizacaoCancelamento buscarRemessasAutorizacaoCancelamento(Arquivo arquivo) {
		Criteria criteria = getCriteria(RemessaAutorizacaoCancelamento.class);
		criteria.createAlias("autorizacaoCancelamento", "autorizacaoCancelamento");
		criteria.add(Restrictions.eq("arquivo", arquivo));
		RemessaAutorizacaoCancelamento remessa = RemessaAutorizacaoCancelamento.class.cast(criteria.uniqueResult());

		Criteria criteriaRemessa = getCriteria(AutorizacaoCancelamento.class);
		criteriaRemessa.add(Restrictions.eq("remessaAutorizacaoCancelamento", remessa));
		List<AutorizacaoCancelamento> autorizacoes = criteriaRemessa.list();

		for (AutorizacaoCancelamento ac : autorizacoes) {
			Criteria criteriaPedido = getCriteria(PedidoAutorizacaoCancelamento.class);
			criteriaPedido.add(Restrictions.eq("autorizacaoCancelamento", ac));
			ac.setAutorizacoesCancelamentos(criteriaPedido.list());
		}
		remessa.setAutorizacaoCancelamento(autorizacoes);
		return remessa;
	}

	public AutorizacaoCancelamento alterarSituacaoAutorizacaoCancelamento(AutorizacaoCancelamento autorizacaoCancelamento,
			boolean download) {
		Transaction transaction = getBeginTransation();

		try {
			autorizacaoCancelamento.setDownload(download);
			update(autorizacaoCancelamento);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível atualizar o status da AC.");
		}
		return autorizacaoCancelamento;

	}

	public AutorizacaoCancelamento buscarRemessaAutorizacaoCancelamento(AutorizacaoCancelamento entidade) {
		return super.buscarPorPK(entidade);
	}

	public AutorizacaoCancelamento buscarAutorizacaoCancelamentoProtesto(Instituicao cartorio, String nomeArquivo) {
		Criteria criteria = getCriteria(AutorizacaoCancelamento.class);
		criteria.createAlias("remessaAutorizacaoCancelamento", "remessaAutorizacaoCancelamento");
		criteria.createAlias("remessaAutorizacaoCancelamento.arquivo", "arquivo");
		criteria.createAlias("cabecalhoCartorio", "cabecalhoCartorio");
		criteria.add(Restrictions.eq("cabecalhoCartorio.codigoMunicipio", cartorio.getMunicipio().getCodigoIBGE()));
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		return AutorizacaoCancelamento.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<PedidoAutorizacaoCancelamento> buscarPedidosAutorizacaoCancelamento(AutorizacaoCancelamento autorizacaoCancelamento) {
		Criteria criteria = getCriteria(PedidoAutorizacaoCancelamento.class);
		criteria.createAlias("titulo", "titulo", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("autorizacaoCancelamento", autorizacaoCancelamento));
		return criteria.list();
	}
}
