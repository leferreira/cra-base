package br.com.ieptbto.cra.enumeration.regra;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.enumeration.AbstractCraEnum;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;

@Entity
public enum TipoArquivoFebraban
		implements AbstractCraEnum {

	REMESSA("B", "TPR", "Remessa"),
	CONFIRMACAO("C", "CRT", "Confirmação"),
	RETORNO("R", "RTP", "Retorno"),
	CANCELAMENTO_DE_PROTESTO("CP", "", "Cancelamento"),
	DEVOLUCAO_DE_PROTESTO("DP", "", "Desistência"),
	AUTORIZACAO_DE_CANCELAMENTO("AC", "", "Autorização");

	public String constante;
	public String identificacaoTransacaoCabecalho;
	public String label;

	TipoArquivoFebraban(String constante, String identificacaoTransacaoCabecalho, String label) {
		this.constante = constante;
		this.identificacaoTransacaoCabecalho = identificacaoTransacaoCabecalho;
		this.label = label;
	}

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoArquivoFebraban getTipoArquivoFebraban(String valor) {
		TipoArquivoFebraban[] values = TipoArquivoFebraban.values();
		for (TipoArquivoFebraban tipoArquivo : values) {
			if (valor.startsWith(tipoArquivo.getConstante())) {
				return tipoArquivo;
			}
		}
		throw new InfraException(Erro.TIPO_DE_ARQUIVO_NAO_ENCONTRADO.getMensagemErro() + valor);
	}

	public static TipoArquivoFebraban getTipoArquivoPorConstante(String constante) {
		TipoArquivoFebraban[] values = TipoArquivoFebraban.values();
		for (TipoArquivoFebraban tipoArquivo : values) {
			if (constante.equals(tipoArquivo.getConstante())) {
				return tipoArquivo;
			}
		}
		throw new InfraException(Erro.TIPO_DE_ARQUIVO_NAO_ENCONTRADO.getMensagemErro() + constante);
	}
	
	/**
	 * Buscar Tipo Arquivo Febraban por Arquivo
	 * @param arquivo
	 * @return
	 */
	public static TipoArquivoFebraban getTipoArquivoFebraban(Arquivo arquivo) {
		return arquivo.getTipoArquivo().getTipoArquivo();
	}

	/**
	 * Gerar nome arquivo com a data atual
	 * @param tipoArquivo
	 * @param codigoPortador
	 * @param sequencialArquivo
	 * @return
	 */
	public static String generateNomeArquivoFebraban(TipoArquivoFebraban tipoArquivo, String codigoPortador, String sequencialArquivo) {
		SimpleDateFormat dataPadraoArquivo = new SimpleDateFormat("ddMM.yy");
		String dataArquivo = dataPadraoArquivo.format(new Date()).toString();
		return tipoArquivo.getConstante() + codigoPortador + dataArquivo + sequencialArquivo;
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
