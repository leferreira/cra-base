package br.com.ieptbto.cra.slip.regra;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.enumeration.BancoTipoRegraBasicaInstrumento;

/**
 * @author Thasso Araújo
 *
 */
public abstract class RegraInstrumentoProtesto {

	protected static final Logger logger = Logger.getLogger(RegraInstrumentoProtesto.class);

	protected abstract String aplicarRegraBasica(BancoTipoRegraBasicaInstrumento bancoTipoRegra);

}
