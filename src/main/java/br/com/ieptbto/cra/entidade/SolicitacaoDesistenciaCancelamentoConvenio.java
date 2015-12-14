package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.StatusSolicitacao;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_SOLICITACAO_DESISTENCIA_CANCELAMENTO_CONVENIO")
@org.hibernate.annotations.Table(appliesTo = "TB_SOLICITACAO_DESISTENCIA_CANCELAMENTO_CONVENIO")
public class SolicitacaoDesistenciaCancelamentoConvenio extends AbstractEntidade<SolicitacaoDesistenciaCancelamentoConvenio> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private TituloFiliado tituloFiliado;
	private LocalDate dataSolicitacao;
	private StatusSolicitacao statusSolicitacao;
 
	@Override
	@Id
	@Column(name = "ID_SOLICITACAO_DESISTENCIA_CANCELAMENTO_CONVENIO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name="titulo_filiado_id")
	public TituloFiliado getTituloFiliado() {
		return tituloFiliado;
	}
	
	@Column(name = "DATA_SOLICITACAO")
	public LocalDate getDataSolicitacao() {
		return dataSolicitacao;
	}
	
	@Column(name = "STATUS_SOLICITACAO")
	@Enumerated(EnumType.STRING)
	public StatusSolicitacao getStatusSolicitacao() {
		return statusSolicitacao;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTituloFiliado(TituloFiliado tituloFiliado) {
		this.tituloFiliado = tituloFiliado;
	}
	
	public void setDataSolicitacao(LocalDate dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}
	
	public void setStatusSolicitacao(StatusSolicitacao statusSolicitacao) {
		this.statusSolicitacao = statusSolicitacao;
	}
	
	@Override
	public int compareTo(SolicitacaoDesistenciaCancelamentoConvenio entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
