package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Aráujo
 *
 */
public enum TipoAcaoLog {

	ACESSO_CRA("Ação padrão CRA."),
	VERIFICACAO_CREDENCIAIS_ACESSO_SUCESSO("Verificação de Credenciais de Acesso"),
	ENVIO_ARQUIVO_REMESSA("Envio de Arquivo de Remessa"),
	DOWNLOAD_ARQUIVO_REMESSA("Donwload Arquivo de Remessa"),
	ENVIO_ARQUIVO_CONFIRMACAO("Envio Arquivo Confirmação"),
	DOWNLOAD_ARQUIVO_CONFIRMACAO("Download Arquivo Confirmação"),
	ENVIO_ARQUIVO_RETORNO("Envio Arquivo de Retorno"),
	DOWNLOAD_ARQUIVO_RETORNO("Download Arquivo de Retorno"),
	DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO("Download Arquivo Desistência de Protesto"),
	DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO("Download Arquivo Cancelamento de Protesto"),
	DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Downlado Arquivo Autorização de Cancelamento"),
	ENVIO_ARQUIVO_DESISTENCIA_PROTESTO("Envio Arquivo Desistência de Protesto"),
	ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO("Envio Arquivo Cancelamento de Protesto"),
	ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Envio Arquivo Autorização de Cancelamento");

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
