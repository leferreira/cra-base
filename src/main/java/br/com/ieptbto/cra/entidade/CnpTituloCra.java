package br.com.ieptbto.cra.entidade;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 *
 */
@Entity
@Audited
@Table(name = "TB_CNP_TITULO_CRA")
@org.hibernate.annotations.Table(appliesTo = "TB_CNP_TITULO_CRA")
@PrimaryKeyJoinColumn(name="id")
public class CnpTituloCra extends CnpTitulo {

	/***/
	private static final long serialVersionUID = 1L;
	private String titulo;
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Override
	public int compareTo(CnpTitulo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}