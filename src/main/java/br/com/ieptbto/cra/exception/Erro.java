package br.com.ieptbto.cra.exception;

import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

/**
 * 
 * @author Lefer
 *
 */
public enum Erro {

					/** */
					O_ARQUIVO_NAO_E_UM_TIPO_TXT_VALIDO("0002", "O arquivo não é um tipo TXT válido "), //
					ARQUIVO_NAO_POSSUI_CABECALHO("0001", "Arquivo não possuí cabeçalho "), //
					NAO_FOI_POSSIVEL_VERIFICAR_A_PRIMEIRA_LINHA_DO_ARQUIVO("0003", "Não foi possível verificar a primeira linha do arquivo "), //
					INSTITUICAO_NAO_CADASTRADA("0004", "Instituição do usuário não está cadastrada na CRA "), //
					INSTITUICAO_NAO_ATIVA("0005", "Instituição não está ativa na CRA "), //
					USUARIO_SEM_PERMISSAO_DE_ENVIO_DE_ARQUIVO("0006", "Usuário não possui permissão para envio desse arquivo "), //
					USUARIO_INATIVO("0007", "Usuário não ativo no sistema "), //
					TIPO_DE_ARQUIVO_NAO_ENCONTRADO("0008", "Tipo deArquivo não encontrado "), //
					NOME_DO_ARQUIVO_INVALIDO("0009", "O nome do arquivo inválido."), //
					TAMANHO_LINHA_FORA_DO_PADRAO("0010", "O tamanho da linha difere do padrão " + ConfiguracaoBase.TAMANHO_PADRAO_LINHA), //
					CODIGO_IBGE_NAO_CADASTRADO("0011", "Código IBGE não cadastrado"), //
					NUMERO_SEQUENCIAL_CABECALHO_INVALDIO("0012", "Número sequencial do Cabecalho inválido"), //
					TAMANHO_NOME_ARQUIVO_INVALIDO("", "Quantidade de caracteres informada no nome do arquivo é inválida"), //
					DATA_NOME_ARQUIVO_INVALIDA("", "A data informada no nome do arquivo é inválida"), //
					EXTENSAO_NOME_ARQUIVO_INVALIDA("",
							"Extensão do nome do arquivo inválida. Verifique o nome do arquivo se segue com o layout FEBRABAN."),
					CODIGO_PORTADOR_CABECALHO_INVALIDO("", "Número do código do portador no cabeçalho inválido."),
					NOME_APRESENTANTE_CABECALHO_INVALIDO("", "Nome do apresentante no cabeçalho inválido."),
					CODIGO_PORTADOR_RODAPE_INVALIDO("", "Número do código do portador no rodapé inválido."),
					NOME_APRESENTANTE_RODAPE_INVALIDO("", "Nome do apresentante no rodapé inválido.");//

	private String codigoErro;
	private String mensagem;

	private Erro(String codigoErro, String mensagem) {
		this.codigoErro = codigoErro;
		this.mensagem = mensagem;
	}

	/**
	 * Retorna o codigo de erro
	 * 
	 * @return
	 */
	public String getCodigoErro() {
		return codigoErro;
	}

	/**
	 * Retorna a mensagem de erro
	 * 
	 * @return
	 */
	public String getMensagemErro() {
		return mensagem;
	}
}
