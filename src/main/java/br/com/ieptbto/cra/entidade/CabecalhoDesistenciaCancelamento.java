package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaCancelamento;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
@MappedSuperclass
public abstract class CabecalhoDesistenciaCancelamento<T> extends AbstractEntidade<T> {

	/***/
	private static final long serialVersionUID = 1L;
	private TipoRegistroDesistenciaCancelamento TipoRegistroDesistenciaCancelamento;
	private int quantidadeCancelamentoDesistencia;
	private String reservado;
	private String sequencialRegistro;
	
	@Override
	public abstract int getId();

	@Column(name = "TIPO_REGISTO", length = 30)
	@Enumerated(EnumType.STRING)
	public TipoRegistroDesistenciaCancelamento getTipoRegistroDesistenciaCancelamento() {
		return TipoRegistroDesistenciaCancelamento;
	}

	@Column(name = "QUANTIDADE_CANCELAMENTO_DESISTENCIA")
	public int getQuantidadeCancelamentoDesistencia() {
		return quantidadeCancelamentoDesistencia;
	}

	@Column(name = "RESERVADO")
	public String getReservado() {
		return reservado;
	}

	@Column(name = "SEQUENCIAL_REGISTRO", length = 5)
	public String getSequencialRegistro() {
		return sequencialRegistro;
	}

	public void setTipoRegistroDesistenciaCancelamento(TipoRegistroDesistenciaCancelamento TipoRegistroDesistenciaCancelamento) {
		this.TipoRegistroDesistenciaCancelamento = TipoRegistroDesistenciaCancelamento;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setSequencialRegistro(String sequencialRegistro) {
		this.sequencialRegistro = sequencialRegistro;
	}


	public void setQuantidadeCancelamentoDesistencia(
			int quantidadeCancelamentoDesistencia) {
		this.quantidadeCancelamentoDesistencia = quantidadeCancelamentoDesistencia;
	}
}
