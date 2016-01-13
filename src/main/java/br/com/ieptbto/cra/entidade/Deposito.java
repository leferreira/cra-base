package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_DEPOSITO")
@org.hibernate.annotations.Table(appliesTo = "TB_DEPOSITO")
public class Deposito extends AbstractEntidade<Deposito> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private LocalDate data;
	private String lancamento;
	private String numeroDocumento;
	private BigDecimal valorCredito;
	private LocalDate dataImportacao;
	private Usuario usuario;
	private SituacaoDeposito situacaoDeposito;
	
	private List<BatimentoDeposito> batimentosDeposito;
	
	@OneToMany(mappedBy = "deposito", fetch=FetchType.LAZY)
	public List<BatimentoDeposito> getBatimentosDeposito() {
		return batimentosDeposito;
	}
	
	public void setBatimentosDeposito(List<BatimentoDeposito> batimentoDeposito) {
		this.batimentosDeposito = batimentoDeposito;
	}

	@Id
	@Column(name = "ID_DEPOSITO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "DATA")
	public LocalDate getData() {
		return data;
	}

	@Column(name = "LANCAMENTO", length=100)
	public String getLancamento() {
		return lancamento;
	}

	@Column(name = "NUMERO_DOCUMENTO")
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	@Column(name = "VALOR_CREDITO")
	public BigDecimal getValorCredito() {
		return valorCredito;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SITUACAO_DEPOSITO")
	public SituacaoDeposito getSituacaoDeposito() {
		return situacaoDeposito;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
	
	public LocalDate getDataImportacao() {
		return dataImportacao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public void setLancamento(String lancamento) {
		this.lancamento = lancamento;
	}
	
	public void setSituacaoDeposito(SituacaoDeposito situacaoDeposito) {
		this.situacaoDeposito = situacaoDeposito;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public void setDataImportacao(LocalDate dataImportacao) {
		this.dataImportacao = dataImportacao;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public void setValorCredito(BigDecimal valorCredito) {
		this.valorCredito = valorCredito;
	}
	
	@Override
	public String toString() {
		return DataUtil.localDateToString(getData()) + " - " + "R$ " + getValorCredito();
	}

	@Override
	public int compareTo(Deposito entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
