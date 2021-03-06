package br.com.ieptbto.cra.entidade;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "TB_BATIMENTO_DEPOSITO")
@org.hibernate.annotations.Table(appliesTo = "TB_BATIMENTO_DEPOSITO")
public class BatimentoDeposito extends AbstractEntidade<BatimentoDeposito> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private Deposito deposito;
	private Batimento batimento;

	@Id
	@Column(name = "TB_BATIMENTO_DEPOSITO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "DEPOSITO_ID")
	public Deposito getDeposito() {
		return deposito;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BATIMENTO_ID")
	public Batimento getBatimento() {
		return batimento;
	}

	public void setBatimento(Batimento batimento) {
		this.batimento = batimento;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDeposito(Deposito deposito) {
		this.deposito = deposito;
	}

	@Override
	public int compareTo(BatimentoDeposito entidade) {
		return 0;
	}
}
