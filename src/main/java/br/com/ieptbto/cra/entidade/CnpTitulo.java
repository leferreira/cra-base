package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.poi.ss.formula.functions.T;
import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 *
 */
@Entity
@Audited
@Table(name="TB_CNP_TITULO")
@org.hibernate.annotations.Table(appliesTo = "TB_CNP_TITULO")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CnpTitulo extends AbstractEntidade<CnpTitulo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	private int id;
	private List<CnpOcorrencia> ocorrencias; 

	@Override
	@Id
	@Column(name = "ID_CNP_TITULO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	
	@OneToMany(mappedBy="cnpTitulo")
	public List<CnpOcorrencia> getOcorrencias() {
		return ocorrencias;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setOcorrencias(List<CnpOcorrencia> ocorrencias) {
		this.ocorrencias = ocorrencias;
	}

	public int compareTo(T entidade) {
		return 0;
	};
}
