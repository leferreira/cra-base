package br.com.ieptbto.cra.exception;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.error.CodigoErro;

/**
 * @author Thasso Araújo
 *
 */
public class CabecalhoRodapeException extends RuntimeException {

	/***/
	private static final long serialVersionUID = 1L;
	private CodigoErro codigoErro;
	private String descricao;

	public CabecalhoRodapeException(CodigoErro codigoErro) {
		super(codigoErro.getDescricao());
		this.descricao = codigoErro.getDescricao();
		this.codigoErro = codigoErro;
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = StringUtils.EMPTY;
		}
		return descricao;
	}

	public CodigoErro getCodigoErro() {
		if (codigoErro == null) {
			codigoErro = CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO;
		}
		return codigoErro;
	}
}