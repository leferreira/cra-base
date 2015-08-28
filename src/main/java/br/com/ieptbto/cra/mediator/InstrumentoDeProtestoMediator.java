package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.Collections;
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
	private List<Retorno> titulosProtestados;
	private List<SlipEtiquetaBean> etiquetas;
	
	public InstrumentoDeProtestoMediator processarInstrumentos(List<Retorno> listaRetorno) {
		this.titulosProtestados = listaRetorno;
		logger.info("Gerando " + listaRetorno.size() +" instrumentos de protesto.");

		gerarInstrumentos();
		ordenarEtiquetasInstrumentos();
		
		logger.info("Instrumentos de protesto processados e etiquetas geradas.");
		return this;
	}

	public List<SlipEnvelopeBean> gerarEnvelopes(List<Retorno> titulosProtestados) {
		this.titulosProtestados = titulosProtestados;
		List<SlipEnvelopeBean> envelopes = new ArrayList<SlipEnvelopeBean>();

		logger.info("Gerando envelopes.");
		
		logger.info("Finalizando geração dos envelopes.");
		return envelopes;
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
		SlipEtiquetaBean novaEtiqueta = new SlipEtiquetaBean();
		novaEtiqueta.parseToTituloRemessa(instrumento.getTitulo());
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
}
