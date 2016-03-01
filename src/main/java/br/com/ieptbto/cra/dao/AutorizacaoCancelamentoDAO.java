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
import br.com.ieptbto.cra.exception.AutorizacaoCancelamentoException;
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

	public Arquivo salvarAutorizacaoCancelamentoSerpro(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		Arquivo arquivoSalvo = new Arquivo();
		Transaction transaction = getSession().beginTransaction();

		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));
			
			arquivoSalvo = save(arquivo);
			if (arquivo.getRemessaAutorizacao() != null) {
				List<PedidoAutorizacaoCancelamento> pedidosACComErros = new ArrayList<PedidoAutorizacaoCancelamento>();
				List<AutorizacaoCancelamento> autorizacoesCancelamentos = new ArrayList<AutorizacaoCancelamento>();
				BigDecimal valorTotalDesistenciaProtesto = BigDecimal.ZERO;
				int totalCancelamentoProtesto = 0;
				int totalRegistroCancelamentoProtesto = 0;
	
				for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
					List<PedidoAutorizacaoCancelamento> pedidosAC = new ArrayList<PedidoAutorizacaoCancelamento>();
					ac.setRemessaAutorizacaoCancelamento(arquivo.getRemessaAutorizacao());
					ac.setDownload(false);
					for (PedidoAutorizacaoCancelamento pedido : ac.getAutorizacoesCancelamentos()) {
						pedido.setAutorizacaoCancelamento(ac);
						pedido.setTitulo(tituloDAO.buscarTituloAutorizacaoCancelamento(pedido));
						if (pedido.getTitulo() != null) {
							if (pedido.getTitulo().getPedidoAutorizacaoCancelamento() == null) {
								pedidosAC.add(pedido);
								valorTotalDesistenciaProtesto = valorTotalDesistenciaProtesto.add(pedido.getValorTitulo());
								totalRegistroCancelamentoProtesto++;
							} else {
								pedidosACComErros.add(pedido);
								erros.add(new InfraException("Linha " + pedido.getSequenciaRegistro() + ": o título de número "+ pedido.getNumeroTitulo() + ", do protocolo " + pedido.getNumeroProtocolo() + " do dia "
								        + DataUtil.localDateToString(pedido.getDataProtocolagem())+ ", já foi enviado anteriormente em outro arquivo de autorização cancelamento!"));
							}
						} else if (pedido.getDataProtocolagem().isAfter(DataUtil.stringToLocalDate("dd/MM/yyyy", "01/12/2015")) ||
								pedido.getDataProtocolagem().equals(DataUtil.stringToLocalDate("dd/MM/yyyy", "01/12/2015"))) {
							pedidosACComErros.add(pedido);
							erros.add(new InfraException("Linha " + pedido.getSequenciaRegistro() + ": o título de número "+ pedido.getNumeroTitulo() + ",com o protocolo " + pedido.getNumeroProtocolo() + " do dia "
							        + DataUtil.localDateToString(pedido.getDataProtocolagem())+ ", não foi localizado para a comarca [ "+ pedido.getAutorizacaoCancelamento().getCabecalhoCartorio().getCodigoMunicipio() +" ]. Verifique os dados do título!"));
						} else {
							pedidosAC.add(pedido);
							valorTotalDesistenciaProtesto = valorTotalDesistenciaProtesto.add(pedido.getValorTitulo());
							totalRegistroCancelamentoProtesto++;
						}
					}
					if (!pedidosAC.isEmpty()) {
						ac.getCabecalhoCartorio().setQuantidadeDesistencia(pedidosAC.size());
						ac.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(pedidosAC.size());
						ac.setAutorizacoesCancelamentos(pedidosAC);
						autorizacoesCancelamentos.add(ac);
						totalCancelamentoProtesto++;
					}
				}
				arquivo.getRemessaAutorizacao().getCabecalho().setQuantidadeDesistencia(totalCancelamentoProtesto);
				arquivo.getRemessaAutorizacao().getCabecalho().setQuantidadeRegistro(totalRegistroCancelamentoProtesto);
				arquivo.getRemessaAutorizacao().getRodape().setQuantidadeDesistencia(totalCancelamentoProtesto);
				arquivo.getRemessaAutorizacao().getRodape().setSomatorioValorTitulo(valorTotalDesistenciaProtesto);
				arquivo.getRemessaAutorizacao().setAutorizacaoCancelamento(autorizacoesCancelamentos);
				arquivo.getRemessaAutorizacao().setCabecalho(save(arquivo.getRemessaAutorizacao().getCabecalho()));
				arquivo.getRemessaAutorizacao().setRodape(save(arquivo.getRemessaAutorizacao().getRodape()));
				save(arquivo.getRemessaAutorizacao());
	
				for (AutorizacaoCancelamento ac : autorizacoesCancelamentos) {
					ac.getCabecalhoCartorio().setQuantidadeDesistencia(ac.getAutorizacoesCancelamentos().size());
					ac.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(totalCancelamentoProtesto);
					ac.setCabecalhoCartorio(save(ac.getCabecalhoCartorio()));
					ac.setRodapeCartorio(save(ac.getRodapeCartorio()));
					ac.setRemessaAutorizacaoCancelamento(ac.getRemessaAutorizacaoCancelamento());
					save(ac);
					for (PedidoAutorizacaoCancelamento pedido : ac.getAutorizacoesCancelamentos()) {
						save(pedido);
					}
				}
				if (!erros.isEmpty()) {
					throw new AutorizacaoCancelamentoException("Não foi possível enviar o arquivo de autorização de cancelamento! Por favor, corriga os erros no arquivo abaixo...", erros ,pedidosACComErros);
				}
				transaction.commit();
			}
			logger.info("O arquivo " + arquivo.getNomeArquivo() + "enviado pelo usuário " + arquivo.getUsuarioEnvio().getLogin()
			        + " foi inserido na base ");

		} catch (AutorizacaoCancelamentoException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new AutorizacaoCancelamentoException(ex.getMessage(), ex.getErros(), ex.getPedidosAutorizacaoCancelamento());
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
	public List<AutorizacaoCancelamento> buscarAutorizacaoCancelamento(Arquivo arquivo, Instituicao portador,
			Municipio municipio, LocalDate dataInicio, LocalDate dataFim, ArrayList<TipoArquivoEnum> tiposArquivo, Usuario usuario) {
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
}
