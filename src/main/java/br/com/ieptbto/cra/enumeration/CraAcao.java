package br.com.ieptbto.cra.enumeration;

public enum CraAcao {

						ACESSO_CRA("Ação padrão CRA."),
						VERIFICACAO_CREDENCIAIS_ACESSO_SUCESSO("Verificação de Credenciais de Acesso"),
						ARQUIVOS_PENDENTES_CARTORIO("Arquivos Pendentes Cartório"),

						ENVIO_ARQUIVO_REMESSA("Envio de Remessa"),
						DOWNLOAD_ARQUIVO_CONFIRMACAO("Download de Confirmação"),
						DOWNLOAD_ARQUIVO_RETORNO("Download de Retorno"),
						ENVIO_ARQUIVO_DESISTENCIA_PROTESTO("Envio de Desistência"),
						ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO("Envio de Cancelamento"),
						ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Envio de Autorização"),

						DOWNLOAD_ARQUIVO_REMESSA("Donwload de Remessa"),
						ENVIO_ARQUIVO_CONFIRMACAO("Envio de Confirmação"),
						ENVIO_ARQUIVO_RETORNO("Envio de Retorno"),
						DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO("Download de Desistência"),
						DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO("Download de Cancelamento"),
						DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Download de Autorização"),
						CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO("Confirmar Recebimento Desistência e Cancelamento"),

						DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO("Download CNP"),
						ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO("Envio da CNP"),
						CONSULTA_CARTORIOS_CENTRAL_NACIONAL_PROTESTO("Cartórios vínculados a CNP"),
						CONSULTA_PROTESTO("Consulta de Protestos");

	private String label;

	private CraAcao(String label) {
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
