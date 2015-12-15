package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.CascadeType;
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

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

/**
 * 
 * @author Lefer
 *
 */

@Entity
@Audited
@Table(name = "TB_ARQUIVO_CNP")
@org.hibernate.annotations.Table(appliesTo = "TB_ARQUIVO_CNP")
public class ArquivoCnp extends AbstractEntidade<ArquivoCnp> {

	private static final long serialVersionUID = 1L;

	private int id;
	private List<RemessaCnp> remessaCnp;
	private LocalDate dataEnvio;
	private Instituicao instituicaoEnvio;

	@Override
	@Id
	@Column(name = "ID_ARQUIVO_CNP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	@OneToMany(mappedBy = "arquivo", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public List<RemessaCnp> getRemessaCnp() {
		return remessaCnp;
	}

	@Column(name = "DATA_ENVIO")
	public LocalDate getDataEnvio() {
		return dataEnvio;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "INSTITUICAO_ENVIO_ID")
	public Instituicao getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessaCnp(List<RemessaCnp> remessaCnp) {
		this.remessaCnp = remessaCnp;
	}

	public void setDataEnvio(LocalDate dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setInstituicaoEnvio(Instituicao instituicaoEnvio) {
		this.instituicaoEnvio = instituicaoEnvio;
	}

	@Override
	public int compareTo(ArquivoCnp entidade) {
		return 0;
	}

}
