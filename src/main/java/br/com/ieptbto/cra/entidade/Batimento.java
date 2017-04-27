package br.com.ieptbto.cra.entidade;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.*;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_BATIMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_BATIMENTO")
public class Batimento extends AbstractEntidade<Batimento> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private LocalDateTime dataBatimento;
	private LocalDate data;
	private Remessa remessa;
	private List<BatimentoDeposito> depositosBatimento;

	@Id
	@Column(name = "ID_BATIMENTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "DATA_BATIMENTO")
	public LocalDateTime getDataBatimento() {
		if (dataBatimento == null) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
			DateTime dt = formatter.parseDateTime("01/01/2000 00:00:00");
			dataBatimento = data.toLocalDateTime(new LocalTime(dt));
		}
		return dataBatimento;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REMESSA_ID")
	public Remessa getRemessa() {
		return remessa;
	}

	@OneToMany(mappedBy = "batimento", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public List<BatimentoDeposito> getDepositosBatimento() {
		return depositosBatimento;
	}

	@Column(name = "DATA")
	public LocalDate getData() {
		return data;
	}

	public void setDepositosBatimento(List<BatimentoDeposito> depositosBatimento) {
		this.depositosBatimento = depositosBatimento;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public void setDataBatimento(LocalDateTime dataBatimento) {
		this.dataBatimento = dataBatimento;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	@Override
	public int compareTo(Batimento entidade) {
		CompareToBuilder compare = new CompareToBuilder();
		compare.append(entidade.getId(), this.getId());
		compare.append(entidade.getData(), this.getData());
		compare.append(entidade.getRemessa(), this.getRemessa());
		return compare.toComparison();
	}

}