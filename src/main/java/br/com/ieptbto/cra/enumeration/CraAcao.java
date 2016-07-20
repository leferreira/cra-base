package br.com.ieptbto.cra.enumeration;

public enum CraAcao {

						ACESSO_CRA("Ação padrão CRA.", ""),
						VERIFICACAO_CREDENCIAIS_ACESSO_SUCESSO("Verificação de Credenciais de Acesso", ""),
						ARQUIVOS_PENDENTES_CARTORIO("Arquivos Pendentes Cartório", ""),

						ENVIO_ARQUIVO_REMESSA("Envio de Remessa", "XML_UPLOAD_REMESSA"),
						DOWNLOAD_ARQUIVO_CONFIRMACAO("Download de Confirmação", "XML_DOWNLOAD_CONFIRMACAO"),
						DOWNLOAD_ARQUIVO_RETORNO("Download de Retorno", "XML_DOWNLOAD_RETORNO"),
						ENVIO_ARQUIVO_DESISTENCIA_PROTESTO("Envio de Desistência", "XML_UPLOAD_SUSTACAO"),
						ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO("Envio de Cancelamento", "XML_UPLOAD_CANCELAMENTO"),
						ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Envio de Autorização", "XML_UPLOAD_AUTORIZACAO"),

						DOWNLOAD_ARQUIVO_REMESSA("Download de Remessa", "XML_DOWNLOAD_REMESSA"),
						ENVIO_ARQUIVO_CONFIRMACAO("Envio de Confirmação", "XML_UPLOAD_CONFIRMACAO"),
						ENVIO_ARQUIVO_RETORNO("Envio de Retorno", "XML_UPLOAD_RETORNO"),
						DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO("Download de Desistência", "XML_DOWNLOAD_SUSTACAO"),
						DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO("Download de Cancelamento", "XML_DOWLOAD_CANCELAMENTO"),
						DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Download de Autorização", "XML_DOWLOAD_AUTORIZACAO"),
						CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO("Confirmar Recebimento Desistência e Cancelamento",
								"XML_CONFIRMAR_SUSTACAO_CANCELAMENTO_AUTORIZACAO"),
						DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO("Download CNP", "XML_DOWNLOAD_CNP"),
						ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO("Envio da CNP", "XML_UPLOAD_CNP"),
						CONSULTA_CARTORIOS_CENTRAL_NACIONAL_PROTESTO("Cartórios vínculados a CNP", "XML_CONSULTA_CARTORIOS"),
						CONSULTA_PROTESTO("Consulta de Protestos", "XML_CONSULTA_PROTESTO");

	private String label;
	private String constante;

	private CraAcao(String label, String constante) {
		this.label = label;
		this.constante = constante;
	}

	public String getLabel() {
		return label;
	}

	public String getConstante() {
		return constante;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
