package br.com.ieptbto.cra.exception;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.error.CodigoErro;

/**
 * @author Thasso Araújo
 *
 */
public class DesistenciaCancelamentoException extends RuntimeException {

	/***/
	private static final long serialVersionUID = 1L;
	private String codigoMunicipio;
	private CodigoErro codigoErro;
	private String descricao;

	public DesistenciaCancelamentoException(String message, String codigoMunicipio, CodigoErro codigoErro) {
		super(message);
		this.descricao = message;
		this.codigoMunicipio = codigoMunicipio;
		this.codigoErro = codigoErro;
	}

	public DesistenciaCancelamentoException(String message, String codigoMunicipio) {
		super(message);
		this.descricao = message;
		this.codigoMunicipio = codigoMunicipio;
		this.codigoErro = CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO;
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = StringUtils.EMPTY;
		}
		return descricao;
	}

	public String getCodigoMunicipio() {
		if (codigoMunicipio == null) {
			codigoMunicipio = StringUtils.EMPTY;
		}
		return codigoMunicipio;
	}

	public CodigoErro getCodigoErro() {
		if (codigoErro == null) {
			codigoErro = CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO;
		}
		return codigoErro;
	}
}