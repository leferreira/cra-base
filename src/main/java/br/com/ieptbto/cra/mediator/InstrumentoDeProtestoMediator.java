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

import br.com.ieptbto.cra.dao.InstrumentoProtestoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.slip.regra.RegraAgenciaDestino;

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
	private InstrumentoProtestoDAO instrumentoDao;
	@Autowired
	private RegraAgenciaDestino regraAgenciaDestino;

	private List<Retorno> titulosProtestados;
	private List<EtiquetaSLIP> etiquetas;
	private List<EnvelopeSLIP> envelopes;

	public InstrumentoDeProtestoMediator processarInstrumentos(List<Retorno> listaRetorno) {
		this.titulosProtestados = listaRetorno;
		logger.info("Gerando " + listaRetorno.size() + " instrumentos de protesto.");

		gerarInstrumentos();
		ordenarEtiquetasInstrumentos();
		gerarEnvelopes();
		salvarSLIP();

		logger.info("Instrumentos de protesto processados e etiquetas geradas.");
		return this;
	}

	private void gerarInstrumentos() {

		for (Retorno retorno : getTitulosProtestados()) {
			Retorno tituloRetorno = instrumentoDao.carregarRetorno(retorno);
			
			InstrumentoProtesto instrumento = new InstrumentoProtesto();
			instrumento.setDataDeEntrada(new LocalDate());
			instrumento.setTituloRetorno(tituloRetorno);

			gerarEtiqueta(instrumento);
		}
	}

	private void gerarEtiqueta(InstrumentoProtesto instrumento) {
		RegraAgenciaDestino regraAgencia = regraAgenciaDestino.regraAgenciaDestino(instrumento.getTituloRetorno().getTitulo());

		EtiquetaSLIP novaEtiqueta = new EtiquetaSLIP();
		novaEtiqueta.parseToTitulo(instrumento.getTituloRetorno());
		novaEtiqueta.setAgenciaDestino(regraAgencia.getAgenciaDestino());
		novaEtiqueta.setMunicipioAgenciaDestino(regraAgencia.getMunicipioDestino());
		novaEtiqueta.setUfAgenciaDestino(regraAgencia.getUfDestino());
		novaEtiqueta.setInstrumentoProtesto(instrumento);
		getEtiquetas().add(novaEtiqueta);

		logger.info("Etiqueta SLIP - Nosso Número: " + novaEtiqueta.getNossoNumero() + " gerada!");
	}

	private void gerarEnvelopes() {
		HashMap<String, EnvelopeSLIP> mapaEnvelopes = new HashMap<String, EnvelopeSLIP>();

		logger.info("Gerando envelopes.");
		for (EtiquetaSLIP etiqueta : getEtiquetas()) {
			if (mapaEnvelopes.containsKey(etiqueta.getAgenciaDestino())) {
				EnvelopeSLIP envelope = mapaEnvelopes.get(etiqueta.getAgenciaDestino());
				envelope.setQuantidadeInstrumentos(envelope.getQuantidadeInstrumentos() + 1);
				envelope.getEtiquetas().add(etiqueta);
			} else {
				EnvelopeSLIP envelope = new EnvelopeSLIP();
				envelope.setBanco(etiqueta.getBanco());
				envelope.setAgenciaDestino(etiqueta.getAgenciaDestino());
				envelope.setMunicipioAgenciaDestino(etiqueta.getMunicipioAgenciaDestino());
				envelope.setUfAgenciaDestino(etiqueta.getUfAgenciaDestino());
				envelope.setQuantidadeInstrumentos(1);

				SimpleDateFormat dataPadraEnvelope = new SimpleDateFormat("ddMMyy");
				String codeBar = envelope.getAgenciaDestino() + envelope.getUfAgenciaDestino()
						+ dataPadraEnvelope.format(new Date()).toString();
				String codigoCRA = StringUtils.leftPad(instrumentoDao.quantidadeEnvelopes(), 6, "0") + codeBar;

				envelope.setCodeBar(codeBar);
				envelope.setCodigoCRA(codigoCRA);
				envelope.setEtiquetas(new ArrayList<EtiquetaSLIP>());
				envelope.getEtiquetas().add(etiqueta);

				mapaEnvelopes.put(etiqueta.getAgenciaDestino(), envelope);
				getEnvelopes().add(envelope);
			}
		}
		logger.info("Envelopes gerados.");
	}

	private void ordenarEtiquetasInstrumentos() {
		Collections.sort(getEtiquetas());
	}

	private void salvarSLIP() {
		instrumentoDao.salvarSLIP(getEnvelopes());
	}

	public List<EtiquetaSLIP> getEtiquetas() {
		if (etiquetas == null) {
			etiquetas = new ArrayList<EtiquetaSLIP>();
		}
		return etiquetas;
	}

	public List<EnvelopeSLIP> getEnvelopes() {
		if (envelopes == null) {
			envelopes = new ArrayList<EnvelopeSLIP>();
		}
		return envelopes;
	}

	public List<Retorno> getTitulosProtestados() {
		if (titulosProtestados == null) {
			titulosProtestados = new ArrayList<Retorno>();
		}
		return titulosProtestados;
	}

	public Retorno buscarTituloProtestado(String numeroProtocolo, String codigoIBGE) {
		return tituloDao.buscarTituloProtestado(numeroProtocolo, codigoIBGE);
	}

	public List<EnvelopeSLIP> buscarEnvelopesPendetesLiberacao() {
		return instrumentoDao.buscarEnvelopesPendetesLiberacao();
	}

	public void alterarEnvelopesParaEnviado(List<EnvelopeSLIP> envelopesLiberados) {
		instrumentoDao.alterarEnvelopesParaEnviado(envelopesLiberados);
	}
}
