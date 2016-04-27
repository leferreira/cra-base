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
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstrumentoProtestoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.slip.regra.RegraAgenciaDestino;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class InstrumentoProtestoMediator {

	private static final Logger logger = Logger.getLogger(InstrumentoProtestoMediator.class);

	@Autowired
	private TituloDAO tituloDao;
	@Autowired
	private InstrumentoProtestoDAO instrumentoDao;
	@Autowired
	private RegraAgenciaDestino regraAgenciaDestino;
	private List<Retorno> titulosProtestados;
	private List<EtiquetaSLIP> etiquetas;
	private List<EnvelopeSLIP> envelopes;
	private List<InstrumentoProtesto> instrumentosProtesto;

	/**
	 * Salvar entrada de instrumento de protesto na CRA
	 * 
	 * @param titulosProtestados
	 * @param usuario
	 */
	public void salvarInstrumentoProtesto(List<Retorno> titulosProtestados, Usuario usuario) {

		for (Retorno retorno : titulosProtestados) {
			Retorno tituloRetorno = instrumentoDao.carregarRetorno(retorno);
			InstrumentoProtesto instrumentoBuscado = instrumentoDao.isTituloJaFoiGeradoInstrumento(tituloRetorno);
			if (instrumentoBuscado == null) {
				InstrumentoProtesto instrumento = new InstrumentoProtesto();
				instrumento.setDataDeEntrada(new LocalDate());
				instrumento.setHoraEntrada(new LocalTime());
				instrumento.setTituloRetorno(tituloRetorno);
				instrumento.setGerado(false);
				instrumento.setUsuario(usuario);

				instrumentoDao.salvarInstrumentoProtesto(instrumento);
			}
		}
	}

	/**
	 * Processar instrumentos recebidos e gerar slip, etiquetas e envelopes
	 * 
	 * @param instrumentos
	 * @param listaRetorno
	 * @return
	 */
	public InstrumentoProtestoMediator processarInstrumentos(List<InstrumentoProtesto> instrumentos, List<Retorno> listaRetorno) {
		this.setInstrumentosProtesto(instrumentos);
		this.titulosProtestados = listaRetorno;
		this.etiquetas = null;
		this.envelopes = null;
		logger.info("Gerando " + listaRetorno.size() + " instrumentos de protesto.");

		gerarSLIP(instrumentos);
		ordenarEtiquetasInstrumentos();
		gerarEnvelopes();
		salvarEnvelopesEtiquetas();

		logger.info("Instrumentos de protesto processados e etiquetas geradas.");
		return this;
	}

	private void gerarSLIP(List<InstrumentoProtesto> instrumentos) {

		for (InstrumentoProtesto instrumento : instrumentos) {
			gerarEtiqueta(instrumento);
		}
	}

	private void gerarEtiqueta(InstrumentoProtesto instrumento) {
		RegraAgenciaDestino regraAgencia = regraAgenciaDestino.regraAgenciaDestino(instrumento.getTituloRetorno().getTitulo());
		instrumento.getTituloRetorno().setRemessa(instrumentoDao.buscarPorPK(instrumento.getTituloRetorno().getRemessa(), Remessa.class));

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
		HashMap<Integer, EnvelopeSLIP> mapaEnvelopes = new HashMap<Integer, EnvelopeSLIP>();

		logger.info("Gerando envelopes.");
		for (EtiquetaSLIP etiqueta : getEtiquetas()) {
			if (mapaEnvelopes.containsKey(Integer.parseInt(
					new chaveEnvelope(etiqueta.getInstrumentoProtesto().getTituloRetorno().getCodigoPortador(), etiqueta.getAgenciaDestino()).toString()))) {
				EnvelopeSLIP envelope = mapaEnvelopes.get(Integer.parseInt(
						new chaveEnvelope(etiqueta.getInstrumentoProtesto().getTituloRetorno().getCodigoPortador(), etiqueta.getAgenciaDestino()).toString()));
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
				String codeBar = envelope.getAgenciaDestino() + envelope.getUfAgenciaDestino() + dataPadraEnvelope.format(new Date()).toString();
				String codigoCRA = StringUtils.leftPad(instrumentoDao.quantidadeEnvelopes(), 6, "0") + codeBar;

				envelope.setCodeBar(codeBar);
				envelope.setCodigoCRA(codigoCRA);
				envelope.setEtiquetas(new ArrayList<EtiquetaSLIP>());
				envelope.getEtiquetas().add(etiqueta);

				mapaEnvelopes.put(Integer.parseInt(
						new chaveEnvelope(etiqueta.getInstrumentoProtesto().getTituloRetorno().getCodigoPortador(), etiqueta.getAgenciaDestino()).toString()),
						envelope);
				getEnvelopes().add(envelope);
			}
		}
		logger.info("Envelopes gerados.");
	}

	private void ordenarEtiquetasInstrumentos() {
		Collections.sort(getEtiquetas());
	}

	private void salvarEnvelopesEtiquetas() {
		instrumentoDao.salvarEnvelopesEtiquetas(getEnvelopes());
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

	public InstrumentoProtesto isTituloJaFoiGeradoInstrumento(Retorno tituloProtestado) {
		return instrumentoDao.isTituloJaFoiGeradoInstrumento(tituloProtestado);
	}

	public List<InstrumentoProtesto> buscarInstrumentosParaSlip() {
		return instrumentoDao.buscarInstrumentosParaSlip();
	}

	public List<InstrumentoProtesto> getInstrumentosProtesto() {
		return instrumentosProtesto;
	}

	public void setInstrumentosProtesto(List<InstrumentoProtesto> instrumentosProtesto) {
		this.instrumentosProtesto = instrumentosProtesto;
	}

	public void alterarInstrumentosParaGerado(List<InstrumentoProtesto> instrumentosProtesto) {
		instrumentoDao.alterarParaInstrumentosGerados(instrumentosProtesto);
	}
}

class chaveEnvelope {

	private String codigoPortador;
	private String codigoAgencia;

	public chaveEnvelope(String codigoPortador, String codigoAgencia) {
		this.codigoPortador = codigoPortador;
		this.codigoAgencia = codigoAgencia;
	}

	@Override
	public String toString() {
		return codigoPortador.toString() + codigoAgencia.toString();
	}
}
