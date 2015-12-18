package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_REMESSA_CANCELAMENTO_PROTESTO")
@org.hibernate.annotations.Table(appliesTo = "TB_REMESSA_CANCELAMENTO_PROTESTO")
public class RemessaCancelamentoProtesto extends AbstractEntidade<RemessaCancelamentoProtesto> {

	private int id;
	private CabecalhoArquivo cabecalho;
	private List<CancelamentoProtesto> cancelamentoProtesto;
	private RodapeArquivo rodape;
	private Arquivo arquivo;

	@Override
	@Id
	@Column(name = "ID_REMESSA_CANCELAMENTO_PROTESTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name = "ARQUIVO_ID")
	public Arquivo getArquivo() {
		return arquivo; 
	}

	@OneToOne
	@JoinColumn(name = "CABECALHO_CANCELAMENTO_PROTESTO_ID")
	public CabecalhoArquivo getCabecalho() {
		return cabecalho;
	}

	@OneToMany(mappedBy = "remessaCancelamentoProtesto", fetch = FetchType.LAZY)
	public List<CancelamentoProtesto> getCancelamentoProtesto() {
		return cancelamentoProtesto;
	}
	
	@OneToOne
	@JoinColumn(name = "RODAPE_CANCELAMENTO_PROTESTO_ID")
	public RodapeArquivo getRodape() {
		return rodape;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCabecalho(CabecalhoArquivo cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setCancelamentoProtesto(List<CancelamentoProtesto> cancelamentoProtesto) {
		this.cancelamentoProtesto = cancelamentoProtesto;
	}

	public void setRodape(RodapeArquivo rodape) {
		this.rodape = rodape;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	@Override
	public int compareTo(RemessaCancelamentoProtesto entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
