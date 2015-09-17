package br.com.ieptbto.cra.validacao.regra;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.ValidacaoErroException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.util.DataUtil;

@Service
public class RegraNomeArquivo extends RegrasDeEntrada {

	@Override
	protected void validar(File arquivo, Arquivo arquivoProcessado, Usuario usuario, List<Exception> erros) {
		this.usuario = usuario;
		this.arquivo = arquivo;
		setErros(erros);
	}

	@Override
	protected void executar() {
		verificarNomeDoArquivo();
		validarNomeArquivo();
	}

	/**
	 * Valida nome do arquivo
	 */
	private void verificarNomeDoArquivo() {

		/**
		 * verifica se o início do nome corresponde a algum tipo de arquivo
		 * válido
		 */
		try {
			TipoArquivoEnum.getTipoArquivoEnum(arquivo.getName());
		} catch (InfraException ex) {
			logger.error(ex.getMessage(), ex.getCause());
			getErros().add(new ValidacaoErroException(arquivo.getName(), ex.getMessage(), ex.getCause()));
			throw new InfraException(ex.getMessage(), ex.getCause());
		}

		if (arquivo.getName().length() > ConfiguracaoBase.TAMANHO_NOME_ARQUIVO) {
			logger.error(Erro.NOME_DO_ARQUIVO_INVALIDO.getMensagemErro());
			getErros().add(new ValidacaoErroException(arquivo.getName(), Erro.NOME_DO_ARQUIVO_INVALIDO, arquivo.getName()));
			throw new InfraException(Erro.NOME_DO_ARQUIVO_INVALIDO.getMensagemErro());
		}

	}
	
	private void validarNomeArquivo() {
		int tamanhoNome =arquivo.getName().length();
		
		if (arquivo.getName().startsWith(TipoArquivoEnum.REMESSA.getConstante()) ||
				arquivo.getName().startsWith(TipoArquivoEnum.CONFIRMACAO.getConstante()) ||
				arquivo.getName().startsWith(TipoArquivoEnum.RETORNO.getConstante())) {
			if (tamanhoNome != 12) {
				logger.error(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
				getErros().add(new ValidacaoErroException(arquivo.getName(), Erro.TAMANHO_NOME_ARQUIVO_INVALIDO, arquivo.getName()));
				throw new InfraException(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
			}
			
			try {
				DataUtil.stringToLocalDate("ddMM.yy", arquivo.getName().substring(4, 10));
			} catch (Exception ex) {
				logger.error(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
				getErros().add(new ValidacaoErroException(arquivo.getName(), Erro.DATA_NOME_ARQUIVO_INVALIDA, arquivo.getName()));
				throw new InfraException(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			}
		} else if (arquivo.getName().startsWith(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.getConstante()) ||
				arquivo.getName().startsWith(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.getConstante()) ||
				arquivo.getName().startsWith(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
			if (tamanhoNome != 13) {
				logger.error(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
				getErros().add(new ValidacaoErroException(arquivo.getName(), Erro.TAMANHO_NOME_ARQUIVO_INVALIDO, arquivo.getName()));
				throw new InfraException(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
			}
			try {
				DataUtil.stringToLocalDate("ddMM.yy", arquivo.getName().substring(5, 11));
			} catch (Exception ex) {
				logger.error(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
				getErros().add(new ValidacaoErroException(arquivo.getName(), Erro.DATA_NOME_ARQUIVO_INVALIDA, arquivo.getName()));
				throw new InfraException(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			}
		}
		
		if (arquivo.getName().endsWith(".txt") || arquivo.getName().endsWith(".TXT")) {
			logger.error(Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			getErros().add(new ValidacaoErroException(arquivo.getName(), Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA, arquivo.getName()));
			throw new InfraException(Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA.getMensagemErro());
		}
	}
}
