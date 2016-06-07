package br.com.ieptbto.cra.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso
 *
 */
public class ArquivoOcorrenciaBean implements Serializable, Comparable<ArquivoOcorrenciaBean> {

	private static final long serialVersionUID = 1L;
	private String dataHora;
	private String nomeUsuario;
	private String mensagem;

	private Arquivo arquivo;
	private Remessa remessa;
	private String arquivoGerado;
	private DesistenciaProtesto desistenciaProtesto;
	private CancelamentoProtesto cancelamentoProtesto;
	private AutorizacaoCancelamento autorizacaoCancelamento;
	private Batimento batimento;
	private InstrumentoProtesto instrumentoProtesto;
	private List<Deposito> depositos;

	public void parseToRemessa(Remessa remessa) {
		this.arquivo = remessa.getArquivo();
		this.remessa = remessa;
		this.dataHora = DataUtil.localDateToString(remessa.getArquivo().getDataEnvio()) + " às "
				+ DataUtil.localTimeToString(remessa.getArquivo().getHoraEnvio());
		this.nomeUsuario = remessa.getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToDesistenciaProtesto(DesistenciaProtesto dp) {
		this.arquivo = dp.getRemessaDesistenciaProtesto().getArquivo();
		this.desistenciaProtesto = dp;
		this.dataHora = DataUtil.localDateToString(dp.getRemessaDesistenciaProtesto().getArquivo().getDataEnvio()) + " às "
				+ DataUtil.localTimeToString(dp.getRemessaDesistenciaProtesto().getArquivo().getHoraEnvio());
		this.nomeUsuario = dp.getRemessaDesistenciaProtesto().getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToArquivoGerado(Arquivo arquivoGerado) {
		this.arquivo = arquivoGerado;
		this.arquivoGerado = arquivoGerado.getNomeArquivo();
		this.dataHora = DataUtil.localDateToString(arquivoGerado.getDataEnvio()) + " às " + DataUtil.localTimeToString(arquivoGerado.getHoraEnvio());
		this.nomeUsuario = arquivoGerado.getUsuarioEnvio().getNome();
	}

	public void parseToCancelamentoProtesto(CancelamentoProtesto cp) {
		this.arquivo = cp.getRemessaCancelamentoProtesto().getArquivo();
		this.cancelamentoProtesto = cp;
		this.dataHora = DataUtil.localDateToString(cp.getRemessaCancelamentoProtesto().getArquivo().getDataEnvio()) + " às "
				+ DataUtil.localTimeToString(cp.getRemessaCancelamentoProtesto().getArquivo().getHoraEnvio());
		this.nomeUsuario = cp.getRemessaCancelamentoProtesto().getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToAutorizacaoCanlamento(AutorizacaoCancelamento ac) {
		this.arquivo = ac.getRemessaAutorizacaoCancelamento().getArquivo();
		this.autorizacaoCancelamento = ac;
		this.dataHora = DataUtil.localDateToString(ac.getRemessaAutorizacaoCancelamento().getArquivo().getDataEnvio()) + " às "
				+ DataUtil.localTimeToString(ac.getRemessaAutorizacaoCancelamento().getArquivo().getHoraEnvio());
		this.nomeUsuario = ac.getRemessaAutorizacaoCancelamento().getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToBatimento(Batimento batimento, List<Deposito> depositos, BigDecimal totalPagos) {
		this.batimento = batimento;
		if (totalPagos != null) {
			this.mensagem = "Total (Pagos): R$" + totalPagos.toString() + ". ";
		}
		this.mensagem = "Depósitos vínculados: \r\n";
		for (Deposito deposito : depositos) {
			this.mensagem =
					this.mensagem.concat(DataUtil.localDateToString(deposito.getData()) + " - R$" + deposito.getValorCredito().toString() + "; \r\n");
			this.nomeUsuario = deposito.getUsuario().getNome();
		}
		if (depositos.isEmpty()) {
			this.mensagem = "Liberação sem identificação de depósitos";
			this.nomeUsuario = "CRA";
		}
		this.dataHora = "";
	}

	public void parseToInstrumentoProtesto(InstrumentoProtesto instrumentoProtesto) {
		this.instrumentoProtesto = instrumentoProtesto;
		this.mensagem = "Slip gerada em ";
		this.dataHora = DataUtil.localDateToString(instrumentoProtesto.getEtiquetaSlip().getEnvelope().getDataGeracao());
		this.nomeUsuario = instrumentoProtesto.getUsuario().getNome();
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

	public String getDataHora() {
		return dataHora;
	}

	public String getArquivoGerado() {
		return arquivoGerado;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public DesistenciaProtesto getDesistenciaProtesto() {
		return desistenciaProtesto;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}

	public void setArquivoGerado(String arquivoGerado) {
		this.arquivoGerado = arquivoGerado;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
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

	@Override
	public int compareTo(ArquivoOcorrenciaBean bean) {
		if (this.getArquivo() != null && bean.getArquivo() != null) {
			if (this.getArquivo().getId() < bean.getArquivo().getId()) {
				return -1;
			}
		}
		return 1;
	}

}
