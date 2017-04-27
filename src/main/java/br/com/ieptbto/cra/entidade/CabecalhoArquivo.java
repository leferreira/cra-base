package br.com.ieptbto.cra.entidade;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import javax.persistence.*;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_CABECALHO_ARQUIVO_DESISTENCIA_CANCELAMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_CABECALHO_ARQUIVO_DESISTENCIA_CANCELAMENTO")
public class CabecalhoArquivo extends CabecalhoDesistenciaCancelamento<CabecalhoArquivo> {

	/****/
	private static final long serialVersionUID = 1L;

	private int id;
	private String codigoApresentante;
	private String nomeApresentante;
	private LocalDate dataMovimento;
	private int quantidadeRegistro;

	@Override
	@Id
	@Column(name = "ID_CABECALHO_ARQUIVO_DP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "CODIGO_APRESENTANTE", length = 3)
	public String getCodigoApresentante() {
		return codigoApresentante;
	}

	@Column(name = "NOME_APRESENTANTE", length = 45)
	public String getNomeApresentante() {
		return nomeApresentante;
	}

	@Column(name = "DATA_MOVIMENTO")
	public LocalDate getDataMovimento() {
		return dataMovimento;
	}

	@Column(name = "QUANTIDADE_REGISTRO")
	public int getQuantidadeRegistro() {
		return quantidadeRegistro;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCodigoApresentante(String codigoApresentante) {
		this.codigoApresentante = codigoApresentante;
	}

	public void setNomeApresentante(String nomeApresentante) {
		this.nomeApresentante = nomeApresentante;
	}

	public void setDataMovimento(LocalDate dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setQuantidadeRegistro(int quantidadeRegistro) {
		this.quantidadeRegistro = quantidadeRegistro;
	}

	@Override
	public int compareTo(CabecalhoArquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
