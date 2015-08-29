package br.com.ieptbto.cra.slip.regra;

import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.BancoTipoRegraBasicaInstrumento;
import br.com.ieptbto.cra.enumeration.TipoRegraInstrumento;
import br.com.ieptbto.cra.mediator.ArquivoDeParaMediator;

/**
 * @author Thasso Araújo
 *
 */
public class RegraAgenciaDestino extends RegraInstrumentoProtesto {

	@SpringBean
	ArquivoDeParaMediator arquivoDeParaMediator;
	private String agenciaDestino;
	private String municipioDestino;
	private String ufDestino;
	private TituloRemessa titulo;
	
	public RegraAgenciaDestino regraAgenciaDestino(TituloRemessa titulo) {
		this.setTitulo(titulo);
		
		logger.info("Aplicando regra de agência destino");
		processar();
		
		return this;
	}
	
	private void processar() {
		
		BancoTipoRegraBasicaInstrumento bancoTipoRegra = BancoTipoRegraBasicaInstrumento.getBancoRegraBasicaInstrumento(getTitulo().getCodigoPortador());
		if (bancoTipoRegra != null) {
			if (bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.ITAU)) {
				new RegraItauAgencia().aplicarRegraEspecifica(getTitulo());
			} else if (bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.BRADESCO)) {
				new RegraBradescoAgencia().aplicarRegraEspecifica(getTitulo());
			} else if (bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.BANCO_CENTRAL_DO_BRASIL)) {
				new RegraBancoDoBrasilAgencia().aplicarRegraEspecifica(getTitulo());
			} else if (bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.SANTANDER) || 
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.HSBC) ||
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.MERCANTIL) ||
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.BRB) ||
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.BIC) ||
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.SAFRA)) {
				aplicarRegraBasica(bancoTipoRegra);
			}
		} else {
			setAgenciaDestino(titulo.getAgenciaCodigoCedente().substring(0,3));
		}
	}

	@Override
	protected void aplicarRegraBasica(BancoTipoRegraBasicaInstrumento bancoTipoRegra) {
		TipoRegraInstrumento tipoRegra = bancoTipoRegra.getTipoRegraBasicaInstrumento();
		setAgenciaDestino(getTitulo().getAgenciaCodigoCedente().substring(tipoRegra.getPosicaoInicialCampo(), tipoRegra.getPosicaoFinalCampo()));
		
	}

	public String getAgenciaDestino() {
		return agenciaDestino;
	}

	public String getMunicipioDestino() {
		return municipioDestino;
	}

	public String getUfDestino() {
		return ufDestino;
	}

	public void setAgenciaDestino(String agenciaDestino) {
		this.agenciaDestino = agenciaDestino;
	}

	public void setMunicipioDestino(String municipioDestino) {
		this.municipioDestino = municipioDestino;
	}

	public void setUfDestino(String ufDestino) {
		this.ufDestino = ufDestino;
	}

	public TituloRemessa getTitulo() {
		return titulo;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.titulo = titulo;
	}
}
