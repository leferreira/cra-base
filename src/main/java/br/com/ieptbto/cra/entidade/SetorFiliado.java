package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

/**
 * @author Thasso Aráujo
 *
 */
@Entity
@Audited
@Table(name = "TB_SETOR_FILIADO")
@org.hibernate.annotations.Table(appliesTo = "TB_SETOR_FILIADO")
public class SetorFiliado extends AbstractEntidade<SetorFiliado>{

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private Filiado filiado;
	private String descricao;
	private boolean setorPadraoFiliado;
	private boolean situacaoAtivo;
	
	@Id
	@Column(name = "ID_SETOR_FILIADO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name="FILIADO_ID")
	public Filiado getFiliado() {
		return filiado;
	}

	@Column(name = "DESCRICAO", length=150)
	public String getDescricao() {
		return descricao;
	}
	
	@Column(name = "SETOR_PADRAO_CRA", length=150)
	public boolean isSetorPadraoFiliado() {
		return setorPadraoFiliado;
	}
	
	@Column(name = "SITUACAO_ATIVO")
	public boolean isSituacaoAtivo() {
		return situacaoAtivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public void setSituacaoAtivo(boolean situacaoAtivo) {
		this.situacaoAtivo = situacaoAtivo;
	}
	
	public void setSetorPadraoFiliado(boolean setorPadraoFiliado) {
		this.setorPadraoFiliado = setorPadraoFiliado;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SetorFiliado) {
			SetorFiliado modalidade = SetorFiliado.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getFiliado(), modalidade.getFiliado());
			equalsBuilder.append(this.getDescricao(), modalidade.getDescricao());
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
	public int compareTo(SetorFiliado entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}

}
