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
@XmlRootElement(name = "tlc")
@XmlAccessorType(XmlAccessType.NONE)
public class RodapeCartorioDesistenciaProtestoVO extends AbstractArquivoVO {

	@XmlAttribute(name = "t01")
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar tipo registro Rodapé Cartório Desistência Protesto. Constante 8.", obrigatoriedade = true, validacao = "8", tipo = Integer.class)
	private String identificacaoRegistro;

	@XmlAttribute(name = "t02")
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 2, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Identificar o código do cartório.", obrigatoriedade = true)
	private String codigoCartorio;

	@XmlAttribute(name = "t03")
	@IAtributoArquivo(ordem = 3, posicao = 4, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Quantidade de Desistencias.", obrigatoriedade = true)
	private String somaTotalCancelamentoDesistencia;

	@XmlAttribute(name = "t04")
	@IAtributoArquivo(ordem = 4, posicao = 9, tamanho = 114, descricao = "Reservado.", obrigatoriedade = true)
	private String reservado;

	@XmlAttribute(name = "t05")
	@IAtributoArquivo(ordem = 5, posicao = 123, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Sequência do Registro.", obrigatoriedade = true)
	private String sequencialRegistro;

	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	public String getSomaTotalCancelamentoDesistencia() {
		if (somaTotalCancelamentoDesistencia == null) {
			somaTotalCancelamentoDesistencia = "0";
		}
		return somaTotalCancelamentoDesistencia;
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

	public void setSomaTotalCancelamentoDesistencia(String somaTotalCancelamentoDesistencia) {
		this.somaTotalCancelamentoDesistencia = somaTotalCancelamentoDesistencia;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setSequencialRegistro(String sequencialRegistro) {
		this.sequencialRegistro = sequencialRegistro;
	}
}
