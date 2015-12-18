package br.com.ieptbto.cra.exception;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.entidade.PedidoDesistencia;

public class TituloException extends RuntimeException {

	/** **/
	private static final long serialVersionUID = 1L;
	private final ErroTitulo erro;
	private final String campo;
	private final List<Exception> erros;
	private final List<PedidoDesistencia> pedidosDesistencia;

	/**
	 * Construtor.
	 * 
	 * @param erro
	 * @param campo
	 */
	public TituloException(ErroTitulo erro, String campo) {
		super(erro.getMensagemErro());
		this.erro = erro;
		this.campo = campo;
		this.erros = new ArrayList<Exception>();
		this.pedidosDesistencia = new ArrayList<PedidoDesistencia>();
	}
	
	public TituloException(String message, List<Exception> erros, List<PedidoDesistencia> pedidosDesistencia) {
		super(message);
		this.erro = ErroTitulo.CAMPOS_INCONSISTENTES;
		this.campo = StringUtils.EMPTY;
		this.erros = erros;
		this.pedidosDesistencia = pedidosDesistencia;
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

	public List<PedidoDesistencia> getPedidosDesistencia() {
		return pedidosDesistencia;
	}
}
