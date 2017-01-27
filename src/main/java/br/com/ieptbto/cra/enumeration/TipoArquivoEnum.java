package br.com.ieptbto.cra.enumeration;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;

@Entity
public enum TipoArquivoEnum implements AbstractCraEnum {

	REMESSA("B", "TPR", "Remessa"),
	CONFIRMACAO("C", "CRT", "Confirmação"),
	RETORNO("R", "RTP", "Retorno"),
	CANCELAMENTO_DE_PROTESTO("CP", "", "Cancelamento"),
	DEVOLUCAO_DE_PROTESTO("DP", "", "Desistência"),
	AUTORIZACAO_DE_CANCELAMENTO("AC", "", "Autorização");

	public String constante;
	public String identificacaoTransacaoCabecalho;
	public String label;

	TipoArquivoEnum(String constante, String identificacaoTransacaoCabecalho, String label) {
		this.constante = constante;
		this.identificacaoTransacaoCabecalho = identificacaoTransacaoCabecalho;
		this.label = label;
	}

	/**
	 * retorna o tipo de arquivo Febraban dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoArquivoEnum getTipoArquivoEnum(String valor) {
		TipoArquivoEnum[] values = TipoArquivoEnum.values();
		for (TipoArquivoEnum tipoArquivo : values) {
			if (valor.startsWith(tipoArquivo.getConstante())) {
				return tipoArquivo;
			}
		}
		throw new InfraException(Erro.TIPO_DE_ARQUIVO_NAO_ENCONTRADO.getMensagemErro() + valor);
	}
	
	/**
	 * Gerar nome arquivo com a data atual
	 * @param tipoArquivo
	 * @param codigoPortador
	 * @param sequencialArquivo
	 * @return
	 */
	public static String generateNomeArquivoFebraban(TipoArquivoEnum tipoArquivo, String codigoPortador, String sequencialArquivo) {
		SimpleDateFormat dataPadraoArquivo = new SimpleDateFormat("ddMM.yy");
		String dataArquivo = dataPadraoArquivo.format(new Date()).toString();
		return tipoArquivo.getConstante() + codigoPortador + dataArquivo + sequencialArquivo;
	}

	/**
	 * Buscar Tipo Arquivo Febraban por Arquivo
	 * @param arquivo
	 * @return
	 */
	public static TipoArquivoEnum getTipoArquivoEnum(Arquivo arquivo) {
		return arquivo.getTipoArquivo().getTipoArquivo();
	}

	@Override
	public String getConstante() {
		return constante;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	public String getIdentificacaoTransacaoCabecalho() {
		return identificacaoTransacaoCabecalho;
	}

	@Override
	public String toString() {
		return constante + " - " + label.toUpperCase();
	}
}
