package br.com.ieptbto.cra.exception;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.error.CodigoErro;

/**
 * @author Thasso Araújo
 *
 */
public class ArquivoException extends RuntimeException {

	/** **/
	private static final long serialVersionUID = 1L;

	private CodigoErro codigoErro;
	private String nomeArquivo;
	private String descricao;
	private LocalDate dataEnvio;

	public ArquivoException(CodigoErro codigoErro, String nomeArquivo, LocalDate dataEnvio) {
		super(codigoErro.getDescricao());
		this.descricao = codigoErro.getDescricao();
		this.codigoErro = codigoErro;
		this.dataEnvio = dataEnvio;
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = StringUtils.EMPTY;
		}
		return descricao;
	}

	public String getNomeArquivo() {
		if (nomeArquivo == null) {
			nomeArquivo = StringUtils.EMPTY;
		}
		return nomeArquivo;
	}

	public LocalDate getDataEnvio() {
		return dataEnvio;
	}

	public CodigoErro getCodigoErro() {
		if (codigoErro == null) {
			codigoErro = CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO;
		}
		return codigoErro;
	}
}