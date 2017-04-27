package br.com.ieptbto.cra.regra.entrada;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraTipoArquivoTXT extends RegraEntrada {

	private static final int POSICAO_FINAL_NUMERO_LINHA = 600;
	private static final int POSICAO_INICIAL_NUMERO_LINHA = 596;
	private String linha;

	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.file = file;
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	protected void executar() {
		try {
			if (this.file != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				linha = reader.readLine();
				reader.close();

				if (LayoutArquivo.TXT.equals(LayoutArquivo.get(linha))) {
					validaInicioLinha(linha);
					validaTamanhoLinha(linha, file);
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new InfraException(Erro.NAO_FOI_POSSIVEL_VERIFICAR_A_PRIMEIRA_LINHA_DO_ARQUIVO.getMensagemErro());
		}
	}

	/**
	 * Verifica o tamanho da linha
	 * 
	 * @param linha
	 * @param arquivo
	 */
	private void validaTamanhoLinha(String linha, File file) {
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(this.arquivo);
		if (tipoArquivo.equals(TipoArquivoFebraban.REMESSA) || tipoArquivo.equals(TipoArquivoFebraban.CONFIRMACAO)
				|| tipoArquivo.equals(TipoArquivoFebraban.RETORNO)) {
			if (linha.length() != ConfiguracaoBase.TAMANHO_PADRAO_LINHA) {
				logger.error(Erro.O_ARQUIVO_NAO_E_UM_TIPO_TXT_VALIDO.getMensagemErro());
				throw new InfraException(
						Erro.TAMANHO_LINHA_FORA_DO_PADRAO.getMensagemErro() + " Tamanho recebido [ " + getNumeroLinha() + " ]");
			}
		} else if (tipoArquivo.equals(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO) || tipoArquivo.equals(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO)
				|| tipoArquivo.equals(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO)) {
			if (linha.length() != ConfiguracaoBase.TAMANHO_PADRAO_LINHA_DESISTENCIA_PROTESTO) {
				logger.error(Erro.O_ARQUIVO_NAO_E_UM_TIPO_TXT_VALIDO.getMensagemErro());
				throw new InfraException(
						Erro.TAMANHO_LINHA_FORA_DO_PADRAO.getMensagemErro() + " Tamanho recebido [ " + getNumeroLinha() + " ]");
			}
		}
	}

	/**
	 * Verifica se o primeiro caracter do arquivo corresponde a um válido para
	 * arquivos TXT
	 * 
	 * @param linha
	 */
	private void validaInicioLinha(String linha) {
		if (!linha.startsWith(ConfiguracaoBase.CARACTER_INICIO_TXT)) {
			logger.error(Erro.O_ARQUIVO_NAO_E_UM_TIPO_TXT_VALIDO.getMensagemErro());
			throw new InfraException(Erro.O_ARQUIVO_NAO_E_UM_TIPO_TXT_VALIDO.getMensagemErro());
		}
	}

	private int getNumeroLinha() {
		return Integer.parseInt(linha.substring(POSICAO_INICIAL_NUMERO_LINHA, POSICAO_FINAL_NUMERO_LINHA));
	}
}