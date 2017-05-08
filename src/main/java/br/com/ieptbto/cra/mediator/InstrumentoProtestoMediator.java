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
	private TituloDAO tituloDao;
	@Autowired
    private InstrumentoProtestoDAO instrumentoDao;
	@Autowired
    private RegraAgenciaDestino regraAgenciaDestino;

	private List<Retorno> titulosProtestados;
	private List<EtiquetaSLIP> etiquetas;
	private List<EnvelopeSLIP> envelopes;
	private Long sequencialDiarioEnvelopes;
	private List<InstrumentoProtesto> instrumentosProtesto;

	/**
	 * Salvar entrada de instrumento de protesto na CRA
	 * 
	 * @param usuario
     * @param numeroProtocolo
     * @param codigoIbge
	 */
	public InstrumentoProtesto salvarInstrumentoProtesto(Usuario usuario, String numeroProtocolo, String codigoIbge) {
        Retorno retorno = tituloDao.buscarTituloProtestado(numeroProtocolo, codigoIbge);
        if (retorno == null) {
            throw new InfraException("O título não foi encontrado ou não foi protestado pelo cartório!");
        }

        InstrumentoProtesto instrumento = instrumentoDao.buscarInstrumentoProtesto(retorno);
        if (instrumento == null) {
            instrumento = new InstrumentoProtesto();
            instrumento.setDataDeEntrada(new LocalDate());
            instrumento.setHoraEntrada(new LocalTime());
            instrumento.setTituloRetorno(retorno);
            instrumento.setGerado(false);
            instrumento.setUsuario(usuario);
            return instrumentoDao.salvarInstrumentoProtesto(instrumento);
        }
        throw new InfraException("Este instrumento já foi processado anteriormente!");
	}

    /**
     * Marcar instrumentos como etiquetas Slips, Envelopes e Listagem geradas
     *
     * @param instrumentosProtesto
     */
    public void alterarInstrumentosParaGerado(List<InstrumentoProtesto> instrumentosProtesto) {

        for (InstrumentoProtesto instrumento : instrumentosProtesto) {
            instrumento.setGerado(true);
            instrumentoDao.atualizarInstrumentoProtesto(instrumento);
        }
    }

    /**
     * Remover registro da entrada do instrumento de protesto na CRA
     *
     * @param instrumentoProtesto
     */
    public void removerInstrumento(InstrumentoProtesto instrumentoProtesto) {
        instrumentoDao.removerInstrumento(instrumentoProtesto);
    }

    /**
     * Verifica se há Slips geradas e não confirmadas pelo usuário
     *
     * @return
     */
    public boolean verificarEtiquetasGeradasNaoConfimadas() {
        return instrumentoDao.verificarEtiquetasGeradasNaoConfimadas();
    }

    /**
     * Buscar instrumento de protesto para retorno de protestado

     * @param retorno
     * @return
     */
    public InstrumentoProtesto buscarInstrumentoProtesto(Retorno retorno) {
        return instrumentoDao.buscarInstrumentoProtesto(retorno);
    }

    /**
     * Processador de instrumentos de protesto e gerador de Slips, Envelpoes e listagem
     *
     * @param instrumentos
     * @param listaRetorno
     * @return
     */
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
				throw new InfraException("Os instrumentos de protestos já tiveram as Slips geradas. Por favor clique em confirmar para marca-los como gerados!");
			}
			RegraAgenciaDestino regraAgencia = regraAgenciaDestino.regraAgenciaDestino(instrumento.getTituloRetorno().getTitulo());
			instrumento.getTituloRetorno().setRemessa(instrumentoDao.buscarPorPK(instrumento.getTituloRetorno().getRemessa(), Remessa.class));

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
		    String codigoPortador = etiqueta.getInstrumentoProtesto().getTituloRetorno().getCodigoPortador();
            chaveEnvelope chavaEnvelope = new chaveEnvelope(codigoPortador, etiqueta.getAgenciaDestino());

			if (mapaEnvelopes.containsKey(Integer.parseInt(chavaEnvelope.toString()))) {
				EnvelopeSLIP envelope = mapaEnvelopes.get(Integer.parseInt(chavaEnvelope.toString()));
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
				mapaEnvelopes.put(Integer.parseInt(chavaEnvelope.toString()), envelope);
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
		codeBar = codeBar.concat(dataPadraEnvelope.format(new Date()));
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

    /**
     * Busca os instrumentos de protesto marcados como não gerados para que
     * seja  gerada a Slip e os Envelopes.
     * @return
     */
    @Transactional(readOnly = true)
    public List<InstrumentoProtesto> buscarInstrumentosParaSlip() {
		return instrumentoDao.buscarInstrumentosParaSlip();
	}

	public List<InstrumentoProtesto> getInstrumentosProtesto() {
		return instrumentosProtesto;
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