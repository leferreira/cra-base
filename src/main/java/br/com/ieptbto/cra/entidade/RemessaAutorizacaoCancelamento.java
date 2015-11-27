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
@Table(name = "TB_REMESSA_AUTORIZACAO_CANCELAMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_REMESSA_AUTORIZACAO_CANCELAMENTO")
public class RemessaAutorizacaoCancelamento extends AbstractEntidade<RemessaAutorizacaoCancelamento> {

	private int id;
	private CabecalhoArquivo cabecalho;
	private List<AutorizacaoCancelamento> autorizacaoCancelamento;
	private RodapeArquivo rodape;
	private Arquivo arquivo;

	@Override
	@Id
	@Column(name = "ID_REMESSA_AUTORIZACAO_CANCELAMENTO", columnDefinition = "serial")
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
	@JoinColumn(name = "CABECALHO_AUTORIZACAO_CANCELAMENTO_ID")
	public CabecalhoArquivo getCabecalho() {
		return cabecalho;
	}

	@OneToMany(mappedBy = "remessaAutorizacaoCancelamento", fetch = FetchType.LAZY)
	public List<AutorizacaoCancelamento> getAutorizacaoCancelamento() {
		return autorizacaoCancelamento;
	}
	
	@OneToOne
	@JoinColumn(name = "RODAPE_AUTORIZACAO_CANCELAMENTO_ID")
	public RodapeArquivo getRodape() {
		return rodape;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCabecalho(CabecalhoArquivo cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setAutorizacaoCancelamento(List<AutorizacaoCancelamento> autorizacaoCancelamento) {
		this.autorizacaoCancelamento = autorizacaoCancelamento;
	}

	public void setRodape(RodapeArquivo rodape) {
		this.rodape = rodape;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	@Override
	public int compareTo(RemessaAutorizacaoCancelamento entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
