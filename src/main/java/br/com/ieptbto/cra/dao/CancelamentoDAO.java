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
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.SolicitacaoCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.StatusSolicitacaoCancelamento;
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
					erros.add(new DesistenciaCancelamentoException(descricao, codigoMunicipio, CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO));
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
			loggerCra.sucess(arquivo.getInstituicaoEnvio(), usuario, CraAcao.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO, "Arquivo " + arquivo.getNomeArquivo()
					+ ", enviado por " + arquivo.getInstituicaoEnvio().getNomeFantasia() + ", recebido com sucesso via aplicação.");

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
	public List<CancelamentoProtesto> buscarCancelamentoProtesto(Arquivo arquivo, Instituicao portador, Municipio municipio, LocalDate dataInicio,
			LocalDate dataFim, ArrayList<TipoArquivoEnum> tiposArquivo, Usuario usuario) {
		Criteria criteria = getCriteria(CancelamentoProtesto.class);
		criteria.createAlias("remessaCancelamentoProtesto", "remessa");
		criteria.createAlias("remessa.arquivo", "arquivo");

		if (StringUtils.isNotBlank(arquivo.getNomeArquivo())) {
			criteria.add(Restrictions.ilike("arquivo.nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
		}

		if (tiposArquivo != null && !tiposArquivo.isEmpty()) {
			criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
			criteria.add(filtrarRemessaPorTipoArquivo(tiposArquivo));
		}

		if (dataInicio != null && dataFim != null) {
			criteria.add((Restrictions.between("arquivo.dataEnvio", dataInicio, dataFim)));
		}

		if (portador != null) {
			criteria.createAlias("remessa.cabecalho", "cabecalhoArquivo");
			criteria.add(Restrictions.eq("cabecalhoArquivo.codigoApresentante", portador.getCodigoCompensacao()));
		}

		if (municipio != null) {
			criteria.createAlias("cabecalhoCartorio", "cabecalho");
			criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", municipio.getCodigoIBGE()));
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

	private Disjunction filtrarRemessaPorTipoArquivo(ArrayList<TipoArquivoEnum> tiposArquivo) {
		Disjunction disjunction = Restrictions.disjunction();
		for (TipoArquivoEnum tipo : tiposArquivo) {
			disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipo));
		}
		return disjunction;
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

	@SuppressWarnings("unchecked")
	public List<TituloRemessa> buscarTitulosParaSolicitarCancelamento(TituloRemessa tituloRemessa, Instituicao bancoConvenio, Municipio municipio,
			Usuario user) {
		Criteria criteria = getCriteria(TituloRemessa.class);

		criteria.createAlias("remessa", "remessa");
		if (!user.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("remessa.instituicaoOrigem", user.getInstituicao()),
					Restrictions.eq("remessa.instituicaoDestino", user.getInstituicao())));
		}

		if (bancoConvenio != null) {
			criteria.add(Restrictions.eq("codigoPortador", bancoConvenio.getCodigoCompensacao()));
		}

		if (tituloRemessa.getNumeroProtocoloCartorio() != null && tituloRemessa.getNumeroProtocoloCartorio() != StringUtils.EMPTY) {
			criteria.createAlias("confirmacao", "confirmacao");
			criteria.add(Restrictions.eq("confirmacao.numeroProtocoloCartorio", tituloRemessa.getNumeroProtocoloCartorio()));
		}

		if (tituloRemessa.getNumeroTitulo() != null && tituloRemessa.getNumeroTitulo() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("numeroTitulo", tituloRemessa.getNumeroTitulo(), MatchMode.EXACT));

		if (tituloRemessa.getNomeDevedor() != null && tituloRemessa.getNomeDevedor() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("nomeDevedor", tituloRemessa.getNomeDevedor(), MatchMode.ANYWHERE));

		if (tituloRemessa.getNumeroIdentificacaoDevedor() != null && tituloRemessa.getNumeroIdentificacaoDevedor() != StringUtils.EMPTY)
			criteria.add(Restrictions.ilike("numeroIdentificacaoDevedor", tituloRemessa.getNumeroIdentificacaoDevedor(), MatchMode.ANYWHERE));

		if (municipio != null) {
			criteria.createAlias("remessa.cabecalho", "cabecalho");
			criteria.add(Restrictions.ilike("cabecalho.codigoMunicipio", municipio.getCodigoIBGE()));
		}
		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}

	public SolicitacaoCancelamento salvarSolicitacaoCancelamento(SolicitacaoCancelamento solicitacaoCancelamento) {
		Transaction transaction = getBeginTransation();

		try {
			save(solicitacaoCancelamento);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível enviar a solicitação de cancelamento!");
		}
		return solicitacaoCancelamento;
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoCancelamento> buscarCancelamentosSolicitados() {
		Criteria criteria = getCriteria(SolicitacaoCancelamento.class);
		criteria.createAlias("tituloRemessa", "tituloRemessa");
		Disjunction disjuntion = Restrictions.disjunction();
		disjuntion.add(Restrictions.eq("statusSolicitacaoCancelamento", StatusSolicitacaoCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO));
		disjuntion.add(Restrictions.eq("statusSolicitacaoCancelamento", StatusSolicitacaoCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO));
		criteria.add(disjuntion);
		return criteria.list();
	}

	public void marcarCancelamentoEnviado(List<SolicitacaoCancelamento> solicitacoesCancelamento) {
		Transaction transaction = getBeginTransation();
		try {
			for (SolicitacaoCancelamento solicitacaoCancelamento : solicitacoesCancelamento) {
				solicitacaoCancelamento.setStatusSolicitacaoCancelamento(StatusSolicitacaoCancelamento.SOLICITACAO_ENVIADA);
				update(solicitacaoCancelamento);
			}
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi salvar os cancelamentos como enviados mas os arquivos foram gerados !");
		}
	}

	public SolicitacaoCancelamento buscarSolicitacaoCancelamentoPorTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(SolicitacaoCancelamento.class);
		criteria.add(Restrictions.eq("tituloRemessa", titulo));
		return SolicitacaoCancelamento.class.cast(criteria.uniqueResult());
	}
}
