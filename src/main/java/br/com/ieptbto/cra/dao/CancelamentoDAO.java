package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.DesistenciaCancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class CancelamentoDAO extends AbstractBaseDAO {

	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;

	@SuppressWarnings("unchecked")
	public List<PedidoCancelamento> buscarPedidosCancelamentoProtestoPorTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(PedidoCancelamento.class);
		criteria.add(Restrictions.eq("titulo", titulo));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<PedidoCancelamento> buscarPedidosCancelamentoProtesto(CancelamentoProtesto cancelamentoProtesto) {
		Criteria criteria = getCriteria(PedidoCancelamento.class);
		criteria.createAlias("titulo", "titulo", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("cancelamentoProtesto", cancelamentoProtesto));
		return criteria.list();
	}

	public CancelamentoProtesto buscarRemessaCancelamentoProtesto(CancelamentoProtesto entidade) {
		return super.buscarPorPK(entidade);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo salvarCancelamento(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		Arquivo arquivoSalvo = new Arquivo();
		Transaction transaction = getSession().beginTransaction();

		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));

			arquivoSalvo = save(arquivo);
			List<PedidoCancelamento> pedidosCancelamentoErros = new ArrayList<PedidoCancelamento>();
			List<CancelamentoProtesto> cancelamentosProtesto = new ArrayList<CancelamentoProtesto>();
			BigDecimal valorTotalCancelamentoProtesto = BigDecimal.ZERO;
			int totalCancelamentoProtestoArquivo = 0;
			for (CancelamentoProtesto cancelamento : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {
				List<PedidoCancelamento> pedidosProcessados = new ArrayList<PedidoCancelamento>();
				int quantidadeCancelamentoCartorio = 0;
				cancelamento.setRemessaCancelamentoProtesto(arquivo.getRemessaCancelamentoProtesto());
				cancelamento.setDownload(false);

				for (PedidoCancelamento pedido : cancelamento.getCancelamentos()) {
					pedido.setCancelamentoProtesto(cancelamento);
					pedido.setTitulo(tituloDAO.buscarTituloCancelamentoProtesto(pedido));

					if (pedido.getTitulo() != null) {
						pedido.setCodigoErroProcessamento(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO);
						pedidosProcessados.add(pedido);
						quantidadeCancelamentoCartorio = quantidadeCancelamentoCartorio + 1;
						valorTotalCancelamentoProtesto = valorTotalCancelamentoProtesto.add(pedido.getValorTitulo());
						totalCancelamentoProtestoArquivo = totalCancelamentoProtestoArquivo + 1;
					} else if (pedido.getDataProtocolagem().isAfter(DataUtil.stringToLocalDate("dd/MM/yyyy", "01/12/2015"))
							|| pedido.getDataProtocolagem().equals(DataUtil.stringToLocalDate("dd/MM/yyyy", "01/12/2015"))) {
						pedido.setCodigoErroProcessamento(CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO);
						pedidosCancelamentoErros.add(pedido);
					} else {
						pedido.setCodigoErroProcessamento(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO);
						pedidosProcessados.add(pedido);
						quantidadeCancelamentoCartorio = quantidadeCancelamentoCartorio + 1;
						valorTotalCancelamentoProtesto = valorTotalCancelamentoProtesto.add(pedido.getValorTitulo());
						totalCancelamentoProtestoArquivo = totalCancelamentoProtestoArquivo + 1;
					}
				}

				if (pedidosCancelamentoErros.isEmpty()) {
					cancelamento.getCabecalhoCartorio().setQuantidadeDesistencia(quantidadeCancelamentoCartorio);
					cancelamento.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(quantidadeCancelamentoCartorio * 2);
					cancelamento.setCancelamentos(pedidosProcessados);
					cancelamentosProtesto.add(cancelamento);
				} else {
					String descricao = StringUtils.EMPTY;
					String codigoMunicipio = StringUtils.EMPTY;
					for (PedidoCancelamento pedidoCancelamento : pedidosCancelamentoErros) {
						descricao = descricao + "Protocolo Inválido (" + pedidoCancelamento.getNumeroProtocolo() + ").";
						codigoMunicipio = pedidoCancelamento.getCancelamentoProtesto().getCabecalhoCartorio().getCodigoMunicipio();
					}
					erros.add(
							new DesistenciaCancelamentoException(descricao, codigoMunicipio, CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO));
					pedidosCancelamentoErros.clear();
				}
			}
			arquivo.getRemessaCancelamentoProtesto().getCabecalho().setQuantidadeDesistencia(totalCancelamentoProtestoArquivo);
			arquivo.getRemessaCancelamentoProtesto().getCabecalho().setQuantidadeRegistro(totalCancelamentoProtestoArquivo);
			arquivo.getRemessaCancelamentoProtesto().getRodape().setQuantidadeDesistencia(totalCancelamentoProtestoArquivo);
			arquivo.getRemessaCancelamentoProtesto().getRodape().setSomatorioValorTitulo(valorTotalCancelamentoProtesto);
			arquivo.getRemessaCancelamentoProtesto().setCancelamentoProtesto(cancelamentosProtesto);
			arquivo.getRemessaCancelamentoProtesto().setCabecalho(save(arquivo.getRemessaCancelamentoProtesto().getCabecalho()));
			arquivo.getRemessaCancelamentoProtesto().setRodape(save(arquivo.getRemessaCancelamentoProtesto().getRodape()));
			save(arquivo.getRemessaCancelamentoProtesto());

			for (CancelamentoProtesto cancelamentoProtestos : cancelamentosProtesto) {
				cancelamentoProtestos.getCabecalhoCartorio().setQuantidadeDesistencia(cancelamentoProtestos.getCancelamentos().size());
				cancelamentoProtestos.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(totalCancelamentoProtestoArquivo);
				cancelamentoProtestos.setCabecalhoCartorio(save(cancelamentoProtestos.getCabecalhoCartorio()));
				cancelamentoProtestos.setRodapeCartorio(save(cancelamentoProtestos.getRodapeCartorio()));
				cancelamentoProtestos.setRemessaCancelamentoProtesto(arquivo.getRemessaCancelamentoProtesto());
				save(cancelamentoProtestos);
				for (PedidoCancelamento pedido : cancelamentoProtestos.getCancelamentos()) {
					save(pedido);
				}
			}
			transaction.commit();
			loggerCra.sucess(arquivo.getInstituicaoEnvio(), usuario, CraAcao.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO,
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
	public List<CancelamentoProtesto> buscarCancelamentoProtesto(String nomeArquivo, Instituicao portador, Instituicao cartorio,
			LocalDate dataInicio, LocalDate dataFim, List<TipoArquivoEnum> tiposArquivo, Usuario usuario) {
		Criteria criteria = getCriteria(CancelamentoProtesto.class);
		criteria.createAlias("remessaCancelamentoProtesto", "remessa");
		criteria.createAlias("remessa.arquivo", "arquivo");

		if (StringUtils.isNotBlank(nomeArquivo)) {
			criteria.add(Restrictions.ilike("arquivo.nomeArquivo", nomeArquivo, MatchMode.ANYWHERE));
		}

		if (tiposArquivo != null && !tiposArquivo.isEmpty()) {
			criteria.createAlias("arquivo.tipoArquivo", "tipo");
			Disjunction disjunction = Restrictions.disjunction();
			for (TipoArquivoEnum tipo : tiposArquivo) {
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

		if (usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			criteria.createAlias("cabecalhoCartorio", "cabecalho");
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", usuario.getInstituicao().getMunicipio().getCodigoIBGE()));
		} else if (usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			criteria.createAlias("remessa.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", usuario.getInstituicao().getCodigoCompensacao()));
		}
		return criteria.list();
	}

	@SuppressWarnings({ "unchecked" })
	public List<CancelamentoProtesto> buscarRemessaCancelamentoPendenteDownload(Instituicao instituicao) {
		instituicao = instituicaoDAO.buscarPorPK(instituicao);
		Criteria criteria = getCriteria(CancelamentoProtesto.class);
		criteria.createAlias("cabecalhoCartorio", "cabecalho");

		if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", instituicao.getMunicipio().getCodigoIBGE()));
		} else if (instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			criteria.createAlias("remessaCancelamentoProtesto", "remessaCancelamentoProtesto");
			criteria.createAlias("remessaCancelamentoProtesto.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", instituicao.getCodigoCompensacao()));
		}
		criteria.add(Restrictions.eq("download", false));
		return criteria.list();
	}

	public CancelamentoProtesto alterarSituacaoCancelamentoProtesto(CancelamentoProtesto cancelamentoProtesto, boolean download) {
		Transaction transaction = getBeginTransation();

		try {
			cancelamentoProtesto.setDownload(download);
			update(cancelamentoProtesto);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível atualizar o status da DP.");
		}
		return cancelamentoProtesto;
	}

	public CancelamentoProtesto buscarCancelamentoProtesto(Instituicao cartorio, String nomeArquivo) {
		Criteria criteria = getCriteria(CancelamentoProtesto.class);
		criteria.createAlias("remessaCancelamentoProtesto", "remessaCancelamentoProtesto");
		criteria.createAlias("remessaCancelamentoProtesto.arquivo", "arquivo");
		criteria.createAlias("cabecalhoCartorio", "cabecalhoCartorio");
		criteria.add(Restrictions.eq("cabecalhoCartorio.codigoMunicipio", cartorio.getMunicipio().getCodigoIBGE()));
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nomeArquivo));
		return CancelamentoProtesto.class.cast(criteria.uniqueResult());
	}

	public SolicitacaoDesistenciaCancelamento salvarSolicitacaoDesistenciaCancelamento(
			SolicitacaoDesistenciaCancelamento solicitacaoCancelamento) {
		Transaction transaction = getBeginTransation();

		try {
			save(solicitacaoCancelamento);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return solicitacaoCancelamento;
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoDesistenciaCancelamento> buscarCancelamentosSolicitados() {
		Criteria criteria = getCriteria(SolicitacaoDesistenciaCancelamento.class);
		criteria.createAlias("tituloRemessa", "tituloRemessa");
		criteria.add(Restrictions.eq("statusLiberacao", false));
		return criteria.list();
	}

	public void marcarSolicitacoesDesistenciasCancelamentosEnviados(List<SolicitacaoDesistenciaCancelamento> solicitacoesCancelamento) {
		Transaction transaction = getBeginTransation();

		try {
			for (SolicitacaoDesistenciaCancelamento solicitacaoCancelamento : solicitacoesCancelamento) {
				solicitacaoCancelamento.setStatusLiberacao(true);
				update(solicitacaoCancelamento);
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi salvar os cancelamentos como enviados mas os arquivos foram gerados !");
		}
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoDesistenciaCancelamento> buscarSolicitacoesDesistenciasCancelamentoPorTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(SolicitacaoDesistenciaCancelamento.class);
		criteria.add(Restrictions.eq("tituloRemessa", titulo));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public SolicitacaoDesistenciaCancelamento verificarSolicitadoAnteriormente(
			SolicitacaoDesistenciaCancelamento solicitacaoDesistenciaCancelamento) {
		Criteria criteria = getCriteria(SolicitacaoDesistenciaCancelamento.class);
		criteria.add(Restrictions.eq("tituloRemessa", solicitacaoDesistenciaCancelamento.getTituloRemessa()));
		criteria.add(Restrictions.eq("tipoSolicitacao", solicitacaoDesistenciaCancelamento.getTipoSolicitacao()));
		criteria.setMaxResults(1);
		criteria.addOrder(Order.desc("id"));
		return SolicitacaoDesistenciaCancelamento.class.cast(criteria.uniqueResult());
	}
}
