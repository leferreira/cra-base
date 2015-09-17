package br.com.ieptbto.cra.mediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.dao.InstrumentoProtestoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.BancoTipoRegraBasicaInstrumento;
import br.com.ieptbto.cra.enumeration.TipoRegraInstrumento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.ireport.SlipEnvelopeBean;
import br.com.ieptbto.cra.ireport.SlipEtiquetaBean;
import br.com.ieptbto.cra.slip.regra.RegraBancoDoBrasilAgencia;
import br.com.ieptbto.cra.slip.regra.RegraBradescoAgencia;
import br.com.ieptbto.cra.slip.regra.RegraItauAgencia;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class InstrumentoDeProtestoMediator {

	private static final Logger logger = Logger.getLogger(InstrumentoDeProtestoMediator.class);
	
	@Autowired
	private TituloDAO tituloDao;
	@Autowired
	private ArquivoDeParaDAO arquivoDeParaDAO;
	@Autowired
	private InstrumentoProtestoDAO instrumentoDao;
	private TituloRemessa titulo;
	private String agenciaDestino;
	private String municipioDestino;
	private String ufDestino;
	private List<Retorno> titulosProtestados;
	private List<SlipEtiquetaBean> etiquetas;
	private List<SlipEnvelopeBean> envelopes;

	public InstrumentoDeProtestoMediator processarInstrumentos(List<Retorno> listaRetorno) {
		this.titulosProtestados = listaRetorno;
		logger.info("Gerando " + listaRetorno.size() +" instrumentos de protesto.");

		gerarInstrumentos();
		ordenarEtiquetasInstrumentos();
		gerarEnvelopes();
		
		logger.info("Instrumentos de protesto processados e etiquetas geradas.");
		return this;
	}

	private void gerarInstrumentos() {
		
		for (Retorno retorno : getTitulosProtestados()) {
			InstrumentoProtesto instrumento = new InstrumentoProtesto();
			instrumento.setDataDeEntrada(new LocalDate());
			instrumento.setDataSlip(new LocalDate());
			instrumento.setSituacao(false);
			instrumento.setTitulo(tituloDao.buscarTituloPorChave(retorno.getTitulo()));
			
			gerarEtiqueta(instrumentoDao.salvarInstrumento(instrumento));
		}
	}

	private void gerarEtiqueta(InstrumentoProtesto instrumento) {
		regraAgenciaDestino(instrumento.getTitulo());
		
		SlipEtiquetaBean novaEtiqueta = new SlipEtiquetaBean();
		novaEtiqueta.parseToTituloRemessa(instrumento.getTitulo());
		novaEtiqueta.setCodigoAgencia(getAgenciaDestino());
		novaEtiqueta.setMunicipioAgencia(getMunicipioDestino());
		novaEtiqueta.setUfAgencia(getUfDestino());
		getEtiquetas().add(novaEtiqueta);

		logger.info("Etiqueta SLIP - Nosso Número: " +novaEtiqueta.getNossoNumero() + " gerada!");
	}
	
	private void gerarEnvelopes() {
		HashMap<String, SlipEnvelopeBean> mapaEnvelopes = new HashMap<String, SlipEnvelopeBean>();

		logger.info("Gerando envelopes.");
		for (SlipEtiquetaBean etiqueta : getEtiquetas()) {
			if (mapaEnvelopes.containsKey(etiqueta.getCodigoAgencia())) {
				SlipEnvelopeBean envelope = mapaEnvelopes.get(etiqueta.getCodigoAgencia());
				envelope.setQuantidadeInstrumentos(envelope.getQuantidadeInstrumentos()+1);
			} else {
				SlipEnvelopeBean envelope = new SlipEnvelopeBean();
				envelope.setNomeApresentante(etiqueta.getRazaoSocialPortador());
				envelope.setNumeroAgencia(etiqueta.getCodigoAgencia());
				envelope.setMunicipio(etiqueta.getMunicipioAgencia());
				envelope.setUf(etiqueta.getUfAgencia());
				envelope.setQuantidadeInstrumentos(1);
				
				SimpleDateFormat dataPadraEnvelope = new SimpleDateFormat("ddMMyy");
				String codeBar = envelope.getNumeroAgencia() + envelope.getUf() + dataPadraEnvelope.format(new Date()).toString();
				envelope.setCodeBar(codeBar);
				mapaEnvelopes.put(etiqueta.getCodigoAgencia(), envelope);
				
				getEnvelopes().add(envelope);
			}
		}
		logger.info("Envelopes gerados.");
	}
	
	private void regraAgenciaDestino(TituloRemessa titulo) {
		this.setTitulo(titulo);
		
		logger.info("Aplicando regra de agência destino");
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

	private String aplicarRegraBasica(BancoTipoRegraBasicaInstrumento bancoTipoRegra) {
		TipoRegraInstrumento tipoRegra = bancoTipoRegra.getTipoRegraBasicaInstrumento();
		return getTitulo().getAgenciaCodigoCedente().substring(tipoRegra.getPosicaoInicialCampo() -1 , tipoRegra.getPosicaoFinalCampo());
	}
	
	private void ordenarEtiquetasInstrumentos() {
		Collections.sort(getEtiquetas());
	}
	
	public TituloRemessa getTitulo() {
		return titulo;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.titulo = titulo;
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

	public List<SlipEtiquetaBean> getEtiquetas() {
		if (etiquetas == null) {
			etiquetas = new ArrayList<SlipEtiquetaBean>();
		}
		return etiquetas;
	}

	public List<Retorno> getTitulosProtestados() {
		return titulosProtestados;
	}

	public Retorno buscarTituloProtestado(String numeroProtocolo, String codigoIBGE) {
		return tituloDao.buscarTituloProtestado(numeroProtocolo, codigoIBGE);
	}

	public List<SlipEnvelopeBean> getEnvelopes() {
		if (envelopes == null) {
			envelopes = new ArrayList<SlipEnvelopeBean>();
		}
		return envelopes;
	}

	public void setEnvelopes(List<SlipEnvelopeBean> envelopes) {
		this.envelopes = envelopes;
	}
}
