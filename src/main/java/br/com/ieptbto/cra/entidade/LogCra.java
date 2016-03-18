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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.enumeration.TipoAcaoLog;
import br.com.ieptbto.cra.enumeration.TipoLog;

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
    private TipoAcaoLog acao;
    private String descricao;
    private Exception excecao;
    private TipoLog tipoLog;
    private Usuario usuario;
    private Date data;

    @Id
    @Column(name = "ID_REGISTRO_ACAO", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
	return id;
    }

    @Column(name = "ACAO", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    public TipoAcaoLog getAcao() {
	return acao;
    }

    @Column(name = "DESCRICAO", nullable = false, length = 150)
    public String getDescricao() {
	return descricao;
    }

    @Column(name = "EXCECAO")
    public Exception getExcecao() {
	return excecao;
    }

    @Column(name = "TIPO_LOG", length = 150)
    @Enumerated(EnumType.STRING)
    public TipoLog getTipoLog() {
	return tipoLog;
    }

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID")
    public Usuario getUsuario() {
	return usuario;
    }

    @Column(name = "DATA")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getData() {
	return data;
    }

    public void setId(int id) {
	this.id = id;
    }

    public void setAcao(TipoAcaoLog acao) {
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

    public void setUsuario(Usuario usuario) {
	this.usuario = usuario;
    }

    public void setData(Date data) {
	this.data = data;
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
	return "CraLog Registrado: [" + tipoLog.getLabel() + "] " + descricao;
    }
}
