package br.com.ieptbto.cra.exception;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;

public class AutorizacaoCancelamentoException extends RuntimeException {

	/** **/
	private static final long serialVersionUID = 1L;
	private final ErroTitulo erro;
	private final String campo;
	private final List<Exception> erros;
	private final List<PedidoAutorizacaoCancelamento> pedidosAutorizacaoCancelamento;

	/**
	 * Construtor.
	 * 
	 * @param erro
	 * @param campo
	 */
	public AutorizacaoCancelamentoException(ErroTitulo erro, String campo) {
		super(erro.getMensagemErro());
		this.erro = erro;
		this.campo = campo;
		this.erros = new ArrayList<Exception>();
		this.pedidosAutorizacaoCancelamento = new ArrayList<PedidoAutorizacaoCancelamento>();
	}
	
	/**
	 * @param message
	 * @param erros
	 * @param pedidosComErros
	 */
	public AutorizacaoCancelamentoException(String message, List<Exception> erros, List<PedidoAutorizacaoCancelamento> pedidosComErros) {
		super(message);
		this.erro = ErroTitulo.CAMPOS_INCONSISTENTES;
		this.campo = StringUtils.EMPTY;
		this.erros = erros;
		this.pedidosAutorizacaoCancelamento = pedidosComErros;
	}
	
	/**
	 * Método responsável por retornar erro.
	 * 
	 * @return erro
	 */
	public ErroTitulo getErro() {
		return erro;
	}
	
	/**
	 * Método responsável por retornar erro.
	 * 
	 * @return erro
	 */
	public List<Exception> getErros() {
		return erros;
	}

	/**
	 * Método responsável por retornar campo.
	 * 
	 * @return campo
	 */
	public String getCampo() {
		return campo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return erro + " " + campo;
	}

	public List<PedidoAutorizacaoCancelamento> getPedidosAutorizacaoCancelamento() {
		return pedidosAutorizacaoCancelamento;
	}
}
