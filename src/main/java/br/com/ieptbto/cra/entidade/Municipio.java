package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "TB_MUNICIPIO")
@org.hibernate.annotations.Table(appliesTo = "TB_MUNICIPIO")
public class Municipio extends AbstractEntidade<Municipio> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String nomeMunicipio;
	private String nomeMunicipioSemAcento;
	private String uf;
	private String codigoIBGE;
	private boolean situacao;

	@Id
	@Column(name = "ID_MUNICIPIO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_MUNICIPIO", nullable = false, unique = true, length = 60)
	public String getNomeMunicipio() {
		return nomeMunicipio;
	}

	@Column(name = "UF", nullable = false, length = 2)
	public String getUf() {
		return uf;
	}

	@Column(name = "COD_IBGE", nullable = false, unique = true, length = 7)
	public String getCodigoIBGE() {
		return codigoIBGE;
	}

	@Column(name = "SITUACAO")
	public boolean isSituacao() {
		return situacao;
	}

	@Column(name = "NOME_MUNICIPIO_SEM_ACENTO")
	public String getNomeMunicipioSemAcento() {
		return nomeMunicipioSemAcento;
	}

	public void setNomeMunicipioSemAcento(String nomeMunicipioSemAcento) {
		this.nomeMunicipioSemAcento = nomeMunicipioSemAcento;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNomeMunicipio(String nomeMunicipio) {
		this.nomeMunicipio = nomeMunicipio;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public void setCodigoIBGE(String codIBGE) {
		this.codigoIBGE = codIBGE;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	@Override
	public int compareTo(Municipio entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		compareToBuilder.append(this.getCodigoIBGE(), entidade.getCodigoIBGE());
		return compareToBuilder.toComparison();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Municipio) {
			Municipio modalidade = Municipio.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getCodigoIBGE(), modalidade.getCodigoIBGE());
			return equalsBuilder.isEquals();
		}
		return false;
	}
}
