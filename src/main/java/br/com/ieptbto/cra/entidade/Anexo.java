package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "TB_ANEXO")
@org.hibernate.annotations.Table(appliesTo = "TB_ANEXO")
public class Anexo extends AbstractEntidade<Anexo> {

	/****/
	private static final long serialVersionUID = 123523;

	private int id;
	private TituloRemessa titulo;

	@Id
	@Column(name = "ID_ANEXO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name = "TITULO_ID")
	public TituloRemessa getTitulo() {
		return titulo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitulo(TituloRemessa Titulo) {
		this.titulo = Titulo;
	}

	@Override
	public int compareTo(Anexo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
