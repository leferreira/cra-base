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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.DesistenciaCancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public class ArquivoDAO extends AbstractBaseDAO {

	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;

	public Arquivo salvar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		Arquivo arquivoSalvo = new Arquivo(); 
		Transaction transaction = getSession().beginTransaction();
		BigDecimal valorTotalSaldo = BigDecimal.ZERO;
		TipoArquivoEnum tipoArquivo = arquivo.getTipoArquivo().getTipoArquivo();
		Boolean retornoContemTituloPago = false;

		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			arquivoSalvo = save(arquivo);

			if (TipoArquivoEnum.REMESSA.equals(tipoArquivo) || TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)
					|| TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
				for (Remessa remessa : arquivo.getRemessas()) {
					remessa.setArquivo(arquivoSalvo);
					remessa.setCabecalho(save(remessa.getCabecalho()));
					remessa.setRodape(save(remessa.getRodape()));
					remessa.setArquivoGeradoProBanco(arquivoSalvo);
					remessa.setDataRecebimento(remessa.getCabecalho().getDataMovimento());
					remessa.setInstituicaoOrigem(arquivo.getInstituicaoEnvio());
					setDevolvidoPelaCRA(remessa);
					setStatusRemessa(arquivo.getInstituicaoEnvio().getTipoInstituicao(), remessa);
					setSituacaoLiberadoProBancoEBatimentoRetorno(arquivo, remessa);
					save(remessa);
					for (Titulo titulo : remessa.getTitulos()) {
						titulo.setRemessa(remessa);
						if (Retorno.class.isInstance(titulo)) {
							if (titulo.getTipoOcorrencia().equals(TipoOcorrencia.PAGO.getConstante())) {
								retornoContemTituloPago = true;
							}
							Retorno.class.cast(titulo).setCabecalho(remessa.getCabecalho());
						}
						TituloRemessa tituloSalvo = tituloDAO.salvar(titulo, erros, transaction);
						if (TituloRemessa.class.isInstance(titulo)) {
							if (TituloRemessa.class.cast(titulo).getAnexo() != null) {
								Anexo anexo = TituloRemessa.class.cast(titulo).getAnexo();
								tituloSalvo.setAnexo(anexo);
								anexo.setTitulo(tituloSalvo);
								save(anexo);
							}
						}
						valorTotalSaldo = valorTotalSaldo.add(titulo.getSaldoTitulo());
					}
					if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
						if (retornoContemTituloPago.equals(false)
								|| remessa.getInstituicaoDestino().getTipoBatimento().equals(TipoBatimento.LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO)) {
							remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.CONFIRMADO);
							update(remessa);
						}
					}
					remessa.getCabecalho().setQtdTitulosRemessa(remessa.getTitulos().size());
					remessa.getRodape().setSomatorioValorRemessa(valorTotalSaldo);
					remessa.setCabecalho(save(remessa.getCabecalho()));
					remessa.setRodape(save(remessa.getRodape()));
				}
			} else if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
				List<DesistenciaProtesto> desistenciasProtesto = new ArrayList<DesistenciaProtesto>();
				BigDecimal valorTotalDesistenciaArquivo = BigDecimal.ZERO;
				int quantidadeDesistenciasArquivo = 0;
				for (DesistenciaProtesto desistenciaProtestos : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
					List<PedidoDesistencia> pedidosDesistenciaComErro = new ArrayList<PedidoDesistencia>();
					List<PedidoDesistencia> pedidosProcessados = new ArrayList<PedidoDesistencia>();
					int quantidadeDesistenciasCartorio = 0;
					desistenciaProtestos.setRemessaDesistenciaProtesto(arquivo.getRemessaDesistenciaProtesto());
					desistenciaProtestos.setDownload(false);
					for (PedidoDesistencia pedido : desistenciaProtestos.getDesistencias()) {
						pedido.setDesistenciaProtesto(desistenciaProtestos);
						pedido.setTitulo(tituloDAO.buscarTituloDesistenciaProtesto(pedido));
						if (pedido.getTitulo() != null) {
							pedidosProcessados.add(pedido);
							quantidadeDesistenciasCartorio = quantidadeDesistenciasCartorio + 1;
							valorTotalDesistenciaArquivo = valorTotalDesistenciaArquivo.add(pedido.getValorTitulo());
							quantidadeDesistenciasArquivo = quantidadeDesistenciasArquivo + 1;
						} else {
							pedidosDesistenciaComErro.add(pedido);
						}
					}
					if (pedidosDesistenciaComErro.isEmpty()) {
						desistenciaProtestos.getCabecalhoCartorio().setQuantidadeDesistencia(quantidadeDesistenciasCartorio);
						desistenciaProtestos.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(quantidadeDesistenciasCartorio * 2);
						desistenciaProtestos.setDesistencias(pedidosProcessados);
						desistenciasProtesto.add(desistenciaProtestos);
					} else {
						String descricao = StringUtils.EMPTY;
						String codigoMunicipio = StringUtils.EMPTY;
						for (PedidoDesistencia pedidoDesistencia : pedidosDesistenciaComErro) {
							descricao = descricao + "Protocolo Inválido (" + pedidoDesistencia.getNumeroProtocolo() + ").";
							codigoMunicipio = pedidoDesistencia.getDesistenciaProtesto().getCabecalhoCartorio().getCodigoMunicipio();
						}
						erros.add(new DesistenciaCancelamentoException(descricao, codigoMunicipio, CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO));
						pedidosDesistenciaComErro.clear();
					}
				}
				arquivo.getRemessaDesistenciaProtesto().getCabecalho().setQuantidadeDesistencia(quantidadeDesistenciasArquivo);
				arquivo.getRemessaDesistenciaProtesto().getCabecalho().setQuantidadeRegistro(quantidadeDesistenciasArquivo);
				arquivo.getRemessaDesistenciaProtesto().getRodape().setQuantidadeDesistencia((quantidadeDesistenciasArquivo * 2));
				arquivo.getRemessaDesistenciaProtesto().getRodape().setSomatorioValorTitulo(valorTotalDesistenciaArquivo);
				arquivo.getRemessaDesistenciaProtesto().setDesistenciaProtesto(desistenciasProtesto);
				arquivo.getRemessaDesistenciaProtesto().setCabecalho(save(arquivo.getRemessaDesistenciaProtesto().getCabecalho()));
				arquivo.getRemessaDesistenciaProtesto().setRodape(save(arquivo.getRemessaDesistenciaProtesto().getRodape()));
				save(arquivo.getRemessaDesistenciaProtesto());

				for (DesistenciaProtesto desistenciaProtestos : desistenciasProtesto) {
					desistenciaProtestos.setCabecalhoCartorio(save(desistenciaProtestos.getCabecalhoCartorio()));
					desistenciaProtestos.setRodapeCartorio(save(desistenciaProtestos.getRodapeCartorio()));
					desistenciaProtestos.setRemessaDesistenciaProtesto(arquivo.getRemessaDesistenciaProtesto());
					save(desistenciaProtestos);
					for (PedidoDesistencia pedido : desistenciaProtestos.getDesistencias()) {
						save(pedido);
					}
				}

			} else if (TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
				throw new InfraException("Não foi possivel enviar o Cancelamento de Protesto! Entre em contato com a CRA!");
			} else if (TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
				throw new InfraException("Não foi possivel enviar a Autorização de Cancelamento! Entre em contato com a CRA!");
			}

			if (!erros.isEmpty()) {
				transaction.rollback();
				return arquivo;
			}
			transaction.commit();
			loggerCra.sucess(arquivo.getInstituicaoEnvio(), usuario, getTipoAcaoEnvio(arquivo), "Arquivo " + arquivo.getNomeArquivo()
					+ ", enviado por " + arquivo.getInstituicaoEnvio().getNomeFantasia() + ", recebido com sucesso via aplicação.");

		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
		}
		return arquivoSalvo;
	}

	private CraAcao getTipoAcaoEnvio(Arquivo arquivo) {
		CraAcao tipoAcao = null;
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo);
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_REMESSA;
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_CONFIRMACAO;
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_RETORNO;
		} else if (tipoArquivo.equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO;
		} else if (tipoArquivo.equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO;
		} else if (tipoArquivo.equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO;
		}
		return tipoAcao;
	}

	public Arquivo alterarStatusArquivo(Arquivo arquivo) {
		Transaction transaction = getBeginTransation();

		try {
			StatusArquivo statusArquivo = save(arquivo.getStatusArquivo());
			arquivo.setStatusArquivo(statusArquivo);
			update(arquivo);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esses dados na base.");
		}
		return arquivo;
	}

	private void setDevolvidoPelaCRA(Remessa remessa) {
		if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			remessa.setDevolvidoPelaCRA(false);
		}
	}

	private void setStatusRemessa(TipoInstituicao tipoInstituicao, Remessa remessa) {
		if (tipoInstituicao.getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicao.getTipoInstituicao().equals(TipoInstituicaoCRA.CONVENIO)) {
			remessa.setStatusRemessa(StatusRemessa.AGUARDANDO);
		} else if (tipoInstituicao.getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			remessa.setStatusRemessa(StatusRemessa.ENVIADO);
		}
	}

	private void setSituacaoLiberadoProBancoEBatimentoRetorno(Arquivo arquivo, Remessa remessa) {
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)
				|| arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			remessa.setSituacao(false);
			if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.NAO_CONFIRMADO);
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Remessa> baixarArquivoInstituicaoRemessa(Arquivo arquivo) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("arquivo", arquivo));

		List<Remessa> remessas = criteria.list();
		arquivo.setRemessas(new ArrayList<Remessa>());
		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(TituloRemessa.class);
			criteriaTitulo.add(Restrictions.eq("remessa", remessa));

			remessa.setTitulos(criteriaTitulo.list());
			arquivo.getRemessas().add(remessa);
		}
		return arquivo.getRemessas();
	}

	@Transactional(readOnly = true)
	public List<Remessa> baixarArquivoInstituicaoConfirmacao(Arquivo arquivo) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("arquivoGeradoProBanco", arquivo));

		List<Remessa> remessas = criteria.list();
		arquivo.setRemessas(new ArrayList<Remessa>());
		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(Confirmacao.class);
			criteriaTitulo.add(Restrictions.eq("remessa", remessa));

			remessa.setTitulos(criteriaTitulo.list());
			arquivo.getRemessas().add(remessa);
		}
		return arquivo.getRemessas();
	}

	@Transactional(readOnly = true)
	public List<Remessa> baixarArquivoInstituicaoRetorno(Arquivo arquivo) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.add(Restrictions.eq("arquivoGeradoProBanco", arquivo));

		List<Remessa> remessas = criteria.list();
		arquivo.setRemessas(new ArrayList<Remessa>());
		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(Retorno.class);
			criteriaTitulo.add(Restrictions.eq("remessa", remessa));

			remessa.setTitulos(criteriaTitulo.list());
			arquivo.getRemessas().add(remessa);
		}
		return arquivo.getRemessas();
	}

	@Transactional(readOnly = true)
	public Arquivo buscarArquivoInstituicaoRemessa(String nomeArquivo, Instituicao instituicaoRecebe) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.ilike("nomeArquivo", nomeArquivo, MatchMode.EXACT));
		criteria.add(Restrictions.eq("instituicaoRecebe", instituicaoRecebe));

		Arquivo arquivo = Arquivo.class.cast(criteria.uniqueResult());
		arquivo.setRemessas(new ArrayList<Remessa>());
		for (Remessa remessa : arquivo.getRemessaBanco()) {
			Criteria criteriaTitulo = getCriteria(TituloRemessa.class);
			criteriaTitulo.add(Restrictions.eq("remessa", remessa));

			remessa.setTitulos(criteriaTitulo.list());
			arquivo.getRemessas().add(remessa);
		}
		return arquivo;
	}

	@Transactional(readOnly = true)
	public Arquivo buscarArquivoInstituicaoConfirmacao(String nomeArquivo, Instituicao instituicaoRecebe) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.ilike("nomeArquivo", nomeArquivo, MatchMode.EXACT));
		criteria.add(Restrictions.eq("instituicaoRecebe", instituicaoRecebe));

		Arquivo arquivo = Arquivo.class.cast(criteria.uniqueResult());
		if (arquivo == null) {
			return null;
		}
		arquivo.setRemessas(new ArrayList<Remessa>());
		for (Remessa remessa : arquivo.getRemessaBanco()) {
			Criteria criteriaTitulo = getCriteria(Confirmacao.class);
			criteriaTitulo.add(Restrictions.eq("remessa", remessa));

			remessa.setTitulos(criteriaTitulo.list());
			arquivo.getRemessas().add(remessa);
		}
		return arquivo;
	}

	@Transactional(readOnly = true)
	public Arquivo buscarArquivoInstituicaoRetorno(String nomeArquivo, Instituicao instituicaoRecebe) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.ilike("nomeArquivo", nomeArquivo, MatchMode.EXACT));
		criteria.add(Restrictions.eq("instituicaoRecebe", instituicaoRecebe));

		Arquivo arquivo = Arquivo.class.cast(criteria.uniqueResult());
		if (arquivo == null) {
			return null;
		}
		arquivo.setRemessas(new ArrayList<Remessa>());
		for (Remessa remessa : arquivo.getRemessaBanco()) {
			Criteria criteriaTitulo = getCriteria(Retorno.class);
			criteriaTitulo.add(Restrictions.eq("remessa", remessa));

			remessa.setTitulos(criteriaTitulo.list());
			arquivo.getRemessas().add(remessa);
		}
		return arquivo;
	}

	public List<Arquivo> buscarArquivosAvancado(Arquivo arquivo, Usuario usuario, ArrayList<TipoArquivoEnum> tipoArquivos, Municipio municipio,
			LocalDate dataInicio, LocalDate dataFim, ArrayList<SituacaoArquivo> situacoes) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("instituicaoEnvio", "instituicaoEnvio");
		criteria.createAlias("tipoArquivo", "tipoArquivo");
		criteria.createAlias("instituicaoEnvio.tipoInstituicao", "tipoInstituicao");
		criteria.add(Restrictions.ne("tipoInstituicao.tipoInstituicao", TipoInstituicaoCRA.CARTORIO));
		criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", usuario.getInstituicao()),
				Restrictions.eq("instituicaoRecebe", usuario.getInstituicao())));

		if (arquivo.getInstituicaoEnvio() != null) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", arquivo.getInstituicaoEnvio()),
					Restrictions.eq("instituicaoRecebe", arquivo.getInstituicaoEnvio())));
		}

		if (!situacoes.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			criteria.createAlias("statusArquivo", "statusArquivo");
			Disjunction disj = Restrictions.disjunction();
			for (SituacaoArquivo status : situacoes) {
				disjunction.add(Restrictions.eq("statusArquivo.situacaoArquivo", status));
			}
			criteria.add(disj);
		}

		if (arquivo.getNomeArquivo() != null && arquivo.getNomeArquivo() != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike("nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
		}

		if (!tipoArquivos.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			for (TipoArquivoEnum tipoArquivo : tipoArquivos) {
				disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipoArquivo));
			}
			criteria.add(disjunction);
		}

		if (dataInicio != null) {
			criteria.add(Restrictions.between("dataEnvio", dataInicio, dataFim));
		}
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO));
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO));
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO));
		criteria.addOrder(Order.desc("dataEnvio"));
		return criteria.list();
	}

	@Transactional(readOnly = true)
	public Arquivo buscarArquivosPorNomeArquivoInstituicaoEnvio(Instituicao instituicao, String nomeArquivo) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicao));
		criteria.add(Restrictions.eq("nomeArquivo", nomeArquivo));
		criteria.setMaxResults(1);
		return Arquivo.class.cast(criteria.uniqueResult());
	}
}
