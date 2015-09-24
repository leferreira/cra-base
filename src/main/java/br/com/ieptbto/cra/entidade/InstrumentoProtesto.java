package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

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
	
	@Id
	@Column(name = "ID_INSTRUMENTO_PROTESTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name="RETORNO_ID")
	public Retorno getTituloRetorno() {
		return tituloRetorno;
	}

	public void setTituloRetorno(Retorno tituloRetorno) {
		this.tituloRetorno = tituloRetorno;
	}

	@Column(name = "DATA_ENTRADA")
	public LocalDate getDataDeEntrada() {
		return dataDeEntrada;
	}

	public void setDataDeEntrada(LocalDate dataDeEntrada) {
		this.dataDeEntrada = dataDeEntrada;
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

}
