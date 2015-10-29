package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.PedidoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.TituloSemTaxaCRA;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.TituloException;
import br.com.ieptbto.cra.util.DataUtil;

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
	@Autowired
	RemessaDAO remessaDAO;
	@Autowired
	TituloSemTaxaCraDAO tituloSemTaxaCraDAO;

	private List<Remessa> remessasConfirmacoesRecebidas;

	public List<Arquivo> buscarTodosArquivos() {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public Arquivo salvar(Arquivo arquivo, Usuario usuarioAcao, List<Exception> erros) {
		Arquivo arquivoSalvo = new Arquivo();
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		BigDecimal valorTotalSaldo = BigDecimal.ZERO;

		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			verificaInstituicaoRecebe(arquivo);
			arquivoSalvo = save(arquivo);

			if (!arquivo.getRemessas().isEmpty()) {
				for (Remessa remessa : arquivo.getRemessas()) {
					remessa.setArquivo(arquivoSalvo);
					remessa.setCabecalho(save(remessa.getCabecalho()));
					remessa.setRodape(save(remessa.getRodape()));
					remessa.setArquivoGeradoProBanco(arquivoSalvo);
					remessa.setDataRecebimento(remessa.getCabecalho().getDataMovimento());
					remessa.setInstituicaoOrigem(arquivo.getInstituicaoEnvio());
					setDevolvidoPelaCRA(remessa);
					setStatusRemessa(arquivo.getInstituicaoEnvio().getTipoInstituicao(), remessa);
					setSituacaoRemessa(arquivo, remessa);
					save(remessa);
					for (Titulo titulo : remessa.getTitulos()) {
						titulo.setRemessa(remessa);
						if (Retorno.class.isInstance(titulo)) {
							Retorno.class.cast(titulo).setCabecalho(remessa.getCabecalho());
						}
						TituloRemessa tituloSalvo = tituloDAO.salvar(titulo, transaction);

						Historico historico = new Historico();
						if (tituloSalvo != null) {
							historico.setDataOcorrencia(new LocalDateTime());
							historico.setRemessa(remessa);
							historico.setTitulo(tituloSalvo);
							historico.setUsuarioAcao(usuarioAcao);
							save(historico);
						} else {
							titulo.setSaldoTitulo(BigDecimal.ZERO);
							remessa.getTitulos().remove(titulo);
						}

						valorTotalSaldo = valorTotalSaldo.add(titulo.getSaldoTitulo());
						remessa.getCabecalho().setQtdTitulosRemessa(remessa.getTitulos().size());
						remessa.getRodape().setSomatorioValorRemessa(valorTotalSaldo);

						remessa.setCabecalho(save(remessa.getCabecalho()));
						remessa.setRodape(save(remessa.getRodape()));
					}
				}
			} else if (arquivo.getRemessaDesistenciaProtesto() != null) {
				List<DesistenciaProtesto> desistenciasProtesto = new ArrayList<DesistenciaProtesto>();
				BigDecimal valorTotalDesistenciaProtesto = BigDecimal.ZERO;
				int totalDesistenciaProtesto = 0;
				int totalRegistroDesistenciaProtesto = 0;

				for (DesistenciaProtesto desistenciaProtestos : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
					List<PedidoDesistenciaCancelamento> pedidos = new ArrayList<PedidoDesistenciaCancelamento>();

					desistenciaProtestos.setRemessaDesistenciaProtesto(arquivo.getRemessaDesistenciaProtesto());
					desistenciaProtestos.setDownload(false);

					for (PedidoDesistenciaCancelamento pedido : desistenciaProtestos.getDesistencias()) {
						pedido.setDesistenciaProtesto(desistenciaProtestos);
						pedido.setTitulo(getTituloDesistenciaProtesto(pedido));
						if (pedido.getTitulo() != null) {
							if (pedido.getTitulo().getPedidoDesistencia() == null) {
								pedidos.add(pedido);
								valorTotalDesistenciaProtesto = valorTotalDesistenciaProtesto.add(pedido.getValorTitulo());
								totalRegistroDesistenciaProtesto++;
							} else {
								erros.add(new InfraException("Linha " + pedido.getSequenciaRegistro() + ": o título de número "+ pedido.getNumeroTitulo() +", do protocolo "+ pedido.getNumeroProtocolo() +
										" do dia "+ DataUtil.localDateToString(pedido.getDataProtocolagem()) +", já foi enviado anteriormente em outro arquivo de título!"));
								
							}
						} else {
							erros.add(new InfraException("Linha " + pedido.getSequenciaRegistro() + ": o título de número "+ pedido.getNumeroTitulo() +",com o protocolo "+ pedido.getNumeroProtocolo() +
									" do dia "+ DataUtil.localDateToString(pedido.getDataProtocolagem()) +", não foi localizado na CRA. Verifique os dados do título!"));
						}
					}
					if (!pedidos.isEmpty()) {
						desistenciaProtestos.getCabecalhoCartorio().setQuantidadeDesistencia(pedidos.size());
						desistenciaProtestos.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(pedidos.size());
						desistenciaProtestos.setDesistencias(pedidos);
						desistenciasProtesto.add(desistenciaProtestos);
						totalDesistenciaProtesto++;
					}

				}
				arquivo.getRemessaDesistenciaProtesto().getCabecalho().setQuantidadeDesistencia(totalDesistenciaProtesto);
				arquivo.getRemessaDesistenciaProtesto().getCabecalho().setQuantidadeRegistro(totalRegistroDesistenciaProtesto);
				arquivo.getRemessaDesistenciaProtesto().getRodape().setQuantidadeDesistencia(totalDesistenciaProtesto);
				arquivo.getRemessaDesistenciaProtesto().getRodape().setSomatorioValorTitulo(valorTotalDesistenciaProtesto);
				arquivo.getRemessaDesistenciaProtesto().setDesistenciaProtesto(desistenciasProtesto);

				arquivo.getRemessaDesistenciaProtesto().setCabecalho(save(arquivo.getRemessaDesistenciaProtesto().getCabecalho()));
				arquivo.getRemessaDesistenciaProtesto().setRodape(save(arquivo.getRemessaDesistenciaProtesto().getRodape()));
				save(arquivo.getRemessaDesistenciaProtesto());

				for (DesistenciaProtesto desistenciaProtestos : desistenciasProtesto) {
					desistenciaProtestos.getCabecalhoCartorio().setQuantidadeDesistencia(desistenciaProtestos.getDesistencias().size());
					desistenciaProtestos.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(
					        desistenciaProtestos.getDesistencias().size());

					desistenciaProtestos.setCabecalhoCartorio(save(desistenciaProtestos.getCabecalhoCartorio()));
					desistenciaProtestos.setRodapeCartorio(save(desistenciaProtestos.getRodapeCartorio()));
					desistenciaProtestos.setRemessaDesistenciaProtesto(arquivo.getRemessaDesistenciaProtesto());

						save(desistenciaProtestos);
						for (PedidoDesistenciaCancelamento pedido : desistenciaProtestos.getDesistencias()) {
							save(pedido);
						}
				}

				if (!erros.isEmpty()) {
					throw new TituloException("Não foi possível enviar a desistência! Por favor, corriga os erros no arquivo abaixo...", erros);
				}
			}
			transaction.commit();
			logger.info("O arquivo " + arquivo.getNomeArquivo() + "enviado pelo usuário " + arquivo.getUsuarioEnvio().getLogin()
			        + " foi inserido na base ");
		} catch (TituloException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new TituloException(ex.getMessage(), ex.getErros());
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo na base de dados.");
		} 
		return arquivoSalvo;

	}

	private void setDevolvidoPelaCRA(Remessa remessa) {
		if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			remessa.setDevolvidoPelaCRA(false);
		}
	}

	private TituloRemessa getTituloDesistenciaProtesto(PedidoDesistenciaCancelamento pedido) {
		return tituloDAO.buscarTituloDesistenciaProtesto(pedido.getNumeroProtocolo(), pedido.getNumeroTitulo(),
		        pedido.getDataProtocolagem(), pedido.getValorTitulo());
	}

	private void verificaInstituicaoRecebe(Arquivo arquivo) {
		if (arquivo.getInstituicaoRecebe() == null) {
			arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao("CRA"));
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

	private void setSituacaoRemessa(Arquivo arquivo, Remessa remessa) {
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			remessa.setSituacao(false);
			if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				remessa.setSituacaoBatimento(false);
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Arquivo> buscarArquivosAvancadoConfirmacao(String nomeArquivo, Instituicao instituicao) {
		Criteria criteria = getCriteria(Arquivo.class);

		criteria.add(Restrictions.ilike("nomeArquivo", nomeArquivo, MatchMode.EXACT));
		criteria.createAlias("statusArquivo", "statusArquivo");
		criteria.add(Restrictions.eq("statusArquivo.situacaoArquivo", SituacaoArquivo.AGUARDANDO));

		List<Arquivo> arquivos = criteria.list();
		if (arquivos != null) {
			for (Arquivo arquivo : arquivos) {
				Hibernate.initialize(arquivo);
				for (Remessa remessa : arquivo.getRemessaBanco()) {
					remessa.setTitulos(buscarTituloConfirmacaoBanco(remessa));
				}
			}
		}

		return arquivos;
	}

	private List<Titulo> buscarTituloConfirmacaoBanco(Remessa remessa) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("confirmacao", "confirmacao");
		criteria.add(Restrictions.eq("confirmacao.remessa", remessa));

		return criteria.list();
	}

	public List<Arquivo> buscarArquivosAvancado(Arquivo arquivo, Usuario usuario, ArrayList<TipoArquivoEnum> tipoArquivos,
	        Municipio municipio, LocalDate dataInicio, LocalDate dataFim, ArrayList<SituacaoArquivo> situacoes) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("instituicaoEnvio", "instituicaoEnvio");
		criteria.createAlias("tipoArquivo", "tipoArquivo");
		criteria.createAlias("instituicaoEnvio.tipoInstituicao", "tipoInstituicao");
		criteria.add(Restrictions.ne("tipoInstituicao.tipoInstituicao", TipoInstituicaoCRA.CARTORIO));
		criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", usuario.getInstituicao()), Restrictions.eq("instituicaoRecebe", usuario.getInstituicao())));

		if (arquivo.getInstituicaoEnvio() != null) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", arquivo.getInstituicaoEnvio()), Restrictions.eq("instituicaoRecebe", arquivo.getInstituicaoEnvio())));
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

	/**
	 * Verifica se o arquivo já foi enviado para CRA
	 * 
	 * @param instituicao
	 * @param nomeArquivo
	 * @return
	 */
	public Arquivo buscarArquivosPorNomeArquivoInstituicaoEnvio(Instituicao instituicao, String nomeArquivo) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicao));
		criteria.add(Restrictions.eq("nomeArquivo", nomeArquivo));

		return Arquivo.class.cast(criteria.uniqueResult());
	}

	public Arquivo alterarSituacaoArquivo(Arquivo arquivo) {
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

	@Transactional(readOnly=true)
	public List<Remessa> buscarRemessasArquivo(Instituicao instituicao, Arquivo arquivo) {
		Arquivo arquivoBuscado = buscarPorPK(arquivo);
		Criteria criteria = getCriteria(Remessa.class);
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			criteria.add(Restrictions.eq("arquivo", arquivo));
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			criteria.add(Restrictions.eq("arquivoGeradoProBanco", arquivo));
		}
		List<Remessa> remessas = criteria.list();

		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(Titulo.class);
			List<Titulo> titulos = new ArrayList<Titulo>();

			criteriaTitulo.createAlias("remessa", "remessa");
			criteriaTitulo.add(Restrictions.eq("remessa", remessa));
			titulos = criteriaTitulo.list();

			remessa.setTitulos(new ArrayList<Titulo>());
			if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				for (Titulo titulo : titulos) {
					if (titulo.getTipoOcorrencia() != null) {
						if (titulo.getTipoOcorrencia().equals("5") ) {
							titulo.setValorGravacaoEletronica(BigDecimal.ZERO);
						} else {
							titulo.setValorGravacaoEletronica(arquivoBuscado.getInstituicaoRecebe().getValorConfirmacao());
						}
					}
				}
			}
			if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				if (arquivoBuscado.getInstituicaoRecebe().getCodigoCompensacao().equals("237")) { // SOMENTE PARA BRADESCO
					for (Titulo titulo : titulos) {
						TituloSemTaxaCRA tituloSemTaxa = tituloSemTaxaCraDAO.verificarTituloEnviadoSemTaxa(titulo);
						if (tituloSemTaxa != null) {
							if (titulo.getTipoOcorrencia() != null) {
								if (titulo.getTipoOcorrencia().equals("5") || tituloSemTaxa.getProtocolo().equals("0") ) {
									titulo.setValorGravacaoEletronica(BigDecimal.ZERO);
								} else {
									titulo.setValorGravacaoEletronica(arquivoBuscado.getInstituicaoRecebe().getValorConfirmacao());
								}
							}	
						}
					}
				}
			}
			remessa.getTitulos().addAll(titulos);
		}
		return remessas;
	}

	public List<Remessa> getRemessasArquivo(Arquivo arquivo, Instituicao instituicao) {
		Criteria criteria = getCriteria(Remessa.class);
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			criteria.add(Restrictions.eq("arquivo", arquivo));
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			criteria.add(Restrictions.eq("arquivoGeradoProBanco", arquivo));
		}
		return criteria.list();
	}

	public Arquivo buscarArquivoPorNome(Instituicao instituicao, String nomeArquivo) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.ilike("nomeArquivo", nomeArquivo, MatchMode.EXACT));
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicao));
		return Arquivo.class.cast(criteria.uniqueResult());
	}

	public List<Arquivo> buscarArquivosPorNome(Instituicao instituicao, Arquivo arquivo) {
		Criteria criteria = getCriteria(Arquivo.class);
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", instituicao),
			        Restrictions.eq("instituicaoRecebe", instituicao)));
		}
		criteria.add(Restrictions.ilike("nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
		return criteria.list();
	}

	public List<Remessa> getRemessasConfirmacoesRecebidas() {
		if (remessasConfirmacoesRecebidas == null) {
			remessasConfirmacoesRecebidas = new ArrayList<Remessa>();
		}
		return remessasConfirmacoesRecebidas;
	}
}
