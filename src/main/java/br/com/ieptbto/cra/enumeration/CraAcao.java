package br.com.ieptbto.cra.enumeration;

public enum CraAcao {

	ACESSO_CRA("Ação padrão CRA.", "", ""),
	VERIFICACAO_CREDENCIAIS_ACESSO_SUCESSO("Verificação de Credenciais de Acesso", "", "verificarAcessoUsuario"),
	ARQUIVOS_PENDENTES_CARTORIO("Arquivos Pendentes Cartório", "", "arquivosPendentesCartorio"),

	ENVIO_ARQUIVO_REMESSA("Envio de Remessa", "XML_UPLOAD_REMESSA", "remessa"),
	DOWNLOAD_ARQUIVO_CONFIRMACAO("Download de Confirmação", "XML_DOWNLOAD_CONFIRMACAO", "confirmacao"),
	DOWNLOAD_ARQUIVO_RETORNO("Download de Retorno", "XML_DOWNLOAD_RETORNO", "retorno"),
	ENVIO_ARQUIVO_DESISTENCIA_PROTESTO("Envio de Desistência", "XML_UPLOAD_SUSTACAO", "desistencia"),
	ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO("Envio de Cancelamento", "XML_UPLOAD_CANCELAMENTO", "cancelamento"),
	ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Envio de Autorização", "XML_UPLOAD_AUTORIZACAO", "autorizacaoCancelamento"),

	DOWNLOAD_ARQUIVO_REMESSA("Download de Remessa", "XML_DOWNLOAD_REMESSA", "buscarRemessa"),
	ENVIO_ARQUIVO_CONFIRMACAO("Envio de Confirmação", "XML_UPLOAD_CONFIRMACAO", "enviarConfirmacao"),
	ENVIO_ARQUIVO_RETORNO("Envio de Retorno", "XML_UPLOAD_RETORNO", "enviarRetorno"),
	DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO("Download de Desistência", "XML_DOWNLOAD_SUSTACAO", "buscarDesistenciaCancelamento"),
	DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO("Download de Cancelamento", "XML_DOWLOAD_CANCELAMENTO", "buscarDesistenciaCancelamento"),
	DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO("Download de Autorização", "XML_DOWLOAD_AUTORIZACAO", "buscarDesistenciaCancelamento"),
	CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO(
													"Confirmar Recebimento Desistência e Cancelamento",
													"XML_CONFIRMAR_SUSTACAO_CANCELAMENTO_AUTORIZACAO",
													"confirmarRecebimentoDesistenciaCancelamento"),
	DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO("Download CNP", "XML_DOWNLOAD_CNP", ""),
	ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO("Envio da CNP", "XML_UPLOAD_CNP", ""),
	CONSULTA_CARTORIOS_CENTRAL_NACIONAL_PROTESTO("Cartórios vínculados a CNP", "XML_CONSULTA_CARTORIOS", ""),
	CONSULTA_PROTESTO("Consulta de Protestos", "XML_CONSULTA_PROTESTO", "");

	private String label;
	private String constante;
	private String nomeServico;

	private CraAcao(String label, String constante, String nomeServico) {
		this.label = label;
		this.constante = constante;
		this.nomeServico = nomeServico;
	}

	public String getLabel() {
		return label;
	}

	public String getConstante() {
		return constante;
	}

	public String getNomeServico() {
		return nomeServico;
	}

	public static CraAcao getCraAcao(String valor) {
		CraAcao[] values = CraAcao.values();
		for (CraAcao craAcao : values) {
			if (valor.equals(craAcao.getNomeServico())) {
				return craAcao;
			}
		}
		return CraAcao.ACESSO_CRA;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
