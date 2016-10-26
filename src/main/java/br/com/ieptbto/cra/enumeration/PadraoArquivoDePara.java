package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
public enum PadraoArquivoDePara {

	ARQUIVO_DE_PARA_ATUALIZACAO("De/Para", "txt"), //
	CAF("CAF", "txt"), //
	BRADESCO("Bradesco", "xls"), //
	BANCO_DO_BRASIL("Banco do Brasil", "txt");

	private String modelo;
	private String extensao;

	private PadraoArquivoDePara(String modelo, String extensao) {
		this.modelo = modelo;
		this.extensao = extensao;
	}

	public static PadraoArquivoDePara get(String modelo) {
		PadraoArquivoDePara[] values = PadraoArquivoDePara.values();
		for (PadraoArquivoDePara padrao : values) {
			if (modelo.startsWith(padrao.getModelo())) {
				return padrao;
			}
		}
		throw new InfraException("O layout do arquivo é desconhecido : " + modelo);
	}

	public String getModelo() {
		return modelo;
	}

	public String getExtensao() {
		return extensao;
	}
}
