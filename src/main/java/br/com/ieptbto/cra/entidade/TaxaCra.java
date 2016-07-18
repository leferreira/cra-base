package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_TAXA_CRA")
@org.hibernate.annotations.Table(appliesTo = "TB_TAXA_CRA")
public class TaxaCra extends AbstractEntidade<TaxaCra> {

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private Date dataInicio;
	private Date dataFim;
	private BigDecimal valor;

	@Override
	@Id
	@Column(name = "ID_TAXA_CRA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INICIO")
	public Date getDataInicio() {
		return dataInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_FIM")
	public Date getDataFim() {
		return dataFim;
	}

	@Column(name = "VALOR")
	public BigDecimal getValor() {
		return valor;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Override
	public int compareTo(TaxaCra entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaxaCra) {
			TaxaCra modalidade = TaxaCra.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getDataInicio(), modalidade.getDataInicio());
			equalsBuilder.append(this.getValor(), modalidade.getValor());
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
}
