package br.com.ieptbto.cra.mediator;

import org.springframework.stereotype.Service;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ConfiguracaoBase {

	public static final int TAMANHO_NOME_ARQUIVO = 13;
	public static String DIRETORIO_BASE = "../ARQUIVOS_CRA/";
	public static String DIRETORIO_TEMP_BASE = "../ARQUIVOS_CRA/TEMP/";
	public static String DIRETORIO_BASE_INSTITUICAO = DIRETORIO_BASE + "INSTITUICAO/";
	public static String DIRETORIO_BASE_INSTITUICAO_TEMP = DIRETORIO_TEMP_BASE + "INSTITUICAO/";
	public static String DIRETORIO_BASE_DE_PARA_TEMP = DIRETORIO_TEMP_BASE + "ARQUIVOS_DE_PARA/";
	public static String BARRA = "/";
	public static String RELATORIOS_PATH = "/br/com/ieptbto/cra/relatorio/";
	public static String RELATORIOS_CONVENIO_PATH = "/br/com/ieptbto/cra/relatorioConvenio/";
	public static final String ZERO = "0";
	public static final String EXTENSAO_ARQUIVO_ZIP = ".zip";
	public static final int TAMANHO_PADRAO_LINHA = 600;
	public static final int TAMANHO_PADRAO_LINHA_DESISTENCIA_PROTESTO = 127;
	public static final String FECHA_CHAVE = "]";
	public static final String CARACTER_INICIO_TXT = "0";
	public static final String CARACTER_INICIO_XML = "<";
}
