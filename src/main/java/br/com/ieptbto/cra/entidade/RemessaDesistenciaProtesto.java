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

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.bytecode.internal.javassist.FieldHandled;
import org.hibernate.bytecode.internal.javassist.FieldHandler;
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
public class RemessaDesistenciaProtesto extends AbstractEntidade<RemessaDesistenciaProtesto> implements FieldHandled {

	private int id;
	private CabecalhoArquivo cabecalho;
	private List<DesistenciaProtesto> desistenciaProtesto;
	private RodapeArquivo rodape;
	private Arquivo arquivo;
	private FieldHandler handler;

	@Override
	@Id
	@Column(name = "ID_REMESSA_DESISTENCIA_PROTESTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name = "ARQUIVO_ID")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public Arquivo getArquivo() {
		if (this.handler != null) {
			return (Arquivo) this.handler.readObject(this, "arquivo", arquivo);
		}
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
		if (this.handler != null) {
			this.arquivo = (Arquivo) this.handler.writeObject(this, "arquivo", this.arquivo, arquivo);
		}
		this.arquivo = arquivo;
	}

	@Override
	public int compareTo(RemessaDesistenciaProtesto entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFieldHandler(FieldHandler handler) {
		this.handler = handler;

	}

	@Override
	public FieldHandler getFieldHandler() {
		return this.handler;
	}

}
