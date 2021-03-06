package br.com.ieptbto.cra.entidade.vo;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.NONE)
public class RodapeVO extends AbstractArquivoVO {

	@XmlAttribute(name = "t08", required = true)
	@IAtributoArquivo(ordem = 8, posicao = 597, tamanho = 04, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Informar o número sequêncial do registro.", obrigatoriedade = true)
	private String numeroSequencialRegistroArquivo;

	@XmlAttribute(name = "t07", required = true)
	@IAtributoArquivo(ordem = 7, posicao = 76, tamanho = 521,
			descricao = "Ajusatar o tamanho do registro do trailler com o tamanho do registro de transação. Preencher com brancos.",
			obrigatoriedade = true)
	private String complementoRegistro;

	@XmlAttribute(name = "t06", required = true)
	@IAtributoArquivo(ordem = 6, posicao = 58, tamanho = 18, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Informar o somatório do Saldos dos Títulos.", obrigatoriedade = true)
	private String somatorioValorRemessa;

	@XmlAttribute(name = "t05", required = true)
	@IAtributoArquivo(ordem = 5, posicao = 53, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Informar o somatório dos registros. Conforme a regra estabelecida para os campos do registro header. Campos 09,10,11 e 12.",
			obrigatoriedade = true)
	private String somatorioQtdRemessa;

	@XmlAttribute(name = "t04", required = true)
	@IAtributoArquivo(ordem = 4, posicao = 45, tamanho = 8,
			descricao = "Informar a data do envio da remessa ao Serviço de Distribuição de Títulos, no formato DDMMAAAA.", obrigatoriedade = true)
	private String dataMovimento;

	@XmlAttribute(name = "t03", required = true)
	@IAtributoArquivo(ordem = 3, posicao = 5, tamanho = 40, descricao = "Preencher com o nome do Portador.('Razão Social')", obrigatoriedade = true)
	private String nomePortador;

	@XmlAttribute(name = "t02", required = true)
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 3,
			descricao = "Identificar o código do banco/portador. Preencher com o código compensação do Banco ou número de identificação do portador.",
			obrigatoriedade = true)
	private String numeroCodigoPortador;

	@XmlAttribute(name = "t01", required = true)
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar o registro trailler no arquivo. Constante 9.",
			obrigatoriedade = true, validacao = "9", tipo = Integer.class)
	private String identificacaoRegistro;

	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public String getNumeroCodigoPortador() {
		return numeroCodigoPortador;
	}

	public String getNomePortador() {
		if (nomePortador != null) {
			nomePortador = RemoverAcentosUtil.removeAcentos(nomePortador);
		}
		return nomePortador;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}

	public String getSomatorioQtdRemessa() {
		return somatorioQtdRemessa;
	}

	public String getSomatorioValorRemessa() {
		return somatorioValorRemessa;
	}

	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	public String getNumeroSequencialRegistroArquivo() {
		return numeroSequencialRegistroArquivo;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setNumeroCodigoPortador(String numeroCodigoPortador) {
		this.numeroCodigoPortador = numeroCodigoPortador;
	}

	public void setNomePortador(String nomePortador) {
		this.nomePortador = nomePortador;
	}

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setSomatorioQtdRemessa(String somatorioQtdRemessa) {
		this.somatorioQtdRemessa = somatorioQtdRemessa;
	}

	public void setSomatorioValorRemessa(String somatorioValorRemessa) {
		this.somatorioValorRemessa = somatorioValorRemessa;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialRegistroArquivo(String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}

	public static RodapeVO parseRodape(Rodape rodape, CabecalhoRemessa cabecalhoRemessa) {
		RodapeVO rodapeVO = new RodapeVO();
		rodapeVO.setComplementoRegistro(rodape.getComplementoRegistro());
		rodapeVO.setDataMovimento(rodape.getDataMovimento().toString(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY));
		rodapeVO.setIdentificacaoRegistro(rodape.getIdentificacaoRegistro().getConstante());
		rodapeVO.setNomePortador(rodape.getNomePortador());
		rodapeVO.setNumeroCodigoPortador(rodape.getNumeroCodigoPortador());
		rodapeVO.setNumeroSequencialRegistroArquivo(rodape.getNumeroSequencialRegistroArquivo());
		rodapeVO.setSomatorioQtdRemessa(somatorioSegurancaRegistrosCabecalho(cabecalhoRemessa));
		rodapeVO.setSomatorioValorRemessa(StringUtils.leftPad(rodape.getSomatorioValorRemessa().toString().replaceAll("\\D", ""), 5, "0"));

		return rodapeVO;
	}

	private static String somatorioSegurancaRegistrosCabecalho(CabecalhoRemessa cabecalho) {
		int somatorioSeguranca = cabecalho.getQtdRegistrosRemessa() + cabecalho.getQtdTitulosRemessa() + cabecalho.getQtdIndicacoesRemessa()
				+ cabecalho.getQtdOriginaisRemessa();
		return StringUtils.leftPad(Integer.toString(somatorioSeguranca), 5, "0");
	}
}
