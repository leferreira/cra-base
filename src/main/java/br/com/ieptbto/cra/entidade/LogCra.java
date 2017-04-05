package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoLog;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Aráujo
 *
 */
@Entity
@Audited
@Table(name = "TB_LOG_CRA")
@org.hibernate.annotations.Table(appliesTo = "TB_LOG_CRA")
public class LogCra extends AbstractEntidade<LogCra> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private CraAcao acao;
	private String descricao;
	private Exception excecao;
	private TipoLog tipoLog;
	private String usuario;
	private String instituicao;
	private LocalDate data;
	private LocalTime hora;
	private String relatorio;

	@Id
	@Column(name = "ID_REGISTRO_ACAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "ACAO", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	public CraAcao getAcao() {
		return acao;
	}

	@Column(name = "DESCRICAO", nullable = false, columnDefinition = "text")
	public String getDescricao() {
		if (descricao == null) {
			this.descricao = StringUtils.EMPTY;
		}
		return descricao;
	}

	@Column(name = "EXCECAO")
	public Exception getExcecao() {
		return excecao;
	}

    @Column(name="RELATORIO", columnDefinition = "text")
    public String getRelatorio() {
        return relatorio;
    }

	@Column(name = "TIPO_LOG", length = 150)
	@Enumerated(EnumType.STRING)
	public TipoLog getTipoLog() {
		if (tipoLog == null) {
			this.tipoLog = TipoLog.OCORRENCIA_ERRO;
		}
		return tipoLog;
	}

	@Column(name = "INSTITUICAO", length = 45)
	public String getInstituicao() {
		if (instituicao == null) {
			instituicao = TipoInstituicaoCRA.CRA.getLabel();
		}
		return instituicao;
	}

	@Column(name = "USUARIO", length = 45)
	public String getUsuario() {
		return usuario;
	}

	@Column(name = "DATA")
	public LocalDate getData() {
		return data;
	}

	@Column(name = "HORA")
	public LocalTime getHora() {
		return hora;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAcao(CraAcao acao) {
		this.acao = acao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setExcecao(Exception excecao) {
		this.excecao = excecao;
	}

	public void setTipoLog(TipoLog tipoL) {
		this.tipoLog = tipoL;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public void setHora(LocalTime hora) {
		this.hora = hora;
	}
	
    public void setRelatorio(String relatorio) {
		this.relatorio = relatorio;
	}

	@Override
	public int compareTo(LogCra entidade) {
		return 0;
	}

	@Override
	public boolean equals(Object user) {
		if (getId() != 0 && user instanceof LogCra) {
			return getId() == ((LogCra) user).getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (getId() == 0) {
			return 0;
		}
		return getId();
	}

	@Override
	public String toString() {
		return "[ " + DataUtil.localDateToString(data) + "  " + DataUtil.localTimeToString("HH:mm:ss", hora) + " ] : [" + tipoLog.getLabel() + "] " + descricao;
	}

	@Transient
	public String getNomeArquivoRelatorio(){
		return DataUtil.localDateToString(data).replace("/", "-") + "_" + DataUtil.localTimeToString("HH:mm:ss", hora) 
			+ "_" + getAcao().toString() + "_" + getInstituicao().toUpperCase(); 
	}
}
