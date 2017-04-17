package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.NONE)
public class TituloConvenioVO extends AbstractArquivoVO {

	@XmlAttribute(name = "t01")
	@IAtributoArquivo(ordem = 1, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 11, descricao = "")
	private String numeroTitulo;

	@XmlAttribute(name = "t02")
	@IAtributoArquivo(ordem = 2, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.DIREITO, tamanho = 8, descricao = "")
	private String dataEmissao;

	@XmlAttribute(name = "t03")
	@IAtributoArquivo(ordem = 3, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 45, descricao = "")
	private String nomeDevedor;

	@XmlAttribute(name = "t04")
	@IAtributoArquivo(ordem = 4, posicao = 0, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 14, descricao = "")
	private String numeroIdentificaoDevedor;

	@XmlAttribute(name = "t05")
	@IAtributoArquivo(ordem = 5, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 45, descricao = "")
	private String enderecoDevedor;

	@XmlAttribute(name = "t06")
	@IAtributoArquivo(ordem = 6, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 20, descricao = "")
	private String municipioDevedor;

	@XmlAttribute(name = "t07")
	@IAtributoArquivo(ordem = 7, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 20, descricao = "")
	private String bairroDevedor;

	@XmlAttribute(name = "t08")
	@IAtributoArquivo(ordem = 8, posicao = 0, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 8, descricao = "")
	private String cepDevedor;

	@XmlAttribute(name = "t09")
	@IAtributoArquivo(ordem = 9, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 11, descricao = "")
	private String documentoDevedor;

	@XmlAttribute(name = "t10")
	@IAtributoArquivo(ordem = 10, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 15, descricao = "")
	private String nossoNumero;

	@XmlAttribute(name = "t11")
	@IAtributoArquivo(ordem = 11, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 19, descricao = "")
	private String complementoRegistro;

	@XmlAttribute(name = "t12")
	@IAtributoArquivo(ordem = 12, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 8, descricao = "")
	private String dataVencimento;

	@XmlAttribute(name = "t13")
	@IAtributoArquivo(ordem = 13, posicao = 0, formato = " ", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 11, descricao = "")
	private String valorSaldo;

	@Override
	public String getIdentificacaoRegistro() {
		return TipoIdentificacaoRegistro.TITULO.getConstante();
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public String getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public String getNumeroIdentificaoDevedor() {
		return numeroIdentificaoDevedor;
	}

	public void setNumeroIdentificaoDevedor(String numeroIdentificaoDevedor) {
		this.numeroIdentificaoDevedor = numeroIdentificaoDevedor;
	}

	public String getEnderecoDevedor() {
		return enderecoDevedor;
	}

	public void setEnderecoDevedor(String enderecoDevedor) {
		this.enderecoDevedor = enderecoDevedor;
	}

	public String getMunicipioDevedor() {
		return municipioDevedor;
	}

	public void setMunicipioDevedor(String municipioDevedor) {
		this.municipioDevedor = municipioDevedor;
	}

	public String getBairroDevedor() {
		return bairroDevedor;
	}

	public void setBairroDevedor(String bairroDevedor) {
		this.bairroDevedor = bairroDevedor;
	}

	public String getCepDevedor() {
		return cepDevedor;
	}

	public void setCepDevedor(String cepDevedor) {
		this.cepDevedor = cepDevedor;
	}

	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public String getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(String dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public String getValorSaldo() {
		return valorSaldo;
	}

	public void setValorSaldo(String valorSaldo) {
		this.valorSaldo = valorSaldo;
	}

}