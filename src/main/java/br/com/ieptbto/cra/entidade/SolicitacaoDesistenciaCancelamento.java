package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.TipoSolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_SOLICITACAO_DESISTENCIA_CANCELAMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_SOLICITACAO_DESISTENCIA_CANCELAMENTO")
public class SolicitacaoDesistenciaCancelamento extends AbstractEntidade<SolicitacaoDesistenciaCancelamento> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private Date dataSolicitacao;
	private LocalTime horaSolicitacao;
	private TipoSolicitacaoDesistenciaCancelamento tipoSolicitacao;
	private CodigoIrregularidade codigoIrregularidade;
	private Usuario usuario;
	private TituloRemessa tituloRemessa;
	private Boolean statusLiberacao;
	private byte[] documentoAnexo;

	@Override
	@Id
	@Column(name = "ID_SOLICITACAO_DESISTENCIA_CANCELAMENTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_SOLICITACAO", length = 50)
	public TipoSolicitacaoDesistenciaCancelamento getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "CODIGO_IRREGULARIDADE", length = 50)
	public CodigoIrregularidade getCodigoIrregularidade() {
		return codigoIrregularidade;
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

	@Column(name = "STATUS_LIBERACAO")
	public Boolean getStatusLiberacao() {
		return statusLiberacao;
	}

	@Column(name = "DOCUMENTO_ANEXO")
	public byte[] getDocumentoAnexo() {
		return documentoAnexo;
	}

	@Transient
	public String getDocumentoAnexoAsString() {
		return new String(this.documentoAnexo);
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

	public void setTipoSolicitacao(TipoSolicitacaoDesistenciaCancelamento tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public void setCodigoIrregularidade(CodigoIrregularidade codigoIrregularidade) {
		this.codigoIrregularidade = codigoIrregularidade;
	}

	public void setTituloRemessa(TituloRemessa tituloRemessa) {
		this.tituloRemessa = tituloRemessa;
	}

	public void setStatusLiberacao(Boolean statusLiberacao) {
		this.statusLiberacao = statusLiberacao;
	}

	public void setDocumentoAnexo(byte[] documentoAnexo) {
		this.documentoAnexo = documentoAnexo;
	}

	@Override
	public int compareTo(SolicitacaoDesistenciaCancelamento entidade) {
		return 0;
	}
}
