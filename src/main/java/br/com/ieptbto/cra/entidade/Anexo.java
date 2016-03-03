package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.bytecode.internal.javassist.FieldHandled;
import org.hibernate.bytecode.internal.javassist.FieldHandler;
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
public class Anexo extends AbstractEntidade<Anexo> implements FieldHandled {

	/****/
	private static final long serialVersionUID = 123523;

	private int id;
	private String documentoAnexo;
	private TituloRemessa titulo;
	private FieldHandler handler;

	@Id
	@Column(name = "ID_ANEXO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TITULO_ID")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public TituloRemessa getTitulo() {
		if (this.handler != null) {
			return (TituloRemessa) this.handler.readObject(this, "titulo", titulo);
		}
		return titulo;
	}

	@Column(name = "DOCUMENTO_ANEXO", columnDefinition = "text")
	public String getDocumentoAnexo() {
		return documentoAnexo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitulo(TituloRemessa Titulo) {
		if (this.handler != null) {
			this.titulo = (TituloRemessa) this.handler.writeObject(this, "titulo", this.titulo, titulo);
		}
		this.titulo = Titulo;
	}

	public void setDocumentoAnexo(String documentoAnexo) {
		this.documentoAnexo = documentoAnexo;
	}

	@Override
	public int compareTo(Anexo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFieldHandler(FieldHandler handler) {
		this.handler = handler;

	}

	@Override
	public FieldHandler getFieldHandler() {
		return this.handler;
	}
}
