package br.com.ieptbto.cra.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso
 *
 */
public class TituloOcorrenciaBean implements Serializable, Comparable<TituloOcorrenciaBean> {

	/***/
	private static final long serialVersionUID = 1L;
	private LocalDate data;
	private LocalTime hora;
	private String nomeUsuario;
	private String mensagem;
	private Arquivo arquivo;
	private Remessa remessa;
	private DesistenciaProtesto desistenciaProtesto;
	private CancelamentoProtesto cancelamentoProtesto;
	private AutorizacaoCancelamento autorizacaoCancelamento;
	private SolicitacaoDesistenciaCancelamento solicitacaoCancelamento;
	private Batimento batimento;
	private InstrumentoProtesto instrumentoProtesto;
	private List<Deposito> depositos;
	private TituloFiliado tituloFiliado;

	/**
	 * @param arquivo
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToArquivo(Arquivo arquivo) {
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();

		arquivoOcorrencia.setArquivo(arquivo);
		arquivoOcorrencia.setData(arquivo.getDataEnvio());
		arquivoOcorrencia.setHora(arquivo.getHoraEnvio());
		arquivoOcorrencia.setNomeUsuario(arquivo.getUsuarioEnvio().getNome());
		return arquivoOcorrencia;
	}

	/**
	 * @param remessa
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToRemessa(Remessa remessa) {
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();

		arquivoOcorrencia.setRemessa(remessa);
		arquivoOcorrencia.setData(remessa.getArquivo().getDataEnvio());
		arquivoOcorrencia.setHora(remessa.getArquivo().getHoraEnvio());
		arquivoOcorrencia.setNomeUsuario(remessa.getArquivo().getUsuarioEnvio().getNome());
		return arquivoOcorrencia;
	}

	/**
	 * @param desistencia
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToDesistenciaProtesto(DesistenciaProtesto desistencia) {
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();

		Arquivo arquivoDesistencia = desistencia.getRemessaDesistenciaProtesto().getArquivo();
		arquivoOcorrencia.setDesistenciaProtesto(desistencia);
		arquivoOcorrencia.setData(arquivoDesistencia.getDataEnvio());
		arquivoOcorrencia.setHora(arquivoDesistencia.getHoraEnvio());
		arquivoOcorrencia.setNomeUsuario(arquivoDesistencia.getUsuarioEnvio().getNome());
		return arquivoOcorrencia;
	}

	/**
	 * @param cancelamento
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToCancelamentoProtesto(CancelamentoProtesto cancelamento) {
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();

		Arquivo arquivoCancelamento = cancelamento.getRemessaCancelamentoProtesto().getArquivo();
		arquivoOcorrencia.setCancelamentoProtesto(cancelamento);
		arquivoOcorrencia.setData(arquivoCancelamento.getDataEnvio());
		arquivoOcorrencia.setHora(arquivoCancelamento.getHoraEnvio());
		arquivoOcorrencia.setNomeUsuario(cancelamento.getRemessaCancelamentoProtesto().getArquivo().getUsuarioEnvio().getNome());
		return arquivoOcorrencia;
	}

	/**
	 * @param autorizacao
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToAutorizacaoCanlamento(
					AutorizacaoCancelamento autorizacao) {
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();

		Arquivo arquivoAutorizacao = autorizacao.getRemessaAutorizacaoCancelamento().getArquivo();
		arquivoOcorrencia.setAutorizacaoCancelamento(autorizacao);
		arquivoOcorrencia.setData(arquivoAutorizacao.getDataEnvio());
		arquivoOcorrencia.setHora(arquivoAutorizacao.getHoraEnvio());
		arquivoOcorrencia.setNomeUsuario(autorizacao.getRemessaAutorizacaoCancelamento().getArquivo().getUsuarioEnvio().getNome());
		return arquivoOcorrencia;
	}

	/**
	 * @param batimento
	 * @param depositos
	 * @param totalPagos
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToBatimento(Batimento batimento, List<Deposito> depositos,
					BigDecimal totalPagos) {
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();
		arquivoOcorrencia.setBatimento(batimento);

		DecimalFormat decimalFormat = new DecimalFormat("R$ #,##0.00");
		String mensagem = "<b>Depósitos vínculados:</b><br/>";
		for (Deposito deposito : depositos) {
			mensagem = mensagem.concat(DataUtil.localDateToString(deposito.getData()) + " - "
							+ decimalFormat.format(deposito.getValorCredito()) + ");<br>");
			arquivoOcorrencia.setNomeUsuario(deposito.getUsuario().getNome());
		}
		if (totalPagos != null) {
			mensagem = mensagem.concat("<b>Total Títulos (Pagos): <span style=\"color: red;\">R$ "
							+ decimalFormat.format(totalPagos) + "</span></b>");
		}
		arquivoOcorrencia.setMensagem(mensagem);
		if (depositos.isEmpty()) {
			arquivoOcorrencia.setMensagem("Liberação sem identificação de depósitos.");
			arquivoOcorrencia.setNomeUsuario("CRA");
		}
		arquivoOcorrencia.setData(batimento.getDataBatimento().toLocalDate());
		arquivoOcorrencia.setHora(batimento.getDataBatimento().toLocalTime());
		return arquivoOcorrencia;
	}

	/**
	 * @param instrumentoProtesto
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToInstrumentoProtesto(
					InstrumentoProtesto instrumentoProtesto) {
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();

		arquivoOcorrencia.setInstrumentoProtesto(instrumentoProtesto);
		if (instrumentoProtesto.getEtiquetaSlip() != null) {
			arquivoOcorrencia.setMensagem("Slip gerada com sucesso.");
			arquivoOcorrencia.setData(instrumentoProtesto.getEtiquetaSlip().getEnvelope().getDataGeracao());
			arquivoOcorrencia.setHora(instrumentoProtesto.getEtiquetaSlip().getEnvelope().getHoraGeracao());
			arquivoOcorrencia.setNomeUsuario(instrumentoProtesto.getUsuario().getNome());
		} else {
			arquivoOcorrencia.setMensagem("Slip aguardando geração.");
			arquivoOcorrencia.setData(instrumentoProtesto.getDataDeEntrada());
			arquivoOcorrencia.setHora(new LocalTime());
			arquivoOcorrencia.setNomeUsuario("");
		}
		return arquivoOcorrencia;
	}

	/**
	 * @param solicitacao
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToSolicitacaoDesistenciaCancelamento(
					SolicitacaoDesistenciaCancelamento solicitacao) {
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();

		arquivoOcorrencia.setSolicitacaoCancelamento(solicitacao);
		arquivoOcorrencia.setData(new LocalDate(solicitacao.getDataSolicitacao()));
		arquivoOcorrencia.setHora(solicitacao.getHoraSolicitacao());
		arquivoOcorrencia.setNomeUsuario(solicitacao.getUsuario().getNome());
		arquivoOcorrencia.setMensagem("Solicitação de " + solicitacao.getTipoSolicitacao().getDescricao());
		if (solicitacao.getCodigoIrregularidade() != null) {
			arquivoOcorrencia.setMensagem(arquivoOcorrencia.getMensagem() + ". <br><b>Motivo:</b> "
							+ solicitacao.getCodigoIrregularidade().getMotivo() + ".");
		}
		return arquivoOcorrencia;
	}

	/**
	 * @param tituloFiliado
	 * @return
	 */
	public static TituloOcorrenciaBean getOcorrenciaToTituloFiliado(TituloFiliado tituloFiliado) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
		TituloOcorrenciaBean arquivoOcorrencia = new TituloOcorrenciaBean();

