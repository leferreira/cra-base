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
public class RodapeArquivoDesistenciaProtestoVO extends AbstractArquivoVO {

	@XmlAttribute(name = "h01")
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar tipo registro Cabeçalho Arquivo Desistência Protesto. Constante 9.", obrigatoriedade = true, validacao = "9", tipo = Integer.class)
	private String identificacaoRegistro;

	@XmlAttribute(name = "h02")
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 3, descricao = "Identificar o código do banco/portador. Preencher com o código compensação do Banco ou número de identificação do portador.", obrigatoriedade = true, tipo  = String.class)
	private String codigoApresentante;
	
	@XmlAttribute(name = "h03")
	@IAtributoArquivo(ordem = 3, posicao = 5, tamanho = 45, descricao = "Identificar o nome do banco/portador. Preencher com o nome do portador. ('Razão Social')", obrigatoriedade = true, tipo = String.class)
	private String nomeApresentante;
	
	@XmlAttribute(name = "h04")
	@IAtributoArquivo(ordem = 4, posicao = 50, tamanho = 8, formato = "", descricao = "Identificar a data de envio da Remessa ao serviço de distribuição, no formato DDMMAAAA.", obrigatoriedade = true)
	private String dataMovimento;
	
	@XmlAttribute(name = "h05")
	@IAtributoArquivo(ordem = 5, posicao = 58, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Quantidade de Desistencias. Informada no Cabeçalho.", obrigatoriedade = true)
	private String quantidadeDesistencia;

	@XmlAttribute(name = "h06")
	@IAtributoArquivo(ordem = 6, posicao = 63, tamanho = 14, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Somatorio Valor Titulos.", obrigatoriedade = true)
	private String somatorioValorTitulo;
	
	@XmlAttribute(name = "h07")
	@IAtributoArquivo(ordem = 7, posicao = 77, tamanho = 46, formato = "", descricao = "", obrigatoriedade = true)
	private String reservado;
	
	@XmlAttribute(name = "h08")
	@IAtributoArquivo(ordem = 8, posicao = 123, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Constante 0001. Sempre reiniciar a contagem do lote de registros para as praças implantadas no processo de centralização.", obrigatoriedade = true, validacao = "00001")
	private String numeroSequencialRegistroArquivo;

	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public String getCodigoApresentante() {
		return codigoApresentante;
	}

	public String getNomeApresentante() {
		return nomeApresentante;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}

	public String getQuantidadeDesistencia() {
		return quantidadeDesistencia;
	}

	public String getSomatorioValorTitulo() {
		return somatorioValorTitulo;
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

	public void setCodigoApresentante(String codigoApresentante) {
		this.codigoApresentante = codigoApresentante;
	}

	public void setNomeApresentante(String nomeApresentante) {
		this.nomeApresentante = nomeApresentante;
	}

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setQuantidadeDesistencia(String quantidadeDesistencia) {
		this.quantidadeDesistencia = quantidadeDesistencia;
	}

	public void setSomatorioValorTitulo(String somatorioValorTitulo) {
		this.somatorioValorTitulo = somatorioValorTitulo;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	public void setNumeroSequencialRegistroArquivo(
			String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}

}
