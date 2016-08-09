package br.com.ieptbto.cra.exception;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.error.CodigoErro;

/**
 * @author Thasso Araújo
 *
 */
public class TituloException extends RuntimeException {

	/** **/
	private static final long serialVersionUID = 1L;

	private String nossoNumero;
	private int numeroSequencialRegistro;
	private CodigoErro codigoErro;
	private String descricao;

	public TituloException(String message, CodigoErro codigoErro, String nossoNumero, int numeroSequencialRegistro) {
		super(message);
		this.descricao = message;
		this.nossoNumero = nossoNumero;
		this.codigoErro = codigoErro;
		this.numeroSequencialRegistro = numeroSequencialRegistro;
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = StringUtils.EMPTY;
		}
		return descricao;
	}

	public String getNossoNumero() {
		if (nossoNumero == null) {
			nossoNumero = StringUtils.EMPTY;
		}
		return nossoNumero;
	}

	public CodigoErro getCodigoErro() {
		if (codigoErro == null) {
			codigoErro = CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO;
		}
		return codigoErro;
	}

	public int getNumeroSequencialRegistro() {
		return numeroSequencialRegistro;
	}
}
