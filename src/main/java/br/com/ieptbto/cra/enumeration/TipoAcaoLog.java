package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Aráujo
 *
 */
public enum TipoAcaoLog {

							ACESSO_CRA("Ação padrão CRA."),
							VERIFICACAO_CREDENCIAIS_ACESSO_SUCESSO("Verificação de Credenciais de Acesso"),
							ENVIO_ARQUIVO_REMESSA("Envio de Remessa"),
							DOWNLOAD_ARQUIVO_REMESSA("Donwload de Remessa"),
							ENVIO_ARQUIVO_CONFIRMACAO("Envio de Confirmação"),
							DOWNLOAD_ARQUIVO_CONFIRMACAO("Download de Confirmação"),
							ENVIO_ARQUIVO_RETORNO("Envio de Retorno"),
							DOWNLOAD_ARQUIVO_RETORNO("Download de Retorno"),
							DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO("Download de Desistência"),
							DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO("Download de Cancelamento"),
							DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Downlado de Autorização"),
							ENVIO_ARQUIVO_DESISTENCIA_PROTESTO("Envio de Desistência"),
							ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO("Envio de Cancelamento"),
							ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Envio de Autorização"),
							ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO("Envio da CNP"),
							DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO("Download CNP");

	private String label;

	private TipoAcaoLog(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
