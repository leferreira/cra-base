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
@Table(name = "TB_CABECALHO_CARTORIO_DP_CP")
@org.hibernate.annotations.Table(appliesTo = "TB_CABECALHO_CARTORIO_DP_CP")
public class CabecalhoCartorio extends	CabecalhoDesistenciaCancelamento<CabecalhoCartorio> {

	/****/
	private static final long serialVersionUID = 1L;

	private int id;
	private String codigoCartorio;

	@Override
	@Id
	@Column(name = "ID_CABECALHO_CARTORIO_DP_CP", columnDefinition = "serial")
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
	public int compareTo(CabecalhoCartorio entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}