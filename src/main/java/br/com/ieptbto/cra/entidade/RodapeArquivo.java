package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

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
@Table(name = "TB_RODAPE_ARQUIVO_DESISTENCIA_CANCELAMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_RODAPE_ARQUIVO_DESISTENCIA_CANCELAMENTO")
public class RodapeArquivo extends RodapeDesistenciaCancelamento<RodapeArquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private String codigoApresentante;
	private String nomeApresentante;
	private LocalDate dataMovimento;
	private BigDecimal somatorioValorTitulo;
	private Integer quantidadeDesistencia;

	@Override
	@Id
	@Column(name = "ID_RODAPE_ARQUIVO_DP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "CODIGO_APRESENTANTE", length = 3)
	public String getCodigoApresentante() {
		return codigoApresentante;
	}

	@Column(name = "NOME_APRENSETANTE", length = 45)
	public String getNomeApresentante() {
		return nomeApresentante;
	}

	@Column(name = "DATA_MOVIMENTO")
	public LocalDate getDataMovimento() {
		return dataMovimento;
	}

	@Column(name = "SOMATORIO_VALOR_TITULO", precision = 14, scale = 2)
	public BigDecimal getSomatorioValorTitulo() {
		return somatorioValorTitulo;
	}

	@Column(name = "QUANTIDADE_DESISTENCIA")
	public Integer getQuantidadeDesistencia() {
		return quantidadeDesistencia;
	}

	public void setQuantidadeDesistencia(Integer quantidadeDesistencia) {
		this.quantidadeDesistencia = quantidadeDesistencia;
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

	public void setSomatorioValorTitulo(BigDecimal somatorioValorTitulo) {
		this.somatorioValorTitulo = somatorioValorTitulo;
	}

	@Override
	public int compareTo(RodapeArquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
