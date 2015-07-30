package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaCancelamento;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_PEDIDO_DESISTENCIA")
@org.hibernate.annotations.Table(appliesTo = "TB_PEDIDO_DESISTENCIA")
public class PedidoDesistencia extends AbstractEntidade<PedidoDesistencia> {

	private static final long serialVersionUID = 4806576818944343466L;

	private int id;
	private TipoRegistroDesistenciaCancelamento tipoRegistro;
	private String numeroProtocolo;
	private LocalDate dataProtocolagem;
	private String numeroTitulo;
	private String nomePrimeiroDevedor;
	private BigDecimal valorTitulo;
	private String solicitacaoCancelamentoSustacao;
	private String agenciaConta;
	private String carteiraNossoNumero;
	private String reservado;
	private String numeroControleRecebimento;
	private String sequenciaRegistro;

	@Override
	@Id
	@Column(name = "ID_PEDIDO_DESISTENCIA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	@Column(name = "TIPO_REGISTO")
	@Enumerated(EnumType.STRING)
	public TipoRegistroDesistenciaCancelamento getTipoRegistro() {
		return tipoRegistro;
	}

	@Column(name = "NUMERO_PROTOCOLO", length = 10)
	public String getNumeroProtocolo() {
		if (numeroProtocolo == null) {
			numeroProtocolo = StringUtils.EMPTY;
		}
		return numeroProtocolo.trim();
	}

	@Column(name = "DATA_PROTOCOLAGEM")
	public LocalDate getDataProtocolagem() {
		return dataProtocolagem;
	}

	@Column(name = "NUMERO_TITULO", length = 10)
	public String getNumeroTitulo() {
		if (numeroTitulo == null) {
			numeroTitulo = StringUtils.EMPTY;
		}
		return numeroTitulo.trim();
	}

	@Column(name = "NOME_PRIMEIRO_DEVEDOR", length = 45)
	public String getNomePrimeiroDevedor() {
		if (nomePrimeiroDevedor == null) {
			nomePrimeiroDevedor = StringUtils.EMPTY;
		}
		return nomePrimeiroDevedor.trim();
	}

	@Column(name = "VALOR_TITULO", precision = 19, scale = 2)
	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	@Column(name = "SOLICITACAO_CANCELAMENTO_SUSTACAO")
	public String getSolicitacaoCancelamentoSustacao() {
		if (solicitacaoCancelamentoSustacao == null) {
			solicitacaoCancelamentoSustacao = StringUtils.EMPTY;
		}
		return solicitacaoCancelamentoSustacao.trim();
	}

	@Column(name = "AGENCIA_CONTA")
	public String getAgenciaConta() {
		if (agenciaConta == null) {
			agenciaConta = StringUtils.EMPTY;
		}
		return agenciaConta.trim();
	}

	@Column(name = "CARTEIRA_NOSSO_NUMERO")
	public String getCarteiraNossoNumero() {
		if (carteiraNossoNumero == null) {
			carteiraNossoNumero = StringUtils.EMPTY;
		}
		return carteiraNossoNumero.trim();
	}

	@Column(name = "RESERVADO")
	public String getReservado() {
		if (reservado == null) {
			reservado = StringUtils.EMPTY;
		}
		return reservado.trim();
	}

	@Column(name = "NUMERO_CONTROLE_RECEBIMENTO")
	public String getNumeroControleRecebimento() {
		if (numeroControleRecebimento == null) {
			numeroControleRecebimento = StringUtils.EMPTY;
		}
		return numeroControleRecebimento.trim();
	}

	@Column(name = "SEQUENCIAL_REGISTRO")
	public String getSequenciaRegistro() {
		if (sequenciaRegistro == null) {
			sequenciaRegistro = StringUtils.EMPTY;
		}
		return sequenciaRegistro.trim();
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTipoRegistro(TipoRegistroDesistenciaCancelamento tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public void setNumeroProtocolo(String numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}

	public void setDataProtocolagem(LocalDate dataProtocolagem) {
		this.dataProtocolagem = dataProtocolagem;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setNomePrimeiroDevedor(String nomeDevedorPrincipal) {
		this.nomePrimeiroDevedor = nomeDevedorPrincipal;
	}

	public void setValorTitulo(BigDecimal valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setSolicitacaoCancelamentoSustacao(String solicitacaoCancelamentoSustacao) {
		this.solicitacaoCancelamentoSustacao = solicitacaoCancelamentoSustacao;
	}

	public void setAgenciaConta(String agenciaConta) {
		this.agenciaConta = agenciaConta;
	}

	public void setCarteiraNossoNumero(String carteiraNossoNumero) {
		this.carteiraNossoNumero = carteiraNossoNumero;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setNumeroControleRecebimento(String numeroControleRecebimento) {
		this.numeroControleRecebimento = numeroControleRecebimento;
	}

	public void setSequenciaRegistro(String sequenciaRegistro) {
		this.sequenciaRegistro = sequenciaRegistro;
	}

	@Override
	public int compareTo(PedidoDesistencia entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		return compareToBuilder.toComparison();
	}

}
