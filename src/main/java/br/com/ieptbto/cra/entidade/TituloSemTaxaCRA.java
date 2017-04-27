package br.com.ieptbto.cra.entidade;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Table(name = "TB_TITULO_SEM_TAXA_CRA")
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO_SEM_TAXA_CRA")
public class TituloSemTaxaCRA extends AbstractEntidade<TituloSemTaxaCRA> {

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private String codigoIBGE;
	private String protocolo;
	private String agenciaCodigoCedente;
	private boolean corrigido;
	private Date dataCorrecao;

	@Id
	@Column(name = "ID_TITULO_SEM_TAXA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "CODIGO_IBGE")
	public String getCodigoIBGE() {
		return codigoIBGE;
	}

	public void setCodigoIBGE(String codigoIBGE) {
		this.codigoIBGE = codigoIBGE;
	}

	@Column(name = "PROTOCOLO")
	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	@Column(name = "AGENCIA_CODIGO_CEDENTE")
	public String getAgenciaCodigoCedente() {
		return agenciaCodigoCedente;
	}
	
	public void setAgenciaCodigoCedente(String agenciaCodigoCedente) {
		this.agenciaCodigoCedente = agenciaCodigoCedente;
	}

	@Override
	public int compareTo(TituloSemTaxaCRA entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Column(name = "CORRIGIDO")
	public boolean isCorrigido() {
		return corrigido;
	}

	@Column(name = "DATA_CORRECAO")
	public Date getDataCorrecao() {
		return dataCorrecao;
	}

	public void setCorrigido(boolean corrigido) {
		this.corrigido = corrigido;
	}

	public void setDataCorrecao(Date dataCorrecao) {
		this.dataCorrecao = dataCorrecao;
	}
}
