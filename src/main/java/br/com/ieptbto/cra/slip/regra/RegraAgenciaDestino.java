package br.com.ieptbto.cra.slip.regra;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.BancoTipoRegraBasicaInstrumento;
import br.com.ieptbto.cra.enumeration.TipoRegraInstrumento;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
public class RegraAgenciaDestino extends RegraInstrumentoProtesto {

	@Autowired
	ArquivoDeParaDAO arquivoDeParaDAO;
	
	private TituloRemessa titulo;
	private String agenciaDestino;
	private String municipioDestino;
	private String ufDestino;
	
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
				aplicarRegraItau();
			} else if (bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.BRADESCO)) {
				aplicarRegraBradesco();
			} else if (bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.BANCO_CENTRAL_DO_BRASIL)) {
				aplicarRegraBB();
			} else if (bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.SANTANDER) || 
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.HSBC) ||
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.MERCANTIL) ||
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.BRB) ||
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.BIC) ||
					bancoTipoRegra.equals(BancoTipoRegraBasicaInstrumento.SAFRA)) {
				aplicarRegraOutros(bancoTipoRegra);
			}
		} else {
			setAgenciaDestino(getTitulo().getAgenciaCodigoCedente().substring(0,3));
		}
	}

	private void aplicarRegraItau() {
		String agenciaItau = new RegraItauAgencia().aplicarRegraEspecifica(getTitulo());
		if (agenciaItau == null) {
			agenciaItau = aplicarRegraBasica(BancoTipoRegraBasicaInstrumento.ITAU);
		}
		
		AgenciaCAF agenciaCAF = arquivoDeParaDAO.buscarAgenciaArquivoCAF(agenciaItau);
		if (agenciaCAF != null) {
			setAgenciaDestino(agenciaCAF.getCodigoAgencia());
			setMunicipioDestino(agenciaCAF.getCidade());
			setUfDestino(agenciaCAF.getUf());
		} else {
			throw new InfraException("Não foi possível identificar a agência de destino do título [Nosso Nº: " +getTitulo().getNossoNumero()+ "] .");
		}
	}
	
	private void aplicarRegraBradesco() {
		String agenciaBradesco = new RegraBradescoAgencia().aplicarRegraEspecifica(getTitulo());
		AgenciaBradesco agenciaDePara = arquivoDeParaDAO.buscarAgenciaArquivoDeParaBradesco(getTitulo());
		
		if (agenciaDePara != null) {
			setAgenciaDestino(agenciaDePara.getAgenciaDestino());
			
		} else {
			agenciaBradesco = aplicarRegraBasica(BancoTipoRegraBasicaInstrumento.BRADESCO);
			AgenciaCAF agenciaCAF = arquivoDeParaDAO.buscarAgenciaArquivoCAF(agenciaBradesco);
			
			if (agenciaCAF == null) {
				throw new InfraException("Não foi possível identificar a agência de destino do título [Nosso Nº: " +getTitulo().getNossoNumero()+ "] .");
			}
			setAgenciaDestino(agenciaCAF.getCodigoAgencia());
			setMunicipioDestino(agenciaCAF.getCidade());
			setUfDestino(agenciaCAF.getUf());
		}
	}
	
	private void aplicarRegraBB () {
		this.agenciaDestino = new RegraBancoDoBrasilAgencia().aplicarRegraEspecifica(getTitulo());
		aplicarRegraBasica(BancoTipoRegraBasicaInstrumento.BANCO_CENTRAL_DO_BRASIL);
	}
	
	private void aplicarRegraOutros(BancoTipoRegraBasicaInstrumento bancoTipoRegra) {
		String agencia = aplicarRegraBasica(BancoTipoRegraBasicaInstrumento.ITAU);
		AgenciaCAF agenciaCAF = arquivoDeParaDAO.buscarAgenciaArquivoCAF(agencia);
		
		if (agenciaCAF == null) {
			throw new InfraException("Não foi possível identificar a agência de destino do título [Nosso Nº: " +getTitulo().getNossoNumero()+ "] .");
		}
		setAgenciaDestino(agenciaCAF.getCodigoAgencia());
		setMunicipioDestino(agenciaCAF.getCidade());
		setUfDestino(agenciaCAF.getUf());
	}

	@Override
	protected String aplicarRegraBasica(BancoTipoRegraBasicaInstrumento bancoTipoRegra) {
		TipoRegraInstrumento tipoRegra = bancoTipoRegra.getTipoRegraBasicaInstrumento();
		return getTitulo().getAgenciaCodigoCedente().substring(tipoRegra.getPosicaoInicialCampo(), tipoRegra.getPosicaoFinalCampo());
	}

	public String getAgenciaDestino() {
		if (agenciaDestino == null) {
			agenciaDestino = StringUtils.EMPTY;
		}
		return agenciaDestino;
	}

	public String getMunicipioDestino() {
		if (municipioDestino == null) {
			municipioDestino = StringUtils.EMPTY;
		}
		return municipioDestino;
	}

	public String getUfDestino() {
		if (ufDestino == null) {
			ufDestino = StringUtils.EMPTY;
		}
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
