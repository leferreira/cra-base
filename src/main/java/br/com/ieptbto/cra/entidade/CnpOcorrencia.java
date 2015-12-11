package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.TipoOcorrencia;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_CNP_OCORRENCIA")
@org.hibernate.annotations.Table(appliesTo = "TB_CNP_OCORRENCIA")
public class CnpOcorrencia extends AbstractEntidade<CnpOcorrencia> {

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private LocalDate dataOcorrencia;
	private TipoOcorrencia TipoOcorrencia;
	private String descricao;
	
	private CnpTitulo cnpTitulo;

	@Id
	@Column(name = "ID_CNP_OCORRENCIA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	
	@Column(name = "DATA_OCORRENCIA")
	public LocalDate getDataOcorrencia() {
		return dataOcorrencia;
	}
	
	@Column(name = "TIPO_OCORRENCIA", length=255)
	@Enumerated(EnumType.STRING)
	public TipoOcorrencia getTipoOcorrencia() {
		return TipoOcorrencia;
	}

	@Column(name = "DESCRICAO", length=255)
	public String getDescricao() {
		return descricao;
	}
	
	public void setDataOcorrencia(LocalDate dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}
	
	public void setTipoOcorrencia(TipoOcorrencia tipoOcorrencia) {
		TipoOcorrencia = tipoOcorrencia;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int compareTo(CnpOcorrencia entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@ManyToOne
	@JoinColumn(name="CNP_TITULO_ID")
	public CnpTitulo getCnpTitulo() {
		return cnpTitulo;
	}

	public void setCnpTitulo(CnpTitulo cnpTitulo) {
		this.cnpTitulo = cnpTitulo;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}
