package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;

/**
 * 
 * @author Lefer
 *
 */
@XmlRootElement(name = "tr")
@XmlAccessorType(XmlAccessType.NONE)
public class RegistroDesistenciaProtestoVO extends AbstractArquivoVO {
	private static final long serialVersionUID = 4806576818944343466L;

	@XmlAttribute(name = "t01")
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar o Registro Transação no arquivo. Constante 2", obrigatoriedade = true, validacao = "2", tipo = Integer.class)
	private String identificacaoRegistro;

	@XmlAttribute(name = "t02")
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 10, formato="0", posicaoCampoVazio=PosicaoCampoVazio.ESQUERDO,descricao = "Número do protocolo", obrigatoriedade = true, tipo = String.class)
	private String numeroProtocolo;

	@IAtributoArquivo(ordem = 3, posicao = 12, tamanho = 8, descricao = "data da Protocolagem DDMMAAA", obrigatoriedade = true)
	@XmlAttribute(name = "t03")
	private String dataProtocolagem;

	@IAtributoArquivo(ordem = 4, posicao = 20, tamanho = 11, descricao = "Numero do título", obrigatoriedade = true)
	@XmlAttribute(name = "t04")
	private String numeroTitulo;

	@IAtributoArquivo(ordem = 5, posicao = 31, tamanho = 45, descricao = "nome do primeiro devedor", obrigatoriedade = true)
	@XmlAttribute(name = "t05")
	private String nomePrimeiroDevedor;

	@IAtributoArquivo(ordem = 6, posicao = 76, tamanho = 14, descricao = "valor do título", obrigatoriedade = true)
	@XmlAttribute(name = "t06")
	private String valorTitulo;

	@IAtributoArquivo(ordem = 7, posicao = 90, tamanho = 1, descricao = "solicitacao de sustação / cancelamento", obrigatoriedade = true)
	@XmlAttribute(name = "t07")
	private String solicitacaoCancelamentoSustacao;

	@IAtributoArquivo(ordem = 8, posicao = 91, tamanho = 12, descricao = "agencia conta", obrigatoriedade = true)
	@XmlAttribute(name = "t08")
	private String agenciaConta;

	@IAtributoArquivo(ordem = 9, posicao = 103, tamanho = 12, descricao = "carteria / nosso número", obrigatoriedade = true)
	@XmlAttribute(name = "t09")
	private String carteiraNossoNumero;

	@IAtributoArquivo(ordem = 10, posicao = 115, tamanho = 2, descricao = "reservado", obrigatoriedade = true)
	@XmlAttribute(name = "t10")
	private String reservado;

	@IAtributoArquivo(ordem = 11, posicao = 117, tamanho = 6, descricao = "número de controle de recebimento (não utilizar)", obrigatoriedade = true)
	@XmlAttribute(name = "t11")
	private String numeroControleRecebimento;

	@IAtributoArquivo(ordem = 12, posicao = 123, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Sequência do Registro.", obrigatoriedade = true)
	@XmlAttribute(name = "t12")
	private String sequenciaRegistro;

	@Override
	public String getIdentificacaoRegistro() {
		if (identificacaoRegistro == null) {
			identificacaoRegistro = StringUtils.EMPTY;
		}
		return identificacaoRegistro.trim();
	}

	public String getNumeroProtocolo() {
		if (numeroProtocolo == null) {
			numeroProtocolo = StringUtils.EMPTY;
		}
		return numeroProtocolo.trim();
	}

	public String getDataProtocolagem() {
		return dataProtocolagem;
	}

	public String getNumeroTitulo() {
		if (numeroTitulo == null) {
			numeroTitulo = StringUtils.EMPTY;
		}
		return numeroTitulo.trim();
	}

	public String getNomePrimeiroDevedor() {
		if (nomePrimeiroDevedor == null) {
			nomePrimeiroDevedor = StringUtils.EMPTY;
		}
		return nomePrimeiroDevedor.trim();
	}

	public String getValorTitulo() {
		if (valorTitulo == null) {
			valorTitulo = StringUtils.EMPTY;
		}
		return valorTitulo.trim();
	}

	public String getSolicitacaoCancelamentoSustacao() {
		if (solicitacaoCancelamentoSustacao == null) {
			solicitacaoCancelamentoSustacao = StringUtils.EMPTY;
		}
		return solicitacaoCancelamentoSustacao.trim();
	}

	public String getAgenciaConta() {
		if (this.agenciaConta == null) {
			this.agenciaConta = StringUtils.EMPTY;
		}
		return this.agenciaConta.trim();
	}

	public String getCarteiraNossoNumero() {
		if (carteiraNossoNumero == null) {
			carteiraNossoNumero = StringUtils.EMPTY;
		}
		return carteiraNossoNumero.trim();
	}

	public String getReservado() {
		if (reservado == null) {
			reservado = StringUtils.EMPTY;
		}
		return reservado.trim();
	}

	public String getNumeroControleRecebimento() {
		if (numeroControleRecebimento == null) {
			numeroControleRecebimento = StringUtils.EMPTY;
		}
		return numeroControleRecebimento.trim();
	}

	public String getSequenciaRegistro() {
		if (sequenciaRegistro == null) {
			sequenciaRegistro = StringUtils.EMPTY;
		}
		return sequenciaRegistro.trim();
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setNumeroProtocolo(String numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}

	public void setDataProtocolagem(String dataProtocolagem) {
		this.dataProtocolagem = dataProtocolagem;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setNomePrimeiroDevedor(String nomePrimeiroDevedor) {
		this.nomePrimeiroDevedor = nomePrimeiroDevedor;
	}

	public void setValorTitulo(String valorTitulo) {
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

}
