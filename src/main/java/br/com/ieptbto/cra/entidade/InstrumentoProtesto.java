package br.com.ieptbto.cra.entidade;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.*;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_INSTRUMENTO_PROTESTO")
@org.hibernate.annotations.Table(appliesTo = "TB_INSTRUMENTO_PROTESTO")
public class InstrumentoProtesto extends AbstractEntidade<InstrumentoProtesto> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private Retorno tituloRetorno;
	private LocalDate dataDeEntrada;
	private LocalTime horaEntrada;
	private EtiquetaSLIP etiquetaSlip;
	private Boolean gerado;
	private Usuario usuario;

	@Id
	@Column(name = "ID_INSTRUMENTO_PROTESTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "RETORNO_ID")
	public Retorno getTituloRetorno() {
		return tituloRetorno;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USUARIO_ID")
	public Usuario getUsuario() {
		return usuario;
	}

	public void setTituloRetorno(Retorno tituloRetorno) {
		this.tituloRetorno = tituloRetorno;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Column(name = "DATA_ENTRADA")
	public LocalDate getDataDeEntrada() {
		return dataDeEntrada;
	}

	public void setDataDeEntrada(LocalDate dataDeEntrada) {
		this.dataDeEntrada = dataDeEntrada;
	}

	@OneToOne(mappedBy = "instrumentoProtesto", fetch = FetchType.LAZY)
	public EtiquetaSLIP getEtiquetaSlip() {
		return etiquetaSlip;
	}

	public void setEtiquetaSlip(EtiquetaSLIP etiquetaSlip) {
		this.etiquetaSlip = etiquetaSlip;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InstrumentoProtesto) {
			InstrumentoProtesto modalidade = InstrumentoProtesto.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getDataDeEntrada(), modalidade.getDataDeEntrada());
			return equalsBuilder.isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (getId() == 0) {
			return 0;
		}
		return getId();
	}

	@Override
	public int compareTo(InstrumentoProtesto entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}

	@Column(name = "GERADO")
	public Boolean getGerado() {
		return gerado;
	}

	public void setGerado(Boolean gerado) {
		this.gerado = gerado;
	}

	@Column(name = "HORA_ENTRADA")
	public LocalTime getHoraEntrada() {
		return horaEntrada;
	}

	public void setHoraEntrada(LocalTime horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

}
