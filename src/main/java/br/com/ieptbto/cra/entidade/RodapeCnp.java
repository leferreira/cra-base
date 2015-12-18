package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_RODAPE_CNP")
@org.hibernate.annotations.Table(appliesTo = "TB_RODAPE_CNP")
public class RodapeCnp extends AbstractEntidade<RodapeCnp> {

	private static final long serialVersionUID = 1L;
	private int id;
	private RemessaCnp remessa;
	private String codigoRegistro;
	private String sequencialRegistro;

	@Override
	@Id
	@Column(name = "ID_RODAPE_CNP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	@OneToOne(mappedBy = "rodape")
	public RemessaCnp getRemessa() {
		return remessa;
	}

	@Column(name = "CODIGO_REGISTRO")
	public String getCodigoRegistro() {
		return codigoRegistro;
	}

	@Column(name = "SEQUENCIAL_REGISTRO")
	public String getSequencialRegistro() {
		return sequencialRegistro;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(RemessaCnp remessa) {
		this.remessa = remessa;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public void setSequencialRegistro(String sequencialRegistro) {
		this.sequencialRegistro = sequencialRegistro;
	}

	@Override
	public int compareTo(RodapeCnp entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
