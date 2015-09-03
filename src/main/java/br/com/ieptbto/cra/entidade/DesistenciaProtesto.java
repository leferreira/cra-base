package br.com.ieptbto.cra.entidade;

import java.util.List;

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
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_DESISTENCIA_PROTESTO")
@org.hibernate.annotations.Table(appliesTo = "TB_DESISTENCIA_PROTESTO")
public class DesistenciaProtesto extends AbstractEntidade<DesistenciaProtesto> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private CabecalhoCartorio cabecalhoCartorio;
	private List<PedidoDesistenciaCancelamento> desistencias;
	private RodapeCartorio rodapeCartorio;
	private RemessaDesistenciaProtesto remessaDesistenciaProtesto;
	private Boolean download;

	@Id
	@Column(name = "ID_DESISTENCIA_PROTESTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name = "CABECALHO_ID")
	public CabecalhoCartorio getCabecalhoCartorio() {
		return cabecalhoCartorio;
	}

	@OneToMany(mappedBy = "desistenciaProtesto", fetch = FetchType.LAZY)
	public List<PedidoDesistenciaCancelamento> getDesistencias() {
		return desistencias;
	}

	@OneToOne
	@JoinColumn(name = "RODAPE_ID")
	public RodapeCartorio getRodapeCartorio() {
		return rodapeCartorio;
	}

	@ManyToOne
	@JoinColumn(name = "REMESSA_DESISTENCIA_PROTESTO_ID")
	public RemessaDesistenciaProtesto getRemessaDesistenciaProtesto() {
		return remessaDesistenciaProtesto;
	}

	@Column(name = "DOWNLOAD_REALIZADO", columnDefinition = "boolean")
	public Boolean getDownload() {
		return download;
	}

	public void setDownload(Boolean download) {
		this.download = download;
	}

	public void setRemessaDesistenciaProtesto(RemessaDesistenciaProtesto remessaDesistenciaProtesto) {
		this.remessaDesistenciaProtesto = remessaDesistenciaProtesto;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCabecalhoCartorio(CabecalhoCartorio cabecalhoCartorio) {
		this.cabecalhoCartorio = cabecalhoCartorio;
	}

	public void setDesistencias(List<PedidoDesistenciaCancelamento> desistencias) {
		this.desistencias = desistencias;
	}

	public void setRodapeCartorio(RodapeCartorio rodapeCartorio) {
		this.rodapeCartorio = rodapeCartorio;
	}

	@Override
	public int compareTo(DesistenciaProtesto entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
