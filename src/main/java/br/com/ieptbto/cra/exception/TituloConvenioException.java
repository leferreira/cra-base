package br.com.ieptbto.cra.exception;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.TituloConvenioVO;
import br.com.ieptbto.cra.error.CodigoErro;
import org.apache.commons.lang.StringUtils;

/**
 * @author Thasso Araújo
 *
 */
public class TituloConvenioException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String numeroTitulo;
	private String nomeDevedor;
	private CodigoErro codigoErro;
	private String descricao;

	public TituloConvenioException(CodigoErro codigoErro, TituloConvenioVO tituloConvenioVO) {
		super(codigoErro.getDescricao());
		this.descricao = codigoErro.getDescricao();
		this.codigoErro = codigoErro;
		this.nomeDevedor = tituloConvenioVO.getNomeDevedor();
		this.numeroTitulo = tituloConvenioVO.getNumeroTitulo();
	}

    public TituloConvenioException(CodigoErro codigoErro, TituloRemessa titulo) {
        super(codigoErro.getDescricao());
        this.descricao = codigoErro.getDescricao();
        this.codigoErro = codigoErro;
        this.nomeDevedor = titulo.getNomeDevedor();
        this.numeroTitulo = titulo.getNumeroTitulo();
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

    public String getNomeDevedor() {
        if (nomeDevedor == null) {
            nomeDevedor = StringUtils.EMPTY;
        }
	    return nomeDevedor;
    }

    public String getNumeroTitulo() {
        if (numeroTitulo == null) {
            numeroTitulo = StringUtils.EMPTY;
        }
        return numeroTitulo;
    }
}
