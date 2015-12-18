package br.com.ieptbto.cra.bean;

import java.io.Serializable;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso
 *
 */
public class ArquivoOcorrenciaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Remessa remessa;
	private DesistenciaProtesto desistencia;
	private String dataHora;
	private String arquivoGerado;
	private String nomeUsuario;
	private DesistenciaProtesto desistenciaProtesto;

	public void parseToHistorico(Historico historico) {
		this.remessa = historico.getRemessa();
		this.dataHora = DataUtil.localDateTimeToString(historico.getDataOcorrencia());
		this.nomeUsuario = historico.getUsuarioAcao().getNome();
	}

	public void parseToDesistenciaProtesto(DesistenciaProtesto dp) {
		this.desistenciaProtesto = dp;
		this.dataHora = DataUtil.localDateToString(dp.getRemessaDesistenciaProtesto().getArquivo().getDataEnvio()) + " " +
				DataUtil.localTimeToString(dp.getRemessaDesistenciaProtesto().getArquivo().getHoraEnvio());
		this.nomeUsuario = dp.getRemessaDesistenciaProtesto().getArquivo().getUsuarioEnvio().getNome();
	}

	public void parseToArquivoGerado(Arquivo arquivoGerado) {
		this.arquivoGerado = arquivoGerado.getNomeArquivo();
		this.dataHora = DataUtil.localDateToString(arquivoGerado.getDataEnvio()) + " " +
				DataUtil.localTimeToString(arquivoGerado.getHoraEnvio());
		this.nomeUsuario = arquivoGerado.getUsuarioEnvio().getNome();
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public DesistenciaProtesto getDesistencia() {
		return desistencia;
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

	public void setDesistencia(DesistenciaProtesto desistencia) {
		this.desistencia = desistencia;
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
}
