package br.com.ieptbto.cra.bean;

import java.io.Serializable;

import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class ArquivoDesistenciaCancelamentoBean implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;
	private TipoArquivoEnum tipoArquivo;
	private String nomeArquivo;
	private String dataEnvio;
	private String instituicao;
	private String codigoMunicipioDestino;
	private String envio;
	private String horaEnvio;
	private Boolean status;

	private DesistenciaProtesto desistenciaProtesto;
	private CancelamentoProtesto cancelamentoProtesto;
	private AutorizacaoCancelamento autorizacaoCancelamento;

	public void parseDesistenciaProtesto(DesistenciaProtesto desistencia) {
		this.tipoArquivo = TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO;
		this.nomeArquivo = desistencia.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo();
		this.dataEnvio = DataUtil.localDateToString(desistencia.getRemessaDesistenciaProtesto().getCabecalho().getDataMovimento());
		this.instituicao = desistencia.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia();
		this.codigoMunicipioDestino = desistencia.getCabecalhoCartorio().getCodigoMunicipio();
		this.envio = desistencia.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoRecebe().getNomeFantasia();
		this.horaEnvio = DataUtil.localTimeToString(desistencia.getRemessaDesistenciaProtesto().getArquivo().getHoraEnvio());
		this.status = desistencia.getDownload();

		this.desistenciaProtesto = desistencia;
	}

	public void parseCancelamentoProtesto(CancelamentoProtesto cancelamentoProtesto) {
		this.tipoArquivo = TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO;
		this.nomeArquivo = cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo();
		this.dataEnvio =
				DataUtil.localDateToString(cancelamentoProtesto.getRemessaCancelamentoProtesto().getCabecalho().getDataMovimento());
		this.instituicao = cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia();
		this.codigoMunicipioDestino = cancelamentoProtesto.getCabecalhoCartorio().getCodigoMunicipio();
		this.envio = cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoRecebe().getNomeFantasia();
		this.horaEnvio = DataUtil.localTimeToString(cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getHoraEnvio());
		this.status = cancelamentoProtesto.getDownload();

		this.cancelamentoProtesto = cancelamentoProtesto;
	}

	public void parseAutorizacaoCancelamento(AutorizacaoCancelamento autorizacaoCancelamento) {
		this.tipoArquivo = TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO;
		this.nomeArquivo = autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo();
		this.dataEnvio =
				DataUtil.localDateToString(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getCabecalho().getDataMovimento());
		this.instituicao = autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoEnvio().getNomeFantasia();
		this.codigoMunicipioDestino = autorizacaoCancelamento.getCabecalhoCartorio().getCodigoMunicipio();
		this.envio = autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoRecebe().getNomeFantasia();
		this.horaEnvio =
				DataUtil.localTimeToString(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getHoraEnvio());
		this.status = autorizacaoCancelamento.getDownload();

		this.autorizacaoCancelamento = autorizacaoCancelamento;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public String getDataEnvio() {
		return dataEnvio;
	}

	public String getInstituicao() {
		return instituicao;
	}

	public String getEnvio() {
		return envio;
	}

	public String getHoraEnvio() {
		return horaEnvio;
	}

	public DesistenciaProtesto getDesistenciaProtesto() {
		return desistenciaProtesto;
	}

	public CancelamentoProtesto getCancelamentoProtesto() {
		return cancelamentoProtesto;
	}

	public AutorizacaoCancelamento getAutorizacaoCancelamento() {
		return autorizacaoCancelamento;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setDataEnvio(String dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	public void setEnvio(String envio) {
		this.envio = envio;
	}

	public void setHoraEnvio(String horaEnvio) {
		this.horaEnvio = horaEnvio;
	}

	public void setDesistenciaProtesto(DesistenciaProtesto desistenciaProtesto) {
		this.desistenciaProtesto = desistenciaProtesto;
	}

	public void setCancelamentoProtesto(CancelamentoProtesto cancelamentoProtesto) {
		this.cancelamentoProtesto = cancelamentoProtesto;
	}

	public void setAutorizacaoCancelamento(AutorizacaoCancelamento autorizacaoCancelamento) {
		this.autorizacaoCancelamento = autorizacaoCancelamento;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public TipoArquivoEnum getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivoEnum tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public String getCodigoMunicipioDestino() {
		return codigoMunicipioDestino;
	}

	public void setCodigoMunicipioDestino(String codigoMunicipioDestino) {
		this.codigoMunicipioDestino = codigoMunicipioDestino;
	}
}
