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

	@Override
	public int compareTo(LoteCnp entidade) {
		return 0;
	}
}
