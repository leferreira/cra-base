package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;

@Entity
@Audited
@Table(name = "TB_TIPO_INSTITUICAO")
@org.hibernate.annotations.Table(appliesTo = "TB_TIPO_INSTITUICAO")
public class TipoInstituicao extends AbstractEntidade<TipoInstituicao> {

	private static final long serialVersionUID = 1L;
	private int id;
	private TipoInstituicaoSistema tipoInstituicao;
	private List<Instituicao> listaInstituicoes;

	@Id
	@Column(name = "ID_TIPO_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "TIPO_INSTITUICAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoInstituicaoSistema getTipoInstituicao() {
		return tipoInstituicao;
	}

	@OneToMany(mappedBy = "tipoInstituicao")
	public List<Instituicao> getListaInstituicoes() {
		return listaInstituicoes;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTipoInstituicao(TipoInstituicaoSistema tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public void setListaInstituicoes(List<Instituicao> listaInstituicoes) {
		this.listaInstituicoes = listaInstituicoes;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TipoInstituicao) {
			TipoInstituicao modalidade = TipoInstituicao.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getTipoInstituicao(), modalidade.getTipoInstituicao());
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
	public int compareTo(TipoInstituicao entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}
}
