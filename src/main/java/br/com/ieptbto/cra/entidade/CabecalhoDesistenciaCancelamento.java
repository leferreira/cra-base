package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;

import javax.persistence.*;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
@MappedSuperclass
public abstract class CabecalhoDesistenciaCancelamento<T> extends AbstractEntidade<T> {

	/***/
	private static final long serialVersionUID = 1L;
	private TipoRegistroDesistenciaProtesto identificacaoRegistro;
	private Integer quantidadeDesistencia;
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

	@Column(name = "QUANTIDADE_CANCELAMENTO_DESISTENCIA")
	public Integer getQuantidadeDesistencia() {
		return quantidadeDesistencia;
	}

	@Column(name = "RESERVADO")
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

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setSequencialRegistro(String sequencialRegistro) {
		this.sequencialRegistro = sequencialRegistro;
	}

	public void setQuantidadeDesistencia(Integer quantidadeDesistencia) {
		this.quantidadeDesistencia = quantidadeDesistencia;
	}
}
