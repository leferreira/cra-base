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

import br.com.ieptbto.cra.conversor.arquivo.RetornoConversor;
import br.com.ieptbto.cra.entidade.vo.TituloVO;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_RETORNO")
@org.hibernate.annotations.Table(appliesTo = "TB_RETORNO")
public class Retorno extends Titulo<Retorno> {

	private CabecalhoRemessa cabecalho;
	private int id;
	private TituloRemessa titulo;

	@Id
	@Column(name = "ID_RETORNO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public int getId() {
		return this.id;
	}

	@ManyToOne
	@JoinColumn(name = "CABECALHO_ID")
	public CabecalhoRemessa getCabecalho() {
		return cabecalho;
	}

	@ManyToOne
	@JoinColumn(name = "TITULO_ID")
	public TituloRemessa getTitulo() {
		return titulo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCabecalho(CabecalhoRemessa cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.titulo = titulo;
	}

	public static Retorno parseTituloVO(TituloVO tituloVO) {
		Retorno titulo = new RetornoConversor().converter(Retorno.class, tituloVO);
		return titulo;
	}

	@Override
	public int compareTo(Retorno entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		compareToBuilder.append(this.getNossoNumero(), entidade.getNossoNumero());
		compareToBuilder.append(this.getNumeroProtocoloCartorio(), getNumeroProtocoloCartorio());

		return compareToBuilder.toComparison();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Retorno) {
			Retorno modalidade = Retorno.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getNossoNumero(), modalidade.getNossoNumero());
			equalsBuilder.append(this.getNumeroProtocoloCartorio(), modalidade.getNumeroProtocoloCartorio());
			return equalsBuilder.isEquals();
		}
		return false;
	}
}
