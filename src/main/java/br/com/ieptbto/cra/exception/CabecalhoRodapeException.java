package br.com.ieptbto.cra.exception;

import br.com.ieptbto.cra.error.CodigoErro;
import org.apache.commons.lang.StringUtils;

/**
 * @author Thasso Araújo
 *
 */
public class CabecalhoRodapeException extends RuntimeException {

	/***/
	private static final long serialVersionUID = 1L;
	private CodigoErro codigoErro;
	private String descricao;
	private String codigoMunicipio;

	public CabecalhoRodapeException(CodigoErro codigoErro) {
		super(codigoErro.getDescricao());
		this.descricao = codigoErro.getDescricao();
		this.codigoErro = codigoErro;
	}

	public CabecalhoRodapeException(CodigoErro codigoErro, String codigoMunicipio) {
		super(codigoErro.getDescricao());
		this.descricao = codigoErro.getDescricao();
		this.codigoErro = codigoErro;
		this.codigoMunicipio = codigoMunicipio;
	}

	public String getCodigoMunicipio() {
		if (StringUtils.isBlank(codigoMunicipio)) {
			codigoMunicipio = StringUtils.EMPTY;
		}
		return codigoMunicipio;
	}

	public String getDescricao() {
		this.descricao = getCodigoErro().getDescricao();
		return descricao;
	}

	public CodigoErro getCodigoErro() {
		if (codigoErro == null) {
			codigoErro = CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO;
		}
		return codigoErro;
	}
	
	public void setCodigoErro(CodigoErro codigoErro) {
		this.codigoErro = codigoErro;
	}
}