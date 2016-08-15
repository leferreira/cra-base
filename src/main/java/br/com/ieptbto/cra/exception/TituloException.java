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
	private String numeroSequencialRegistro;
	private CodigoErro codigoErro;
	private String descricao;

	public TituloException(CodigoErro codigoErro, String nossoNumero, String sequencialRegistro) {
		super(codigoErro.getDescricao());
		this.descricao = codigoErro.getDescricao();
		this.nossoNumero = nossoNumero;
		this.codigoErro = codigoErro;
		this.numeroSequencialRegistro = sequencialRegistro;
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

	public String getNumeroSequencialRegistro() {
		return numeroSequencialRegistro;
	}
}
