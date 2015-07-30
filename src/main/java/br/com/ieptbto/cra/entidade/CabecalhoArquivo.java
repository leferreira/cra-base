package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_CABECALHO_ARQUIVO_DP_CP")
@org.hibernate.annotations.Table(appliesTo = "TB_CABECALHO_ARQUIVO_DP_CP")
public class CabecalhoArquivo extends CabecalhoDesistenciaCancelamento<CabecalhoArquivo> {

	/****/
	private static final long serialVersionUID = 1L;

	private int id;
	private String codigoAprensentante;
	private String nomeAprensentante;
	private LocalDate dataMovimento;
	private int quantidadeRegistrosTipoDois;

	@Override
	@Id
	@Column(name = "ID_CABECALHO_ARQUIVO_DP_CP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "CODIGO_APRESENTANTE", length = 3)
	public String getCodigoAprensentante() {
		return codigoAprensentante;
	}

	@Column(name = "NOME_APRENSETANTE", length = 45)
	public String getNomeAprensentante() {
		return nomeAprensentante;
	}

	@Column(name = "DATA_MOVIMENTO")
	public LocalDate getDataMovimento() {
		return dataMovimento;
	}

	@Column(name = "QUANTIDADE_REGISTROS_TIPO_2")
	public int getQuantidadeRegistrosTipoDois() {
		return quantidadeRegistrosTipoDois;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCodigoAprensentante(String codigoAprensentante) {
		this.codigoAprensentante = codigoAprensentante;
	}

	public void setNomeAprensentante(String nomeAprensentante) {
		this.nomeAprensentante = nomeAprensentante;
	}

	public void setDataMovimento(LocalDate dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setQuantidadeRegistrosTipoDois(int quantidadeRegistrosTipoDois) {
		this.quantidadeRegistrosTipoDois = quantidadeRegistrosTipoDois;
	}

	@Override
	public int compareTo(CabecalhoArquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
