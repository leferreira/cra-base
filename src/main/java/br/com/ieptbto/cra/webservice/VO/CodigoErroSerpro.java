package br.com.ieptbto.cra.webservice.VO;

/**
 * @author Thasso Araújo
 *
 */
public enum CodigoErroSerpro {
	
	SUCESSO("0000", "Registros OK! Aguardando distribuição."), //
	FALHA_NA_AUTENTICACAO("0001", "Falha na autenticação."), //
	NOME_DO_ARQUIVO_INVALIDO("0002", "Nome do arquivo inválido."), //
	FIM_DO_HORARIO_ENVIO("0003", "Fim do horário de envio."), //
	ARQUIVO_XML_INVALIDO("0004","Arquivo XML inválido."),
	CODIGO_APRESENTANTE_INVALIDO("1202","Código do Apresentante Inválido.");
	
	private String codigo;
	private String descricao;

	private CodigoErroSerpro(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
}
