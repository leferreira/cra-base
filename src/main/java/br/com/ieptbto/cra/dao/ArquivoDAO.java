package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.view.ViewArquivoPendente;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
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
	private TituloDAO tituloDAO;

	/**
	 * Salvar arquivo
	 * 
	 * @param arquivo
	 * @param usuario
	 * @param erros
	 * @return
	 */
	public Arquivo salvar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		Arquivo arquivoProcessado = arquivo;
		Transaction transaction = getSession().beginTransaction();

		TipoArquivoFebraban tipoArquivo = arquivo.getTipoArquivo().getTipoArquivo();
		try {
			StatusArquivo statusArquivo = save(arquivo.getStatusArquivo());
			arquivo.setStatusArquivo(statusArquivo);
			arquivo = save(arquivo);

			if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo) || TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)
					|| TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {

				salvarRemessaConfirmacaoRetorno(arquivo, usuario, erros, transaction);
				if (!erros.isEmpty()) {
					if (arquivo.getId() != 0) {
						flush();
					}
					transaction.rollback();
					return arquivoProcessado;
				}
				transaction.commit();

			} else if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
				salvarDesistenciaProtesto(arquivo, usuario, erros, transaction);
				transaction.commit();
				loggerCra.sucess(usuario, CraAcao.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO,
                        "Arquivo " + arquivo.getNomeArquivo() + ", enviado por " + arquivo.getInstituicaoEnvio().getNomeFantasia() + ", recebido com sucesso.");
			} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
				throw new InfraException("Não foi possivel enviar o Cancelamento de Protesto! Entre em contato com a CRA!");
			} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
				throw new InfraException("Não foi possivel enviar a Autorização de Cancelamento! Entre em contato com a CRA!");
			}

		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
		}
		return arquivo;
	}

	private void salvarRemessaConfirmacaoRetorno(Arquivo arquivo, Usuario usuario, List<Exception> erros, Transaction transaction) {
		BigDecimal valorTotalSaldo = BigDecimal.ZERO;
		Boolean retornoContemTituloPago = false;

		for (Remessa remessa : arquivo.getRemessas()) {
			remessa.setArquivo(arquivo);
			remessa.setCabecalho(save(remessa.getCabecalho()));
			remessa.setRodape(save(remessa.getRodape()));
			remessa.setArquivoGeradoProBanco(arquivo);
			remessa.setDataRecebimento(new LocalDate());
			remessa.setInstituicaoOrigem(arquivo.getInstituicaoEnvio());
			remessa.setDevolvidoPelaCRA(false);
			remessa.setStatusRemessaPorTipoInstituicaoEnvio();
			remessa.setConfirmacaoRetornoPendenteLiberacao();
			save(remessa);
			for (Titulo titulo : remessa.getTitulos()) {
				titulo.setRemessa(remessa);
				if (Retorno.class.isInstance(titulo)) {
					if (titulo.getTipoOcorrencia().equals(TipoOcorrencia.PAGO.getConstante())) {
						retornoContemTituloPago = true;
					}
					Retorno.class.cast(titulo).setCabecalho(remessa.getCabecalho());
				}
				TituloRemessa tituloSalvo = tituloDAO.salvar(arquivo.getInstituicaoEnvio(), titulo, erros, transaction);
				if (TituloRemessa.class.isInstance(titulo)) {
					if (TituloRemessa.class.cast(titulo).getAnexos() != null) {
						for (Anexo anexo : TituloRemessa.class.cast(titulo).getAnexos()) {

							tituloSalvo.setAnexos(new ArrayList<Anexo>());
							tituloSalvo.getAnexos().add(anexo);
							anexo.setTitulo(tituloSalvo);
							save(anexo);
						}
					}
				}
				if (tituloSalvo != null) {
					valorTotalSaldo = valorTotalSaldo.add(titulo.getSaldoTitulo());
				}
			}
			if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.RETORNO)) {
				if (retornoContemTituloPago.equals(false) || remessa.getInstituicaoDestino().getTipoBatimento()
						.equals(TipoBatimento.LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO)) {
					remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.CONFIRMADO);
				} else {
					remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.NAO_CONFIRMADO);
				}
				update(remessa);
			}
			remessa.getCabecalho().setQtdTitulosRemessa(remessa.getTitulos().size());
			remessa.getRodape().setSomatorioValorRemessa(valorTotalSaldo);
			remessa.setCabecalho(save(remessa.getCabecalho()));
			remessa.setRodape(save(remessa.getRodape()));
		}
	}

	private void salvarDesistenciaProtesto(Arquivo arquivo, Usuario usuario, List<Exception> erros, Transaction transaction) {
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
	}


	/**
	 * Atualizar dados de status do arquivo
	 * @param arquivo
	 * @return
	 */
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
	public Arquivo buscarArquivoInstituicaoRemessa(String nomeArquivo, Instituicao instituicaoEnvio) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.ilike("nomeArquivo", nomeArquivo, MatchMode.EXACT));
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicaoEnvio));

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

	public List<Arquivo> buscarArquivos(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim,
			TipoInstituicaoCRA tipoInstituicao, Instituicao bancoConvenio, List<TipoArquivoFebraban> tiposArquivo,
			List<StatusDownload> situacoesArquivos) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("tipoArquivo", "tipoArquivo");

		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", usuario.getInstituicao()),
					Restrictions.eq("instituicaoRecebe", usuario.getInstituicao())));
		}
		if (bancoConvenio != null) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", bancoConvenio),
					Restrictions.eq("instituicaoRecebe", bancoConvenio)));
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			criteria.createAlias("instituicaoEnvio", "instituicaoEnvio");
			criteria.createAlias("instituicaoEnvio.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		if (!situacoesArquivos.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			criteria.createAlias("statusArquivo", "statusArquivo");
			for (StatusDownload status : situacoesArquivos) {
				disjunction.add(Restrictions.eq("statusArquivo.statusDownload", status));
			}
			criteria.add(disjunction);
		}
		if (nomeArquivo != null && StringUtils.isNotEmpty(nomeArquivo)) {
			criteria.add(Restrictions.ilike("nomeArquivo", nomeArquivo, MatchMode.ANYWHERE));
		}
		if (!tiposArquivo.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			for (TipoArquivoFebraban tipoArquivo : tiposArquivo) {
				disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipoArquivo));
			}
			criteria.add(disjunction);
		}
		if (dataInicio != null) {
			criteria.add(Restrictions.between("dataEnvio", dataInicio, dataFim));
		}
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO));
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO));
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO));
		criteria.addOrder(Order.desc("dataEnvio"));
		return criteria.list();
	}
	
	public List<Arquivo> buscarRetornoParaLayoutRecebimentoEmpresa(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
		List<Arquivo> results = new ArrayList<>();

		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa", JoinType.INNER_JOIN);
		criteria.createAlias("remessa.arquivoGeradoProBanco", "a", JoinType.INNER_JOIN);

		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("a.instituicaoEnvio", usuario.getInstituicao()),
					Restrictions.eq("a.instituicaoRecebe", usuario.getInstituicao())));
		}
		if (dataInicio != null) {
			criteria.add(Restrictions.between("a.dataEnvio", dataInicio, dataFim));
		}
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.setProjection(Projections.groupProperty("remessa.arquivoGeradoProBanco"));
		
		Iterator iterate = criteria.list().iterator();
		while (iterate.hasNext()) {
			Object object = (Object) iterate.next();
			Arquivo arquivo = Arquivo.class.cast(object);
			
			Criteria criteriaArquivo = getCriteria(Arquivo.class);
			criteriaArquivo.add(Restrictions.eq("id", arquivo.getId()));
			results.add(Arquivo.class.cast(criteriaArquivo.uniqueResult()));
		}
		return results;
	}

	public List<Arquivo> buscarArquivosDesistenciaCancelamento(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim,
			TipoInstituicaoCRA tipoInstituicao, Instituicao bancoConvenio, List<TipoArquivoFebraban> tiposArquivo,
			List<StatusDownload> situacoesArquivos) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("tipoArquivo", "tipoArquivo");

		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", usuario.getInstituicao()),
					Restrictions.eq("instituicaoRecebe", usuario.getInstituicao())));
		}
		if (bancoConvenio != null) {
			criteria.add(Restrictions.or(Restrictions.eq("instituicaoEnvio", bancoConvenio),
					Restrictions.eq("instituicaoRecebe", bancoConvenio)));
		}
		if (tipoInstituicao != null && bancoConvenio == null) {
			criteria.createAlias("instituicaoEnvio", "instituicaoEnvio");
			criteria.createAlias("instituicaoEnvio.tipoInstituicao", "tipoInstituicao");
			criteria.add(Restrictions.eq("tipoInstituicao.tipoInstituicao", tipoInstituicao));
		}
		if (!situacoesArquivos.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			criteria.createAlias("statusArquivo", "statusArquivo");
			for (StatusDownload status : situacoesArquivos) {
				disjunction.add(Restrictions.eq("statusArquivo.statusDownload", status));
			}
			criteria.add(disjunction);
		}
		if (nomeArquivo != null && StringUtils.isNotEmpty(nomeArquivo)) {
			criteria.add(Restrictions.ilike("nomeArquivo", nomeArquivo, MatchMode.ANYWHERE));
		}
		if (!tiposArquivo.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			for (TipoArquivoFebraban tipoArquivo : tiposArquivo) {
				disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", tipoArquivo));
			}
			criteria.add(disjunction);
		}
		if (dataInicio != null) {
			criteria.add(Restrictions.between("dataEnvio", dataInicio, dataFim));
		}
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.REMESSA));
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.CONFIRMACAO));
		criteria.add(Restrictions.ne("tipoArquivo.tipoArquivo", TipoArquivoFebraban.RETORNO));
		criteria.addOrder(Order.desc("dataEnvio"));
		return criteria.list();
	}

	@Transactional(readOnly = true)
	public Arquivo buscarArquivoPorNomeInstituicaoEnvio(Instituicao instituicao, String nomeArquivo) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicao));
		criteria.add(Restrictions.eq("nomeArquivo", nomeArquivo));
		criteria.setMaxResults(1);
		return Arquivo.class.cast(criteria.uniqueResult());
	}
	
	@Transactional(readOnly = true)
	public Arquivo buscarArquivoEnviadoDataAtual(Instituicao instituicao) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.eq("instituicaoEnvio", instituicao));
		criteria.add(Restrictions.eq("dataEnvio", new LocalDate()));
		criteria.setMaxResults(1);
		return Arquivo.class.cast(criteria.uniqueResult());
	}
	
	/**
	 * @param instituicao
	 * @return 
	 */
	public List<ViewArquivoPendente> consultarArquivosPendentes(Instituicao instituicao) {
		List<ViewArquivoPendente> resultados = new ArrayList<>();
		
		Query query = getSession().getNamedQuery("findRemessasPendentes");
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findDesistenciasPendentes");
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findCancelamentosPendentes");
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findAutorizacoesPendentes");
		resultados.addAll(query.list());
		return resultados;
	}

	/**
	 * @param instituicao
	 * @return
	 */
	public List<ViewArquivoPendente> consultarArquivosPendentesCartorio(Instituicao instituicao) {
		List<ViewArquivoPendente> resultados = new ArrayList<>();
			
		Query query = getSession().getNamedQuery("findRemessasPendentesCartorio");
		query.setParameter("id", instituicao.getId());
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findDesistenciasPendentesCartorio");
		query.setParameter("id", instituicao.getId());
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findCancelamentosPendentesCartorio");
		query.setParameter("id", instituicao.getId());
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findAutorizacoesPendentesCartorio");
		query.setParameter("id", instituicao.getId());
		resultados.addAll(query.list());
		return resultados;
	}

	/**
	 * @param instituicao
	 * @return
	 */
	public List<ViewArquivoPendente> consultarArquivosPendentesBancoConvenio(Instituicao instituicao) {
		List<ViewArquivoPendente> resultados = new ArrayList<>();
		
		Query query = getSession().getNamedQuery("findRemessasPendentesInstituicao");
		query.setParameter("id", instituicao.getId());
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findDesistenciasPendentesInstituicao");
		query.setParameter("id", instituicao.getId());
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findCancelamentosPendentesInstituicao");
		query.setParameter("id", instituicao.getId());
		resultados.addAll(query.list());
		
		query = getSession().getNamedQuery("findAutorizacoesPendentesInstituicao");
		query.setParameter("id", instituicao.getId());
		resultados.addAll(query.list());
		return resultados;
	}

	/**
	 * Busca o sequencial de arquivo de acordo com a quantidade 
	 * retorno gerado para a instituição cadastrada com o Layout de Arrecadação e Recebimento
	 * @param dataEnvio
	 * @return
	 */
	public Integer buscarSequencialRetornoRecebimentoEmpresa(Instituicao instituicaoRecebe, LocalDate dataEnvio) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.createAlias("tipoArquivo", "tipoArquivoFebraban");
		criteria.add(Restrictions.eq("instituicaoRecebe", instituicaoRecebe));
		criteria.add(Restrictions.eq("tipoArquivoFebraban.tipoArquivo", TipoArquivoFebraban.RETORNO));
		criteria.add(Restrictions.le("dataEnvio", dataEnvio));
		criteria.setProjection(Projections.count("id"));
		Long sequencial = Long.class.cast(criteria.uniqueResult());
		return (sequencial == 0) ? 1 : sequencial.intValue();
	}
}
