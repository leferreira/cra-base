package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.InstrumentoProtestoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.slip.regra.RegraAgenciaDestino;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class InstrumentoProtestoMediator extends BaseMediator {

	private static final String SIGLA_ENVELOPE_PT = "PT";
	private static final String UF_CRA = "TO";

	@Autowired
	TituloDAO tituloDao;
	@Autowired
	InstrumentoProtestoDAO instrumentoDao;
	@Autowired
	RegraAgenciaDestino regraAgenciaDestino;
	private List<Retorno> titulosProtestados;
	private List<EtiquetaSLIP> etiquetas;
	private List<EnvelopeSLIP> envelopes;
	private Long sequencialDiarioEnvelopes;
	private List<InstrumentoProtesto> instrumentosProtesto;

	/**
	 * Salvar entrada de instrumento de protesto na CRA
	 * 
	 * @param titulosProtestados
	 * @param usuario
	 */
	public void salvarInstrumentoProtesto(List<Retorno> titulosProtestados, Usuario usuario) {

		for (Retorno retorno : titulosProtestados) {
			InstrumentoProtesto instrumentoBuscado = instrumentoDao.isTituloJaFoiGeradoInstrumento(retorno);

			if (instrumentoBuscado == null) {
				InstrumentoProtesto instrumento = new InstrumentoProtesto();
				instrumento.setDataDeEntrada(new LocalDate());
				instrumento.setHoraEntrada(new LocalTime());
				instrumento.setTituloRetorno(retorno);
				instrumento.setGerado(false);
				instrumento.setUsuario(usuario);

				instrumentoDao.salvarInstrumentoProtesto(instrumento);
			}
		}
	}

	public InstrumentoProtestoMediator processarInstrumentos(List<InstrumentoProtesto> instrumentos, List<Retorno> listaRetorno) {
		this.instrumentosProtesto = instrumentos;
		this.titulosProtestados = listaRetorno;
		this.etiquetas = null;
		this.envelopes = null;
		this.sequencialDiarioEnvelopes = null;

		gerarSLIP(instrumentos);
		ordenarEtiquetasInstrumentos();
		gerarEnvelopes();
		salvarEnvelopesEtiquetas();
		return this;
	}

	private void gerarSLIP(List<InstrumentoProtesto> instrumentos) {

		for (InstrumentoProtesto instrumento : instrumentos) {
			if (instrumento.getEtiquetaSlip() != null) {
				throw new InfraException(
						"Os instrumentos de protestos já tiveram as Slips geradas. Por favor clique em confirmar para marca-los como gerados!");
			}
			RegraAgenciaDestino regraAgencia = regraAgenciaDestino.regraAgenciaDestino(instrumento.getTituloRetorno().getTitulo());
			instrumento.getTituloRetorno()
					.setRemessa(instrumentoDao.buscarPorPK(instrumento.getTituloRetorno().getRemessa(), Remessa.class));

			if (StringUtils.isNotEmpty(regraAgencia.getAgenciaDestino().trim())) {
				EtiquetaSLIP novaEtiqueta = new EtiquetaSLIP();
				novaEtiqueta.parseToTitulo(instrumento.getTituloRetorno());
				novaEtiqueta.setAgenciaDestino(regraAgencia.getAgenciaDestino());
				novaEtiqueta.setMunicipioAgenciaDestino(regraAgencia.getMunicipioDestino());
				novaEtiqueta.setUfAgenciaDestino(regraAgencia.getUfDestino());
				novaEtiqueta.setInstrumentoProtesto(instrumento);
				getEtiquetas().add(novaEtiqueta);
			}
		}
	}

	private void gerarEnvelopes() {
		HashMap<Integer, EnvelopeSLIP> mapaEnvelopes = new HashMap<Integer, EnvelopeSLIP>();

		for (EtiquetaSLIP etiqueta : getEtiquetas()) {
			if (mapaEnvelopes.containsKey(
					Integer.parseInt(new chaveEnvelope(etiqueta.getInstrumentoProtesto().getTituloRetorno().getCodigoPortador(),
							etiqueta.getAgenciaDestino()).toString()))) {
				EnvelopeSLIP envelope = mapaEnvelopes
						.get(Integer.parseInt(new chaveEnvelope(etiqueta.getInstrumentoProtesto().getTituloRetorno().getCodigoPortador(),
								etiqueta.getAgenciaDestino()).toString()));
				envelope.setQuantidadeInstrumentos(envelope.getQuantidadeInstrumentos() + 1);
				envelope.getEtiquetas().add(etiqueta);
			} else {
				EnvelopeSLIP envelope = new EnvelopeSLIP();
				envelope.setBanco(etiqueta.getBanco());
				envelope.setAgenciaDestino(etiqueta.getAgenciaDestino());
				envelope.setMunicipioAgenciaDestino(etiqueta.getMunicipioAgenciaDestino());
				envelope.setUfAgenciaDestino(etiqueta.getUfAgenciaDestino());
				envelope.setQuantidadeInstrumentos(1);

				String codeBar = gerarCodigoBarraEnvelope(envelope);
				envelope.setCodeBar(codeBar);
				envelope.setEtiquetas(new ArrayList<EtiquetaSLIP>());
				envelope.getEtiquetas().add(etiqueta);

				mapaEnvelopes
						.put(Integer.parseInt(new chaveEnvelope(etiqueta.getInstrumentoProtesto().getTituloRetorno().getCodigoPortador(),
								etiqueta.getAgenciaDestino()).toString()), envelope);
				getEnvelopes().add(envelope);
			}
		}
	}

	private String gerarCodigoBarraEnvelope(EnvelopeSLIP envelope) {
		SimpleDateFormat dataPadraEnvelope = new SimpleDateFormat("ddMMyy");

		String codeBar = SIGLA_ENVELOPE_PT;
		codeBar = codeBar.concat(envelope.getAgenciaDestino());
		codeBar = codeBar.concat(incrementarSequencialEnvelope());
		codeBar = codeBar.concat(UF_CRA);
		codeBar = codeBar.concat(dataPadraEnvelope.format(new Date()).toString());
		return codeBar;
	}

	private String incrementarSequencialEnvelope() {
		if (sequencialDiarioEnvelopes == null) {
			this.sequencialDiarioEnvelopes = instrumentoDao.buscarSequencialDiarioEnvelopes();
		}

		this.sequencialDiarioEnvelopes = sequencialDiarioEnvelopes + 1;
		return StringUtils.leftPad(Long.toString(sequencialDiarioEnvelopes), 4, "0");
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

    @Transactional
	public Retorno buscarTituloProtestado(String numeroProtocolo, String codigoIBGE) {
		return tituloDao.buscarTituloProtestado(numeroProtocolo, codigoIBGE);
	}

    @Transactional
	public InstrumentoProtesto isTituloJaFoiGeradoInstrumento(Retorno tituloProtestado) {
		return instrumentoDao.isTituloJaFoiGeradoInstrumento(tituloProtestado);
	}

	public List<InstrumentoProtesto> buscarInstrumentosParaSlip() {
		return instrumentoDao.buscarInstrumentosParaSlip();
	}

	public List<InstrumentoProtesto> getInstrumentosProtesto() {
		return instrumentosProtesto;
	}

	public void alterarInstrumentosParaGerado(List<InstrumentoProtesto> instrumentosProtesto) {
		instrumentoDao.alterarParaInstrumentosGerados(instrumentosProtesto);
	}

	public void removerInstrumento(InstrumentoProtesto instrumentoProtesto) {
		instrumentoDao.removerInstrumento(instrumentoProtesto);
	}

	public boolean verificarEtiquetasGeradasNaoConfimadas() {
		return instrumentoDao.verificarEtiquetasGeradasNaoConfimadas();
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