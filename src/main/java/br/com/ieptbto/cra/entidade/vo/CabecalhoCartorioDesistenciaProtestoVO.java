package br.com.ieptbto.cra.entidade.vo;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "hdc")
@XmlAccessorType(XmlAccessType.NONE)
public class CabecalhoCartorioDesistenciaProtestoVO extends AbstractArquivoVO {

	@XmlAttribute(name = "h01")
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar tipo registro Cabeçalho Cartório Desistência Protesto. Constante 1.", obrigatoriedade = true, validacao = "1", tipo = Integer.class)
	private String identificacaoRegistro;

	@XmlAttribute(name = "h02")
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 2, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Identificar o código do cartório.", obrigatoriedade = true)
	private String codigoCartorio;

	@XmlAttribute(name = "h03")
	@IAtributoArquivo(ordem = 3, posicao = 4, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Quantidade de Desistencias.", obrigatoriedade = true)
	private String quantidadeDesistencia;

	@XmlAttribute(name = "h04")
	@IAtributoArquivo(ordem = 4, posicao = 9, tamanho = 7, posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Preencher com dois dígitos para o Código da Unidade da Federação e 5 para Código do Município.", obrigatoriedade = true)
	private String codigoMunicipio;

	@XmlAttribute(name = "h05")
	@IAtributoArquivo(ordem = 5, posicao = 16, tamanho = 107, formato = "", descricao = "Reservado.", obrigatoriedade = true)
	private String reservado;

	@XmlAttribute(name = "h06")
	@IAtributoArquivo(ordem = 6, posicao = 123, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Sequência do Registro.", obrigatoriedade = true)
	private String sequencialRegistro;

	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	public String getQuantidadeDesistencia() {
		return quantidadeDesistencia;
	}

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public String getReservado() {
		return reservado;
	}

	public String getSequencialRegistro() {
		return sequencialRegistro;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setCodigoCartorio(String codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}

	public void setQuantidadeDesistencia(String quantidadeDesistencia) {
		this.quantidadeDesistencia = quantidadeDesistencia;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setSequencialRegistro(String sequencialRegistro) {
		this.sequencialRegistro = sequencialRegistro;
	}
}