		arquivoOcorrencia.setTituloFiliado(tituloFiliado);
		arquivoOcorrencia.setData(new LocalDate(tituloFiliado.getDataEntrada()));
		arquivoOcorrencia.setHora(new LocalTime(formatter.parseDateTime("01/01/2000 00:00:00")));
		arquivoOcorrencia.setNomeUsuario(tituloFiliado.getUsuarioEntradaManual().getNome());
		arquivoOcorrencia.setMensagem("Título cadastrado no IEPTB-Convênios.");
		return arquivoOcorrencia;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public LocalDate getData() {
		return data;
	}

	public LocalTime getHora() {
		return hora;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public SolicitacaoDesistenciaCancelamento getSolicitacaoCancelamento() {
		return solicitacaoCancelamento;
	}

	public DesistenciaProtesto getDesistenciaProtesto() {
		return desistenciaProtesto;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public void setHora(LocalTime hora) {
		this.hora = hora;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public void setSolicitacaoCancelamento(SolicitacaoDesistenciaCancelamento solicitacaoCancelamento) {
		this.solicitacaoCancelamento = solicitacaoCancelamento;
	}

	public void setDesistenciaProtesto(DesistenciaProtesto desistenciaProtesto) {
		this.desistenciaProtesto = desistenciaProtesto;
	}

	public CancelamentoProtesto getCancelamentoProtesto() {
		return cancelamentoProtesto;
	}

	public AutorizacaoCancelamento getAutorizacaoCancelamento() {
		return autorizacaoCancelamento;
	}

	public Batimento getBatimento() {
		return batimento;
	}

	public InstrumentoProtesto getInstrumentoProtesto() {
		return instrumentoProtesto;
	}

	public List<Deposito> getDepositos() {
		return depositos;
	}

	public void setCancelamentoProtesto(CancelamentoProtesto cancelamentoProtesto) {
		this.cancelamentoProtesto = cancelamentoProtesto;
	}

	public void setAutorizacaoCancelamento(AutorizacaoCancelamento autorizacaoCancelamento) {
		this.autorizacaoCancelamento = autorizacaoCancelamento;
	}

	public void setBatimento(Batimento batimento) {
		this.batimento = batimento;
	}

	public void setInstrumentoProtesto(InstrumentoProtesto instrumentoProtesto) {
		this.instrumentoProtesto = instrumentoProtesto;
	}

	public void setDepositos(List<Deposito> depositos) {
		this.depositos = depositos;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public TituloFiliado getTituloFiliado() {
		return tituloFiliado;
	}

	public void setTituloFiliado(TituloFiliado tituloFiliado) {
		this.tituloFiliado = tituloFiliado;
	}

	@Override
	public int compareTo(TituloOcorrenciaBean bean) {
		if (this.getData() != null && bean.getData() != null) {
			if (this.getData().isBefore(bean.getData())) {
				return -1;
			}
			if (this.getData().isEqual(bean.getData())) {
				if (this.getHora() != null && bean.getHora() != null) {
					if (this.getHora().isBefore(bean.getHora())) {
						return -1;
					}
				}
			}
		}
		return 1;
	}
}