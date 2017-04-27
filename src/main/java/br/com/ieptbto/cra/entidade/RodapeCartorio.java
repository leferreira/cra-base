package br.com.ieptbto.cra.entidade;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_RODAPE_CARTORIO_DESISTENCIA_CANCELAMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_RODAPE_CARTORIO_DESISTENCIA_CANCELAMENTO")
public class RodapeCartorio extends RodapeDesistenciaCancelamento<RodapeCartorio> {

	/**/
	private static final long serialVersionUID = 1L;

	private int id;
	private String codigoCartorio;

	@Override
	@Id
	@Column(name = "ID_RODAPE_CARTORIO_DP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "CODIGO_CARTORIO", length = 2)
	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCodigoCartorio(String codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}

	@Override
	public int compareTo(RodapeCartorio entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
