package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "TB_REMESSA_CNP")
@org.hibernate.annotations.Table(appliesTo = "TB_REMESSA_CNP")
public class RemessaCnp extends AbstractEntidade<RemessaCnp> {

	private static final long serialVersionUID = 1L;
	private int id;
	private ArquivoCnp arquivo;
	private CabecalhoCnp cabecalho;
	private List<TituloCnp> titulos;
	private RodapeCnp rodape;
	
	@Override
	@Id
	@Column(name = "ID_REMESSA_CNP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	@ManyToOne
	@JoinColumn(name = "ARQUIVO_CNP_ID")
	public ArquivoCnp getArquivo() {
		return arquivo;
	}

	@OneToOne
	@JoinColumn(name = "CABECALHO_CNP_ID")
	public CabecalhoCnp getCabecalho() {
		return cabecalho;
	}

	@OneToMany(mappedBy = "remessa", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public List<TituloCnp> getTitulos() {
		return titulos;
	}

	@OneToOne
	@JoinColumn(name = "RODAPE_CNP_ID")
	public RodapeCnp getRodape() {
		return rodape;
	}

	public void setTitulos(List<TituloCnp> titulos) {
		this.titulos = titulos;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setArquivo(ArquivoCnp arquivo) {
		this.arquivo = arquivo;
	}

	public void setCabecalho(CabecalhoCnp cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setRodape(RodapeCnp rodape) {
		this.rodape = rodape;
	}

	@Override
	public int compareTo(RemessaCnp entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
