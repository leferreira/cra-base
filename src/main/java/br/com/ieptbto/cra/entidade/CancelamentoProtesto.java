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
@Table(name = "TB_CANCELAMENTO_PROTESTO")
@org.hibernate.annotations.Table(appliesTo = "TB_CANCELAMENTO_PROTESTO")
public class CancelamentoProtesto extends AbstractEntidade<CancelamentoProtesto> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private CabecalhoCartorio cabecalhoCartorio;
	private List<PedidoCancelamento> cancelamentos;
	private RodapeCartorio rodapeCartorio;
	private RemessaCancelamentoProtesto remessaCancelamentoProtesto;
	private Boolean download;

	@Id
	@Column(name = "ID_CANCELAMENTO_PROTESTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name = "CABECALHO_ID")
	public CabecalhoCartorio getCabecalhoCartorio() {
		return cabecalhoCartorio;
	}

	@OneToMany(mappedBy = "cancelamentoProtesto", fetch = FetchType.LAZY)
	public List<PedidoCancelamento> getCancelamentos() {
		return cancelamentos;
	}

	@OneToOne
	@JoinColumn(name = "RODAPE_ID")
	public RodapeCartorio getRodapeCartorio() {
		return rodapeCartorio;
	}

	@ManyToOne
	@JoinColumn(name = "REMESSA_CANCELAMENTO_PROTESTO_ID")
	public RemessaCancelamentoProtesto getRemessaCancelamentoProtesto() {
		return remessaCancelamentoProtesto;
	}

	@Column(name = "DOWNLOAD_REALIZADO", columnDefinition = "boolean")
	public Boolean getDownload() {
		return download;
	}

	public void setDownload(Boolean download) {
		this.download = download;
	}

	public void setRemessaCancelamentoProtesto(RemessaCancelamentoProtesto remessaCancelamentoProtesto) {
		this.remessaCancelamentoProtesto = remessaCancelamentoProtesto;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCabecalhoCartorio(CabecalhoCartorio cabecalhoCartorio) {
		this.cabecalhoCartorio = cabecalhoCartorio;
	}

	public void setCancelamentos(List<PedidoCancelamento> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public void setRodapeCartorio(RodapeCartorio rodapeCartorio) {
		this.rodapeCartorio = rodapeCartorio;
	}

	@Override
	public int compareTo(CancelamentoProtesto entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
