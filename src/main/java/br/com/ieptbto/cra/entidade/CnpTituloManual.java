package br.com.ieptbto.cra.entidade;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_CNP_TITULO_MANUAL")
@org.hibernate.annotations.Table(appliesTo = "TB_CNP_TITULO_MANUAL")
@PrimaryKeyJoinColumn(name="id")
public class CnpTituloManual extends CnpTitulo {

	/***/
	private static final long serialVersionUID = 1L;
	private String teste;

	public String getTeste() {
		return teste;
	}

	public void setTeste(String teste) {
		this.teste = teste;
	}

	@Override
	public int compareTo(CnpTitulo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
