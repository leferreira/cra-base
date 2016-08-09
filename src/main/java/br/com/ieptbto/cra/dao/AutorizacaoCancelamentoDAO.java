package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Usuario;
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
public class AutorizacaoCancelamentoDAO extends AbstractBaseDAO {

	@Autowired
	private TituloDAO tituloDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;

	public Arquivo salvarAutorizacao(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		Arquivo arquivoSalvo = new Arquivo();
		Transaction transaction = getSession().beginTransaction();

		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));

			arquivoSalvo = save(arquivo);
			if (arquivo.getRemessaAutorizacao() != null) {
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
						erros.add(new DesistenciaCancelamentoException(descricao, codigoMunicipio, CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO));
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
			}
			logger.info("O arquivo " + arquivo.getNomeArquivo() + "enviado pelo usuário " + arquivo.getUsuarioEnvio().getLogin()
					+ " foi inserido na base ");

		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
			throw new InfraException("Não foi possível inserir esse arquivo na base de dados.");
		}
		return arquivoSalvo;
	}

	@SuppressWarnings("unchecked")
	public List<AutorizacaoCancelamento> buscarAutorizacaoCancelamento(Arquivo arquivo, Instituicao portador, Municipio municipio,
			LocalDate dataInicio, LocalDate dataFim, ArrayList<TipoArquivoEnum> tiposArquivo, Usuario usuario) {
		Criteria criteria = getCriteria(AutorizacaoCancelamento.class);
		criteria.createAlias("remessaAutorizacaoCancelamento", "remessa");
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

	public AutorizacaoCancelamento alterarSituacaoAutorizacaoCancelamento(AutorizacaoCancelamento autorizacaoCancelamento, boolean download) {
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
