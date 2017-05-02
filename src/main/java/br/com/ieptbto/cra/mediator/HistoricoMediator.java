package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.beans.TituloOcorrenciaBean;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class HistoricoMediator extends BaseMediator {

	@Autowired
    private RemessaMediator remessaMediator;
	@Autowired
    private ConfirmacaoMediator confirmacaoMediator;
	@Autowired
    private RetornoMediator retornoMediator;
	@Autowired
    private DesistenciaProtestoMediator desistenciaMediator;
	@Autowired
    private CancelamentoProtestoMediator cancelamentoProtestoMediator;
	@Autowired
    private AutorizacaoCancelamentoMediator autorizacaoCancelamentoMediator;
	@Autowired
    private SolicitacaoDesistenciaCancelamentoMediator solicitacaoMediator;
	@Autowired
    private BatimentoMediator batimentoMediator;
	@Autowired
    private InstrumentoProtestoMediator instrumentoMediator;

	private List<TituloOcorrenciaBean> ocorrenciasTitulo;

	/**
	 * Carrega todas as ocorrencias do título com relação a arquivo de remessa,
	 * confirmação, retorno, batimentos, depósitos, Slips, arquivos de
	 * desistências e cancelamentos e solicitações dentro de um objeto genérico
	 * 
	 * @param titulo
	 * @return
	 */
	public List<TituloOcorrenciaBean> carregarOrrenciasTitulo(TituloRemessa titulo) {
		this.ocorrenciasTitulo = new ArrayList<TituloOcorrenciaBean>();

		ocorrenciaRemessa(titulo);
		ocorrenciaConfirmacao(titulo);
		ocorrenciaRetornoBatimentoDepositosSlips(titulo);
		ocorrenciaDesistencias(titulo);
		ocorrenciaCancelamentos(titulo);
		ocorrenciaAutorizacoes(titulo);
		ocorrenciaSolicitacaoDesistenciaCancelamento(titulo);
		return getOcorrenciasTitulo();
	}

	private void ocorrenciaRemessa(TituloRemessa titulo) {
		Remessa remessa = titulo.getRemessa();
		if (remessa != null) {
			remessa = remessaMediator.buscarRemessaPorPK(titulo.getRemessa());
		}
		addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToRemessa(remessa));
	}

	private void ocorrenciaConfirmacao(TituloRemessa titulo) {
		Confirmacao confirmacao = titulo.getConfirmacao();
		if (confirmacao != null) {
			confirmacao = confirmacaoMediator.carregarTituloConfirmacao(confirmacao);
			addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToRemessa(confirmacao.getRemessa()));

			// Confirmação liberada para banco
			if (confirmacao.getRemessa().getSituacao() == true) {
				addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToArquivo(confirmacao.getRemessa().getArquivoGeradoProBanco()));
			}
		}
	}

	private void ocorrenciaRetornoBatimentoDepositosSlips(TituloRemessa titulo) {
		Retorno retorno = titulo.getRetorno();
		if (retorno != null) {
			retorno = retornoMediator.carregarTituloRetorno(retorno);
			addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToRemessa(retorno.getRemessa()));

			if (retorno.getRemessa().getBatimento() != null) {
				Batimento batimento = retorno.getRemessa().getBatimento();

				if (retorno.getRemessa().getSituacaoBatimentoRetorno().equals(SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO)
								|| retorno.getRemessa().getSituacaoBatimentoRetorno().equals(SituacaoBatimentoRetorno.CONFIRMADO)) {
					List<Deposito> depositos = batimentoMediator.buscarDepositosPorBatimento(batimento);
					BigDecimal valorPagos = retornoMediator.buscarValorDeTitulosPagos(retorno.getRemessa());

					addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToBatimento(batimento, depositos, valorPagos));
				}
			}

			if (retorno.getTipoOcorrencia().equals(TipoOcorrencia.PROTESTADO.getConstante())) {
				InstrumentoProtesto instrumentoProtesto = instrumentoMediator.isTituloJaFoiGeradoInstrumento(retorno);
				if (instrumentoProtesto != null) {
					addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToInstrumentoProtesto(instrumentoProtesto));
				}
			}

			// Retorno liberado para bancos
			if (retorno.getRemessa().getSituacao() == true) {
				addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToArquivo(retorno.getRemessa().getArquivoGeradoProBanco()));
			}
		}
	}

	private void ocorrenciaDesistencias(TituloRemessa titulo) {
		if (titulo.getPedidosDesistencia() != null) {
			List<PedidoDesistencia> pedidoDesistencias = desistenciaMediator.buscarPedidosDesistenciaProtestoPorTitulo(titulo);
			for (PedidoDesistencia pedido : pedidoDesistencias) {
				addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToDesistenciaProtesto(pedido.getDesistenciaProtesto()));
			}
		}
	}

	private void ocorrenciaSolicitacaoDesistenciaCancelamento(TituloRemessa titulo) {
		List<SolicitacaoDesistenciaCancelamento> solicitacoesDesistenciasCancelamentos = solicitacaoMediator.buscarSolicitacoesDesistenciasCancelamentoPorTitulo(titulo);
		if (solicitacoesDesistenciasCancelamentos != null && !solicitacoesDesistenciasCancelamentos.isEmpty()) {
			for (SolicitacaoDesistenciaCancelamento solicitacao : solicitacoesDesistenciasCancelamentos) {
				addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToSolicitacaoDesistenciaCancelamento(solicitacao));
			}
		}
	}

	private void ocorrenciaAutorizacoes(TituloRemessa titulo) {
		if (titulo.getPedidosAutorizacaoCancelamento() != null) {
			List<PedidoAutorizacaoCancelamento> pedidosAutorizacao = autorizacaoCancelamentoMediator.buscarPedidosAutorizacaoCancelamentoPorTitulo(titulo);
			for (PedidoAutorizacaoCancelamento pedido : pedidosAutorizacao) {
				addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToAutorizacaoCanlamento(pedido.getAutorizacaoCancelamento()));
			}
		}

	}

	private void ocorrenciaCancelamentos(TituloRemessa titulo) {
		if (titulo.getPedidosCancelamento() != null) {
			List<PedidoCancelamento> pedidoCancelamento = cancelamentoProtestoMediator.buscarPedidosCancelamentoProtestoPorTitulo(titulo);
			for (PedidoCancelamento pedido : pedidoCancelamento) {
				addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToCancelamentoProtesto(pedido.getCancelamentoProtesto()));
			}
		}
	}

	private List<TituloOcorrenciaBean> getOcorrenciasTitulo() {
		if (ocorrenciasTitulo != null) {
			Collections.sort(ocorrenciasTitulo);
		}
		return ocorrenciasTitulo;
	}

	private void addOcorrencia(TituloOcorrenciaBean arquivoOcorrencia) {
		getOcorrenciasTitulo().add(arquivoOcorrencia);
	}
}