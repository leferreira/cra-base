package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDateTime;

import br.com.ieptbto.cra.enumeration.SituacaoArquivo;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_STATUS_ARQUIVO")
@org.hibernate.annotations.Table(appliesTo = "TB_STATUS_ARQUIVO")
public class StatusArquivo extends AbstractEntidade<StatusArquivo> {

	/****/
	private static final long serialVersionUID = 853651236L;

	private int id;
	private LocalDateTime data;
//	private String status;
	private SituacaoArquivo situacaoArquivo;

	@Override
	@Id
	@Column(name = "ID_STATUS_ARQUIVO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	 @Column(name = "SITUACAO_ARQUIVO")
	 @Enumerated(EnumType.STRING)
	 public SituacaoArquivo getSituacaoArquivo() {
		 return situacaoArquivo;
	 }

	@Column(name = "DATA")
	public LocalDateTime getData() {
		return data;
	}

//	@Column(name = "STATUS")
//	public String getStatus() {
//		return status;
//	}

	public void setId(int id) {
		this.id = id;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

//	public void setStatus(String status) {
//		this.status = status;
//	}

	 public void setSituacaoArquivo(SituacaoArquivo situacaoArquivo) {
		 this.situacaoArquivo = situacaoArquivo;
	 }

	@Override
	public int compareTo(StatusArquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
