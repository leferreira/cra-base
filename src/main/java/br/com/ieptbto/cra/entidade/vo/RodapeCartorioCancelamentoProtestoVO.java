package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "hdb")
@XmlAccessorType(XmlAccessType.NONE)
public class RodapeCartorioCancelamentoProtestoVO extends AbstractArquivoVO {

	@XmlAttribute(name = "h01")
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar tipo registro Rodapé Cartório Cancelamenteo Protesto. Constante 8.", obrigatoriedade = true, validacao = "8", tipo = Integer.class)
	private String identificacaoRegistro;
	
	@XmlAttribute(name = "h02")
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 2, descricao = "Identificar o código do cartório.", obrigatoriedade = true)
	private String codigoCartorio;
	
	@XmlAttribute(name = "h03")
	@IAtributoArquivo(ordem = 3, posicao = 4, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Quantidade de Cancelamentos.", obrigatoriedade = true)
	private String quantidadeCancelamentos;
	
	@XmlAttribute(name = "h05")
	@IAtributoArquivo(ordem = 4, posicao = 9, tamanho = 114, formato = "", descricao = "Reservado.", obrigatoriedade = true)
	private String reservado;
	
	@XmlAttribute(name = "h06")
	@IAtributoArquivo(ordem = 6, posicao = 123, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Sequência do Registro.", obrigatoriedade = true)
	private String numeroSequencialRegistroArquivo;

	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	public String getQuantidadeCancelamentos() {
		return quantidadeCancelamentos;
	}

	public String getReservado() {
		return reservado;
	}

	public String getNumeroSequencialRegistroArquivo() {
		return numeroSequencialRegistroArquivo;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setCodigoCartorio(String codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}

	public void setQuantidadeCancelamentos(String quantidadeCancelamentos) {
		this.quantidadeCancelamentos = quantidadeCancelamentos;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setNumeroSequencialRegistroArquivo(
			String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}
}
