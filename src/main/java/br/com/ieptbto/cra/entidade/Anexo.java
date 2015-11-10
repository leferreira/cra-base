package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private String nomeAnexo;
	private Arquivo arquivo;

	@Id
	@Column(name = "ID_ANEXO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name="NOME_ANEXO", length=45)
	public String getNomeAnexo() {
		return nomeAnexo;
	}

	@ManyToOne
	@JoinColumn(name = "ARQUIVO_ID")
	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNomeAnexo(String nomeAnexo) {
		this.nomeAnexo = nomeAnexo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	@Override
	public int compareTo(Anexo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
