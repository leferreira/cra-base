package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_TITULO_FILIADO")
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO_FILIADO")
public class TituloFiliado extends AbstractEntidade<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String numeroTitulo;
	private BigDecimal valorTitulo;
	private BigDecimal valorSaldoTitulo;
	private LocalDate dataVencimento;
	private LocalDate dataEmissao;
	private String nomeDevedor;
	private String documentoDevedor;
	private String enderecoDevedor;
	private String cidadeDevedor;
	private String cepDevedor;
	private String ufDevedor;
	private Municipio pracaProtesto;
	private Filiado filiado;

	public int getId() {
		return id;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	public BigDecimal getValorSaldoTitulo() {
		return valorSaldoTitulo;
	}

	public LocalDate getDataVencimento() {
		return dataVencimento;
	}

	public LocalDate getDataEmissao() {
		return dataEmissao;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	public String getEnderecoDevedor() {
		return enderecoDevedor;
	}

	public String getCidadeDevedor() {
		return cidadeDevedor;
	}

	public String getCepDevedor() {
		return cepDevedor;
	}

	public String getUfDevedor() {
		return ufDevedor;
	}

	public Municipio getPracaProtesto() {
		return pracaProtesto;
	}

	public Filiado getFiliado() {
		return filiado;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setValorTitulo(BigDecimal valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setValorSaldoTitulo(BigDecimal valorSaldoTitulo) {
		this.valorSaldoTitulo = valorSaldoTitulo;
	}

	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public void setDataEmissao(LocalDate dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
	}

	public void setEnderecoDevedor(String enderecoDevedor) {
		this.enderecoDevedor = enderecoDevedor;
	}

	public void setCidadeDevedor(String cidadeDevedor) {
		this.cidadeDevedor = cidadeDevedor;
	}

	public void setCepDevedor(String cepDevedor) {
		this.cepDevedor = cepDevedor;
	}

	public void setUfDevedor(String ufDevedor) {
		this.ufDevedor = ufDevedor;
	}

	public void setPracaProtesto(Municipio pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
	}

	@Override
	public int compareTo(TituloFiliado entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
