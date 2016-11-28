package br.com.ieptbto.cra.entidade;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.bytecode.internal.javassist.FieldHandled;
import org.hibernate.bytecode.internal.javassist.FieldHandler;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_ARQUIVO")
@org.hibernate.annotations.Table(appliesTo = "TB_ARQUIVO")
public class Arquivo extends AbstractEntidade<Arquivo> implements FieldHandled {

	/****/
	private static final long serialVersionUID = 8563214L;
	private int id;
	private String nomeArquivo;
	private String comentario;
	private String path;
	private Date dataRecebimento;
	private LocalDate dataEnvio;
	private LocalTime horaEnvio;
	private List<Remessa> remessas;
	private RemessaDesistenciaProtesto remessaDesistenciaProtesto;
	private RemessaCancelamentoProtesto remessaCancelamentoProtesto;
	private RemessaAutorizacaoCancelamento remessaAutorizacao;
	private Instituicao instituicaoRecebe;
	private Instituicao instituicaoEnvio;
	private TipoArquivo tipoArquivo;
	private Usuario usuarioEnvio;
	private StatusArquivo statusArquivo;
	private List<Remessa> remessaBanco;
	private FieldHandler handler;

	@Id
	@Column(name = "ID_ARQUIVO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_ARQUIVO", length = 13)
	public String getNomeArquivo() {
		return nomeArquivo;
	}

	@Column(name = "COMENTARIO", columnDefinition = "TEXT")
	public String getComentario() {
		return comentario;
	}

	@Column(name = "PATH", length = 255)
	public String getPath() {
		return path;
	}

	@Column(name = "DATA_RECEBIMENTO")
	@Temporal(TemporalType.DATE)
	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	@Column(name = "DATA_ENVIO")
	public LocalDate getDataEnvio() {
		return dataEnvio;
	}

	@OneToMany(mappedBy = "arquivo", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public List<Remessa> getRemessas() {
		return remessas;
	}

	@OneToMany(mappedBy = "arquivoGeradoProBanco", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public List<Remessa> getRemessaBanco() {
		return remessaBanco;
	}

	@OneToOne(mappedBy = "arquivo", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public RemessaDesistenciaProtesto getRemessaDesistenciaProtesto() {
		if (this.handler != null) {
			return (RemessaDesistenciaProtesto) this.handler.readObject(this, "remessaDesistenciaProtesto", remessaDesistenciaProtesto);
		}
		return remessaDesistenciaProtesto;
	}

	@OneToOne(mappedBy = "arquivo", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public RemessaCancelamentoProtesto getRemessaCancelamentoProtesto() {
		if (this.handler != null) {
			return (RemessaCancelamentoProtesto) this.handler.readObject(this, "remessaCancelamentoProtesto", remessaCancelamentoProtesto);
		}
		return remessaCancelamentoProtesto;
	}

	@OneToOne(mappedBy = "arquivo", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public RemessaAutorizacaoCancelamento getRemessaAutorizacao() {
		if (this.handler != null) {
			return (RemessaAutorizacaoCancelamento) this.handler.readObject(this, "remessaAutorizacao", remessaAutorizacao);
		}
		return remessaAutorizacao;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "INSTITUICAO_ENVIO_ID")
	public Instituicao getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "INSTITUICAO_RECEBE_ID")
	public Instituicao getInstituicaoRecebe() {
		return instituicaoRecebe;
	}

	@OneToOne
	@JoinColumn(name = "TIPO_ARQUIVO_ID")
	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	@OneToOne
	@JoinColumn(name = "USUARIO_ENVIO_ID")
	public Usuario getUsuarioEnvio() {
		return usuarioEnvio;
	}

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "STATUS_ARQUIVO_ID", nullable = false)
	public StatusArquivo getStatusArquivo() {
		return statusArquivo;
	}

	@Column(name = "HORA_ENVIO", nullable = true)
	public LocalTime getHoraEnvio() {
		return horaEnvio;
	}

	public void setHoraEnvio(LocalTime horaEnvio) {
		this.horaEnvio = horaEnvio;
	}

	public void setRemessaBanco(List<Remessa> remessaBanco) {
		this.remessaBanco = remessaBanco;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setDataEnvio(LocalDate dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setInstituicaoRecebe(Instituicao instituicaoRecebe) {
		this.instituicaoRecebe = instituicaoRecebe;
	}

	public void setUsuarioEnvio(Usuario usuarioEnvio) {
		this.usuarioEnvio = usuarioEnvio;
	}

	public void setStatusArquivo(StatusArquivo statusArquivo) {
		this.statusArquivo = statusArquivo;
	}

	public void setRemessas(List<Remessa> remessas) {
		this.remessas = remessas;
	}

	public void setRemessaDesistenciaProtesto(RemessaDesistenciaProtesto remessaDesistenciaProtesto) {
		if (this.handler != null) {
			this.remessaDesistenciaProtesto = (RemessaDesistenciaProtesto) this.handler.writeObject(this, "remessaDesistenciaProtesto",
					this.remessaDesistenciaProtesto, remessaDesistenciaProtesto);
		}

		this.remessaDesistenciaProtesto = remessaDesistenciaProtesto;
	}

	public void setInstituicaoEnvio(Instituicao instituicaoEnvio) {
		this.instituicaoEnvio = instituicaoEnvio;
	}

	public void setRemessaCancelamentoProtesto(RemessaCancelamentoProtesto remessaCancelamentoProtesto) {
		if (this.handler != null) {
			this.remessaCancelamentoProtesto = (RemessaCancelamentoProtesto) this.handler.writeObject(this, "remessaCancelamentoProtesto",
					this.remessaCancelamentoProtesto, remessaCancelamentoProtesto);
		}
		this.remessaCancelamentoProtesto = remessaCancelamentoProtesto;
	}

	public void setRemessaAutorizacao(RemessaAutorizacaoCancelamento remessaAutorizacao) {
		if (this.handler != null) {
			this.remessaAutorizacao = (RemessaAutorizacaoCancelamento) this.handler.writeObject(this, "remessaAutorizacao",
					this.remessaAutorizacao, remessaAutorizacao);
		}
		this.remessaAutorizacao = remessaAutorizacao;
	}

	@Override
	public int compareTo(Arquivo entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		compareToBuilder.append(this.getNomeArquivo(), entidade.getNomeArquivo());
		return compareToBuilder.toComparison();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
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
