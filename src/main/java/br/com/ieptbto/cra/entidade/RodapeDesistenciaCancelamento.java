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
public abstract class RodapeDesistenciaCancelamento<T> extends AbstractEntidade<T> {

	/***/
	private static final long serialVersionUID = 1L;
	private TipoRegistroDesistenciaCancelamento tipoRegistroDesistenciaCancelamento;
	private int somaTotalCancelamentoDesistencia;
	private String reservado;
	private String sequencialRegistro;

	@Override
	public abstract int getId();

	@Column(name = "TIPO_REGISTO", length = 30)
	@Enumerated(EnumType.STRING)
	public TipoRegistroDesistenciaCancelamento getTipoRegistroDesistenciaCancelamento() {
		return tipoRegistroDesistenciaCancelamento;
	}

	@Column(name = "SOMA_TOTAL_REGISTROS")
	public int getSomaTotalCancelamentoDesistencia() {
		return somaTotalCancelamentoDesistencia;
	}

	@Column(name = "RESERVADO", length = 150)
	public String getReservado() {
		return reservado;
	}

	@Column(name = "SEQUENCIAL_REGISTRO", length = 5)
	public String getSequencialRegistro() {
		return sequencialRegistro;
	}

	public void setTipoRegistroDesistenciaCancelamento(TipoRegistroDesistenciaCancelamento tipoRegistroDesistenciaCancelamento) {
		this.tipoRegistroDesistenciaCancelamento = tipoRegistroDesistenciaCancelamento;
	}

	public void setSomaTotalCancelamentoDesistencia(
			int somaTotalCancelamentoDesistencia) {
		this.somaTotalCancelamentoDesistencia = somaTotalCancelamentoDesistencia;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setSequencialRegistro(String sequencialRegistro) {
		this.sequencialRegistro = sequencialRegistro;
	}
}
