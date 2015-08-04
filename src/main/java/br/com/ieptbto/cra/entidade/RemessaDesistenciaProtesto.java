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
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_REMESSA_DESISTENCIA_PROTESTO")
@org.hibernate.annotations.Table(appliesTo = "TB_REMESSA_DESISTENCIA_PROTESTO")
public class RemessaDesistenciaProtesto extends AbstractEntidade<RemessaDesistenciaProtesto> {

	private int id;
	private CabecalhoArquivo cabecalho;
	private List<DesistenciaProtesto> desistenciaProtesto;
	private RodapeArquivo rodape;
	private Arquivo arquivo;

	@Override
	@Id
	@Column(name = "ID_REMESSA_DESISTENCIA_PROTESTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "ARQUIVO_ID")
	public Arquivo getArquivo() {
		return arquivo;
	}

	@OneToOne
	@JoinColumn(name = "CABECALHO_DESISTENCIA_PROTESTO_ID")
	public CabecalhoArquivo getCabecalho() {
		return cabecalho;
	}

	@OneToMany(mappedBy = "remessaDesistenciaProtesto", fetch = FetchType.LAZY)
	public List<DesistenciaProtesto> getDesistenciaProtesto() {
		return desistenciaProtesto;
	}

	@OneToOne
	@JoinColumn(name = "RODAPE_DESISTENCIA_PROTESTO_ID")
	public RodapeArquivo getRodape() {
		return rodape;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCabecalho(CabecalhoArquivo cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setDesistenciaProtesto(List<DesistenciaProtesto> desistenciaProtesto) {
		this.desistenciaProtesto = desistenciaProtesto;
	}

	public void setRodape(RodapeArquivo rodape) {
		this.rodape = rodape;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	@Override
	public int compareTo(RemessaDesistenciaProtesto entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
