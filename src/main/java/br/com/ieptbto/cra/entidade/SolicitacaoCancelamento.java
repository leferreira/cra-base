package br.com.ieptbto.cra.entidade;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.StatusSolicitacaoCancelamento;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_SOLICITACAO_DESISTENCIA_CANCELAMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_SOLICITACAO_DESISTENCIA_CANCELAMENTO")
public class SolicitacaoCancelamento extends AbstractEntidade<SolicitacaoCancelamento> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private Date dataSolicitacao;
	private LocalTime horaSolicitacao;
	private StatusSolicitacaoCancelamento statusSolicitacaoCancelamento;
	private CodigoIrregularidade codigoIrregularidadeCancelamento;
	private Usuario usuario;
	private TituloRemessa tituloRemessa;

	@Override
	@Id
	@Column(name = "ID_SOLICITACAO_CANCELAMENTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_SOLICITACAO_CANCELAMENTO", length = 50)
	public StatusSolicitacaoCancelamento getStatusSolicitacaoCancelamento() {
		return statusSolicitacaoCancelamento;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "CODIGO_IRREGULARIDADE_CANCELAMENTO", length = 50)
	public CodigoIrregularidade getCodigoIrregularidadeCancelamento() {
		return codigoIrregularidadeCancelamento;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_SOLICITACAO")
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	@Column(name = "HORA_SOLICITACAO")
	public LocalTime getHoraSolicitacao() {
		if (horaSolicitacao == null) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
			DateTime dt = formatter.parseDateTime("01/01/2000 00:00:00");
			horaSolicitacao = new LocalTime(dt);
		}
		return horaSolicitacao;
	}

	@OneToOne
	@JoinColumn(name = "USUARIO_ID")
	public Usuario getUsuario() {
		return usuario;
	}

	@OneToOne
	@JoinColumn(name = "TITULO_REMESSA_ID")
	public TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public void setHoraSolicitacao(LocalTime horaSolicitacao) {
		this.horaSolicitacao = horaSolicitacao;
	}

	public void setStatusSolicitacaoCancelamento(StatusSolicitacaoCancelamento statusSolicitacaoCancelamento) {
		this.statusSolicitacaoCancelamento = statusSolicitacaoCancelamento;
	}

	public void setCodigoIrregularidadeCancelamento(CodigoIrregularidade codigoIrregularidadeCancelamento) {
		this.codigoIrregularidadeCancelamento = codigoIrregularidadeCancelamento;
	}

	public void setTituloRemessa(TituloRemessa tituloRemessa) {
		this.tituloRemessa = tituloRemessa;
	}

	@Override
	public int compareTo(SolicitacaoCancelamento entidade) {
		return 0;
	}
}
