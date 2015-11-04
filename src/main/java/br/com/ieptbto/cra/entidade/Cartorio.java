package br.com.ieptbto.cra.entidade;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Cartorio extends Instituicao {


	/***/
	private static final long serialVersionUID = 1L;
	
	private Municipio municipio;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional=true)
	@JoinColumn(name = "MUNICIPIO_ID", columnDefinition = "integer NULL", nullable=true)
	public Municipio getMunicipio() {
		return municipio;
	}
	
	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cartorio) {
			Cartorio modalidade = Cartorio.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getRazaoSocial(), modalidade.getRazaoSocial());
			equalsBuilder.append(this.getCnpj(), modalidade.getCnpj());
			equalsBuilder.append(this.getMunicipio(), modalidade.getMunicipio());
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
}
