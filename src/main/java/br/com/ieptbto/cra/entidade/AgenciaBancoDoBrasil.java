package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_AGENCIA_BANCO_BRASIL")
@org.hibernate.annotations.Table(appliesTo = "TB_AGENCIA_BANCO_BRASIL")
public class AgenciaBancoDoBrasil extends AbstractEntidade<AgenciaBancoDoBrasil> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String numeroContrato;
	private String agenciaDestino;

	@Id
	@Column(name = "ID_AGENCIA_BANCO_BRASIL", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="NUMERO_CONTRATO", length=9)
	public String getNumeroContrato() {
		return numeroContrato;
	}

	@Column(name="AGENCIA_DESTINO", length=4)
	public String getAgenciaDestino() {
		return agenciaDestino;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public void setAgenciaDestino(String agenciaDestino) {
		this.agenciaDestino = agenciaDestino;
	}

	@Override
	public int compareTo(AgenciaBancoDoBrasil entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
