package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.StatusDownload;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

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
	private StatusDownload statusDownload;

	@Override
	@Id
	@Column(name = "ID_STATUS_ARQUIVO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	@Column(name = "STATUS_DOWNLOAD")
	@Enumerated(EnumType.STRING)
	public StatusDownload getStatusDownload() {
		return statusDownload;
	}

	@Column(name = "DATA")
	public LocalDateTime getData() {
		return data;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public void setStatusDownload(StatusDownload statusDownload) {
		this.statusDownload = statusDownload;
	}

	@Override
	public int compareTo(StatusArquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
