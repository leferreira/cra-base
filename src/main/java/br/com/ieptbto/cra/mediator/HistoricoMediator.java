package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.bean.TituloOcorrenciaBean;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class HistoricoMediator extends BaseMediator {

	@Autowired
	TituloFiliadoMediator tituloFiliadoMediator;
	@Autowired
	RemessaMediator remessaMediator;
	@Autowired
	TituloMediator tituloMediator;
	@Autowired
	DesistenciaProtestoMediator desistenciaMediator;
	@Autowired
	CancelamentoProtestoMediator cancelamentoProtestoMediator;
	@Autowired
	AutorizacaoCancelamentoMediator autorizacaoCancelamentoMediator;
	@Autowired
	BatimentoMediator batimentoMediator;
	@Autowired
	InstrumentoProtestoMediator instrumentoMediator;
	@Autowired
	RetornoMediator retornoMediator;

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

		ocorrenciaTituloFiliado(titulo);
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
			remessa = remessaMediator.carregarRemessaPorId(titulo.getRemessa());
		}
		addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToRemessa(remessa));
	}

	private void ocorrenciaConfirmacao(TituloRemessa titulo) {
		Confirmacao confirmacao = titulo.getConfirmacao();
		if (confirmacao != null) {
			confirmacao = tituloMediator.carregarTituloConfirmacao(confirmacao);
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
			retorno = tituloMediator.carregarTituloRetorno(retorno);
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
		List<SolicitacaoDesistenciaCancelamento> solicitacoesDesistenciasCancelamentos =
						cancelamentoProtestoMediator.buscarSolicitacoesDesistenciasCancelamentoPorTitulo(titulo);
		if (solicitacoesDesistenciasCancelamentos != null && !solicitacoesDesistenciasCancelamentos.isEmpty()) {
			for (SolicitacaoDesistenciaCancelamento solicitacao : solicitacoesDesistenciasCancelamentos) {
				addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToSolicitacaoDesistenciaCancelamento(solicitacao));
			}
		}
	}

	private void ocorrenciaAutorizacoes(TituloRemessa titulo) {
		if (titulo.getPedidosAutorizacaoCancelamento() != null) {
			List<PedidoAutorizacaoCancelamento> pedidosAutorizacao =
							autorizacaoCancelamentoMediator.buscarPedidosAutorizacaoCancelamentoPorTitulo(titulo);
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

	private void ocorrenciaTituloFiliado(TituloRemessa titulo) {
		if (new Integer(titulo.getCodigoPortador()) > 799) {
			TituloFiliado tituloFiliado =
							tituloFiliadoMediator.buscarTituloFiliadoProcessadoNaCra(titulo.getNossoNumero(), titulo.getNumeroTitulo());
			if (tituloFiliado != null) {
				addOcorrencia(TituloOcorrenciaBean.getOcorrenciaToTituloFiliado(tituloFiliado));
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