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

import br.com.ieptbto.cra.enumeration.TipoAcao;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Aráujo
 *
 */
@Entity
@Audited
@Table(name = "TB_REGISTRO_ACAO")
@org.hibernate.annotations.Table(appliesTo = "TB_REGISTRO_ACAO")
public class RegistroAcao extends AbstractEntidade<RegistroAcao> {

    /***/
    private static final long serialVersionUID = 1L;
    private int id;
    private String acao;
    private String descricao;
    private Exception excecao;
    private TipoAcao tipoAcao;
    private Usuario usuario;
    private Date data;

    public RegistroAcao(String acao, String descricao, Exception exception, TipoAcao tipoAcao, Usuario usuario) {
	this.acao = acao;
	this.descricao = descricao;
	this.excecao = exception;
	this.tipoAcao = tipoAcao;
	this.usuario = usuario;
	this.data = new Date();
    }

    public RegistroAcao(String acao, String descricao, TipoAcao tipoAcao, Usuario usuario) {
	this.acao = acao;
	this.acao = acao;
	this.descricao = descricao;
	this.excecao = new InfraException(descricao);
	this.tipoAcao = tipoAcao;
	this.usuario = usuario;
	this.data = new Date();
    }

    @Override
    @Id
    @Column(name = "ID_REGISTRO_ACAO", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
	return id;
    }

    @Column(name = "ACAO", nullable = false, length = 20)
    public String getAcao() {
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

    @Column(name = "TIPO_ACAO", length = 150)
    @Enumerated(EnumType.STRING)
    public TipoAcao getTipoAcao() {
	return tipoAcao;
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

    public void setAcao(String acao) {
	this.acao = acao;
    }

    public void setDescricao(String descricao) {
	this.descricao = descricao;
    }

    public void setExcecao(Exception excecao) {
	this.excecao = excecao;
    }

    public void setTipoAcao(TipoAcao tipoAcao) {
	this.tipoAcao = tipoAcao;
    }

    public void setUsuario(Usuario usuario) {
	this.usuario = usuario;
    }

    public void setData(Date data) {
	this.data = data;
    }

    @Override
    public int compareTo(RegistroAcao entidade) {
	return 0;
    }

    @Override
    public boolean equals(Object user) {
	if (getId() != 0 && user instanceof RegistroAcao) {
	    return getId() == ((RegistroAcao) user).getId();
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
}
