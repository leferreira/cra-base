package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;

import javax.persistence.*;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
@MappedSuperclass
public abstract class RodapeDesistenciaCancelamento<T> extends AbstractEntidade<T> {

	/***/
	private static final long serialVersionUID = 1L;
	private TipoRegistroDesistenciaProtesto identificacaoRegistro;
	private Integer somaTotalCancelamentoDesistencia;
	private String reservado;
	private String sequencialRegistro;

	@Override
	@Transient
	public abstract int getId();

	@Column(name = "TIPO_REGISTO", length = 30)
	@Enumerated(EnumType.STRING)
	public TipoRegistroDesistenciaProtesto getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "SOMA_TOTAL_REGISTROS")
	public Integer getSomaTotalCancelamentoDesistencia() {
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

	public void setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setSomaTotalCancelamentoDesistencia(Integer somaTotalCancelamentoDesistencia) {
		this.somaTotalCancelamentoDesistencia = somaTotalCancelamentoDesistencia;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setSequencialRegistro(String sequencialRegistro) {
		this.sequencialRegistro = sequencialRegistro;
	}
}
