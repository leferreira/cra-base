package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstrumentoProtestoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.ireport.SlipEnvelopeBean;
import br.com.ieptbto.cra.ireport.SlipEtiquetaBean;
import br.com.ieptbto.cra.slip.regra.RegraAgenciaDestino;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class InstrumentoDeProtestoMediator {

	private static final Logger logger = Logger.getLogger(InstrumentoDeProtestoMediator.class);
	
	@Autowired
	TituloDAO tituloDao;
	@Autowired
	InstrumentoProtestoDAO instrumentoDao;
	private RegraAgenciaDestino regraAgenciaDestino;
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

	public void gerarEnvelopes() {
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
				mapaEnvelopes.put(etiqueta.getCodigoAgencia(), envelope);
				
				getEnvelopes().add(envelope);
			}
		}
		logger.info("Envelopes gerados.");
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
		RegraAgenciaDestino regra = regraAgenciaDestino.regraAgenciaDestino(instrumento.getTitulo());
		
		SlipEtiquetaBean novaEtiqueta = new SlipEtiquetaBean();
		novaEtiqueta.parseToTituloRemessa(instrumento.getTitulo());
		novaEtiqueta.setCodigoAgencia(regra.getAgenciaDestino());
		novaEtiqueta.setMunicipioAgencia(regra.getMunicipioDestino());
		novaEtiqueta.setUfAgencia(regra.getUfDestino());
		getEtiquetas().add(novaEtiqueta);

		logger.info("Etiqueta SLIP - Nosso Número: " +novaEtiqueta.getNossoNumero() + " gerada!");
	}
	
	private void ordenarEtiquetasInstrumentos() {
		Collections.sort(getEtiquetas());
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
