package br.com.ieptbto.cra.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
public class ArquivoOcorrenciaBean implements Serializable, Comparable<ArquivoOcorrenciaBean> {

	private static final long serialVersionUID = 1L;
	private LocalDate data;
	private LocalTime hora;
	private String nomeUsuario;
	private String mensagem;

	private Arquivo arquivo;
	private Arquivo arquivoLiberado;
	private Remessa remessa;
	private DesistenciaProtesto desistenciaProtesto;
	private CancelamentoProtesto cancelamentoProtesto;
	private AutorizacaoCancelamento autorizacaoCancelamento;
	private SolicitacaoDesistenciaCancelamento solicitacaoCancelamento;
	private Batimento batimento;
	private InstrumentoProtesto instrumentoProtesto;
	private List<Deposito> depositos;
	private TituloFiliado tituloFiliado;

	public void parseToRemessa(Remessa remessa) {
		this.arquivo = remessa.getArquivo();
		this.remessa = remessa;
		this.data = arquivo.getDataEnvio();
		this.hora = arquivo.getHoraEnvio();
		this.nomeUsuario = remessa.getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToDesistenciaProtesto(DesistenciaProtesto dp) {
		this.arquivo = dp.getRemessaDesistenciaProtesto().getArquivo();
		this.desistenciaProtesto = dp;
		this.data = arquivo.getDataEnvio();
		this.hora = arquivo.getHoraEnvio();
		this.nomeUsuario = dp.getRemessaDesistenciaProtesto().getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToArquivoGerado(Arquivo arquivo) {
		this.arquivo = arquivo;
		this.arquivoLiberado = arquivo;
		this.data = arquivo.getDataEnvio();
		this.hora = arquivo.getHoraEnvio();
		this.nomeUsuario = arquivo.getUsuarioEnvio().getNome();
	}

	public void parseToCancelamentoProtesto(CancelamentoProtesto cp) {
		this.arquivo = cp.getRemessaCancelamentoProtesto().getArquivo();
		this.cancelamentoProtesto = cp;
		this.data = arquivo.getDataEnvio();
		this.hora = arquivo.getHoraEnvio();
		this.nomeUsuario = cp.getRemessaCancelamentoProtesto().getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToAutorizacaoCanlamento(AutorizacaoCancelamento ac) {
		this.arquivo = ac.getRemessaAutorizacaoCancelamento().getArquivo();
		this.autorizacaoCancelamento = ac;
		this.data = arquivo.getDataEnvio();
		this.hora = arquivo.getHoraEnvio();
		this.nomeUsuario = ac.getRemessaAutorizacaoCancelamento().getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToBatimento(Batimento batimento, List<Deposito> depositos, BigDecimal totalPagos) {
		DecimalFormat decimalFormat = new DecimalFormat("R$ #,##0.00");

		this.batimento = batimento;
		this.mensagem = "<b>Depósitos vínculados:</b><br/>";
		for (Deposito deposito : depositos) {
			this.mensagem = this.mensagem.concat(
					DataUtil.localDateToString(deposito.getData()) + " - " + decimalFormat.format(deposito.getValorCredito()) + ";<br>");
			this.nomeUsuario = deposito.getUsuario().getNome();
		}
		if (totalPagos != null) {
			this.mensagem = this.mensagem
					.concat("<b>Total Títulos (Pagos): <span style=\"color: red;\">R$ " + decimalFormat.format(totalPagos) + "</span></b>");
		}
		if (depositos.isEmpty()) {
			this.mensagem = "Liberação sem identificação de depósitos.";
			this.nomeUsuario = "CRA";
		}
		this.data = batimento.getDataBatimento().toLocalDate();
		this.hora = batimento.getDataBatimento().toLocalTime();
	}

	public void parseToInstrumentoProtesto(InstrumentoProtesto instrumentoProtesto) {
		this.instrumentoProtesto = instrumentoProtesto;
		if (instrumentoProtesto.getEtiquetaSlip() != null) {
			this.mensagem = "Slip gerada com sucesso.";
			this.data = instrumentoProtesto.getEtiquetaSlip().getEnvelope().getDataGeracao();
			this.hora = instrumentoProtesto.getEtiquetaSlip().getEnvelope().getHoraGeracao();
			this.nomeUsuario = instrumentoProtesto.getUsuario().getNome();
		} else {
			this.mensagem = "Slip aguardando geração.";
			this.data = instrumentoProtesto.getDataDeEntrada();
			this.hora = new LocalTime();
			this.nomeUsuario = StringUtils.EMPTY;
		}
	}

	public void parseToSolicitacaoCancelamento(SolicitacaoDesistenciaCancelamento solicitacao) {
		this.solicitacaoCancelamento = solicitacao;
		this.data = new LocalDate(solicitacao.getDataSolicitacao());
		this.hora = solicitacao.getHoraSolicitacao();
		this.nomeUsuario = solicitacao.getUsuario().getNome();
		this.mensagem = "Solicitação de " + solicitacao.getTipoSolicitacao().getDescricao();
		if (solicitacao.getCodigoIrregularidade() != null) {
			this.mensagem = mensagem + ". <br><b>Motivo:</b> " + solicitacao.getCodigoIrregularidade().getMotivo() + ".";
		}
	}

	public void parseToTituloFiliado(TituloFiliado tituloFiliado) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
		this.tituloFiliado = tituloFiliado;
		this.data = new LocalDate(tituloFiliado.getDataEntrada());
		this.hora = new LocalTime(formatter.parseDateTime("01/01/2000 00:00:00"));
		this.nomeUsuario = tituloFiliado.getUsuarioEntradaManual().getNome();
		this.mensagem = "Título cadastrado no IEPTB-Convênios.";
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Arquivo getArquivoLiberado() {
		return arquivoLiberado;
	}

	public void setArquivoLiberado(Arquivo arquivoLiberado) {
		this.arquivoLiberado = arquivoLiberado;
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
	public int compareTo(ArquivoOcorrenciaBean bean) {
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