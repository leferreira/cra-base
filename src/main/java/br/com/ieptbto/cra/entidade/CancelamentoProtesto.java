package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Entity;
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
	private Remessa remessa;
	private CabecalhoArquivo cabecalhoArquivo;
	private CabecalhoCartorio cabecalhoCartorio;
	private List<PedidoDesistenciaCancelamento> cancelamentos;
	private RodapeCartorio rodapeCartorio;
	private RodapeArquivo rodapeArquivo;

	public int getId() {
		return id;
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public CabecalhoArquivo getCabecalhoArquivo() {
		return cabecalhoArquivo;
	}

	public CabecalhoCartorio getCabecalhoCartorio() {
		return cabecalhoCartorio;
	}

	public List<PedidoDesistenciaCancelamento> getCancelamentos() {
		return cancelamentos;
	}

	public RodapeCartorio getRodapeCartorio() {
		return rodapeCartorio;
	}

	public RodapeArquivo getRodapeArquivo() {
		return rodapeArquivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setCabecalhoArquivo(CabecalhoArquivo cabecalhoArquivo) {
		this.cabecalhoArquivo = cabecalhoArquivo;
	}

	public void setCabecalhoCartorio(CabecalhoCartorio cabecalhoCartorio) {
		this.cabecalhoCartorio = cabecalhoCartorio;
	}

	public void setDesistencias(List<PedidoDesistenciaCancelamento> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public void setRodapeCartorio(RodapeCartorio rodapeCartorio) {
		this.rodapeCartorio = rodapeCartorio;
	}

	public void setRodapeArquivo(RodapeArquivo rodapeArquivo) {
		this.rodapeArquivo = rodapeArquivo;
	}

	@Override
	public int compareTo(CancelamentoProtesto entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
