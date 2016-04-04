package br.com.ieptbto.cra.enumeration;

import javax.persistence.Entity;

import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;

@Entity
public enum TipoArquivoEnum implements CraEnum {

												REMESSA("B", "TPR", "Remessa"),
												CONFIRMACAO("C", "CRT", "Confirmação"),
												RETORNO("R", "RTP", "Retorno"),
												CANCELAMENTO_DE_PROTESTO("CP", "", "Cancelamento"),
												DEVOLUCAO_DE_PROTESTO("DP", "", "Devolução"),
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
	 * retorna o tipo de arquivo dependendo do tipo informado
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
