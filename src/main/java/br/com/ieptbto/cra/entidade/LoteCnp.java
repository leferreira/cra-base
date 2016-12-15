package br.com.ieptbto.cra.entidade;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "TB_LOTE_CNP")
@org.hibernate.annotations.Table(appliesTo = "TB_LOTE_CNP")
public class LoteCnp extends AbstractEntidade<LoteCnp> {

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private List<RegistroCnp> registrosCnp;
	private Date dataRecebimento;
	private Instituicao instituicaoOrigem;
	private Date dataLiberacao;
	private int sequencialLiberacao;
	private Boolean status;
	private boolean lote5anos;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_LOTE_CNP", columnDefinition = "serial")
	public int getId() {
		return id;
	}

	@OneToMany(mappedBy = "loteCnp", fetch = FetchType.LAZY)
	public List<RegistroCnp> getRegistrosCnp() {
		return registrosCnp;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_RECEBIMENTO")
	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "INSTITUICAO_ORIGEM_ID")
	public Instituicao getInstituicaoOrigem() {
		return instituicaoOrigem;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_LIBERACAO")
	public Date getDataLiberacao() {
		return dataLiberacao;
	}

	@Column(name = "SEQUENCIAL_LIBERACAO")
	public int getSequencialLiberacao() {
		return sequencialLiberacao;
	}

	@Column(name = "STATUS")
	public Boolean getStatus() {
		return status;
	}

	@Column(name = "LOTE_5_ANOS")
	public boolean isLote5anos() {
		return lote5anos;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRegistrosCnp(List<RegistroCnp> registrosCnp) {
		this.registrosCnp = registrosCnp;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public void setInstituicaoOrigem(Instituicao instituicaoOrigem) {
		this.instituicaoOrigem = instituicaoOrigem;
	}

	public void setDataLiberacao(Date dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}

	public void setSequencialLiberacao(int sequencialLiberacao) {
		this.sequencialLiberacao = sequencialLiberacao;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setLote5anos(boolean lote5anos) {
		this.lote5anos = lote5anos;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LoteCnp) {
			LoteCnp modalidade = LoteCnp.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getDataRecebimento(), modalidade.getDataRecebimento());
			equalsBuilder.append(this.getInstituicaoOrigem(), modalidade.getInstituicaoOrigem());
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
	public int compareTo(LoteCnp entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}
}
