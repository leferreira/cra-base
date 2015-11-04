package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDateTime;

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
	private boolean termosContratoAceite;
	private LocalDateTime dataAceiteContrato;

	@Override
	@Id
	@Column(name = "ID_USUARIO_FILIADO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "USUARIO_ID")
	public Usuario getUsuario() {
		return usuario;
	}

	@ManyToOne
	@JoinColumn(name = "FILIADO_ID")
	public Filiado getFiliado() {
		return filiado;
	}
	
	@Column(name="TERMOS_CONTRATO_ACEITE")
	public boolean isTermosContratoAceite() {
		return termosContratoAceite;
	}

	@Column(name="DATA_CONTRATO_ACEITE")
	public LocalDateTime getDataAceiteContrato() {
		return dataAceiteContrato;
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
	
	public void setTermosContratoAceite(boolean termosContratoAceite) {
		this.termosContratoAceite = termosContratoAceite;
	}

	public void setDataAceiteContrato(LocalDateTime dataAceiteContrato) {
		this.dataAceiteContrato = dataAceiteContrato;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UsuarioFiliado) {
			UsuarioFiliado modalidade = UsuarioFiliado.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getFiliado(), modalidade.getFiliado());
			equalsBuilder.append(this.getUsuario(), modalidade.getUsuario());
			return equalsBuilder.isEquals();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (getId() == 0) {
			return 0;
		}
		return getId();
	}
	
	@Override
	public int compareTo(UsuarioFiliado entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}
}
