package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "TB_USUARIO_FILIADO")
@org.hibernate.annotations.Table(appliesTo = "TB_USUARIO_FILIADO")
public class UsuarioFiliado extends AbstractEntidade<UsuarioFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private Filiado filiado;
	private Usuario usuario;

	@Override
	@Id
	@Column(name = "ID_USUARIO_FILIADO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name = "USUARIO_ID")
	public Usuario getUsuario() {
		return usuario;
	}

	@ManyToOne
	@JoinColumn(name = "FILIADO_ID")
	public Filiado getFiliado() {
		return filiado;
	}

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public int compareTo(UsuarioFiliado entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
