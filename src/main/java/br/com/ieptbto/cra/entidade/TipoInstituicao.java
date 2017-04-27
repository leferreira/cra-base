package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Table(name = "TB_TIPO_INSTITUICAO")
@org.hibernate.annotations.Table(appliesTo = "TB_TIPO_INSTITUICAO")
public class TipoInstituicao extends AbstractEntidade<TipoInstituicao> {

	private static final long serialVersionUID = 1L;
	private int id;
	private TipoInstituicaoCRA tipoInstituicao;
	private List<Instituicao> listaInstituicoes;

	@Id
	@Column(name = "ID_TIPO_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "TIPO_INSTITUICAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoInstituicaoCRA getTipoInstituicao() {
		return tipoInstituicao;
	}

	@OneToMany(mappedBy = "tipoInstituicao")
	public List<Instituicao> getListaInstituicoes() {
		return listaInstituicoes;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTipoInstituicao(TipoInstituicaoCRA tipoInstituicao) {
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
