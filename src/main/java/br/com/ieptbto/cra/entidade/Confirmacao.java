package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.conversor.arquivo.ConfirmacaoConversor;
import br.com.ieptbto.cra.entidade.vo.TituloVO;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_CONFIRMACAO")
@org.hibernate.annotations.Table(appliesTo = "TB_CONFIRMACAO")
public class Confirmacao extends Titulo<Confirmacao> {

	private String identificacaoTransacaoRemetente;
	private String identificacaoTransacaoDestinatario;
	private String identificacaoTransacaoTipo;
	private int id;

	private TituloRemessa tituloRemessa;

	@Id
	@Column(name = "ID_CONFIRMACAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public int getId() {
		return this.id;
	}

	@Column(name = "IDENTIFICAO_TRANSACAO_REMENTE", length = 3)
	public String getIdentificacaoTransacaoRemetente() {
		return identificacaoTransacaoRemetente;
	}

	@Column(name = "IDENTIFICAO_TRANSACAO_DESTINATARIO", length = 3)
	public String getIdentificacaoTransacaoDestinatario() {
		return identificacaoTransacaoDestinatario;
	}

	@Column(name = "IDENTIFICAO_TRANSACAO_TIPO", length = 3)
	public String getIdentificacaoTransacaoTipo() {
		return identificacaoTransacaoTipo;
	}

	@OneToOne
	@JoinColumn(name = "TITULO_ID")
	public TituloRemessa getTitulo() {
		return tituloRemessa;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.tituloRemessa = titulo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIdentificacaoTransacaoDestinatario(String identificacaoTransacaoDestinatario) {
		this.identificacaoTransacaoDestinatario = identificacaoTransacaoDestinatario;
	}

	public void setIdentificacaoTransacaoRemetente(String identificacaoTransacaoRemetente) {
		this.identificacaoTransacaoRemetente = identificacaoTransacaoRemetente;
	}

	public void setIdentificacaoTransacaoTipo(String identificacaoTransacaoTipo) {
		this.identificacaoTransacaoTipo = identificacaoTransacaoTipo;
	}

	public static Confirmacao parseTituloVO(TituloVO tituloVO) {
		Confirmacao titulo = new ConfirmacaoConversor().converter(Confirmacao.class, tituloVO);
		return titulo;
	}

	@Override
	public int compareTo(Confirmacao entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		return compareToBuilder.toComparison();
	}

}
