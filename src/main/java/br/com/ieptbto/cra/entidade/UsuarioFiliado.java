package br.com.ieptbto.cra.entidade;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@PrimaryKeyJoinColumn(name="id")
@Table(name = "TB_USUARIO_FILIADO")
@org.hibernate.annotations.Table(appliesTo = "TB_USUARIO_FILIADO")
public class UsuarioFiliado extends Usuario {

	/***/
	private static final long serialVersionUID = 1L;
	private Filiado filiado;
	
	@ManyToOne
	@JoinColumn(name="FILIADO_ID")
	public Filiado getFiliado() {
		return filiado;
	}
	
	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}
}
