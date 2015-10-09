package br.com.ieptbto.cra.validacao.regra;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RegraNomeArquivo extends RegrasDeEntrada {

	@Override
	protected void validar(File arquivo, Arquivo arquivoProcessado, Usuario usuario, List<Exception> erros) {
		this.usuario = usuario;
		this.arquivo = arquivo;
		setErros(erros);
//		executar();
	}

	@Override
	protected void executar() {
		validarNomeArquivo();
	}

	private void validarNomeArquivo() {
		int tamanhoNome =arquivo.getName().length();
		
		if (arquivo.getName().endsWith(".txt") || arquivo.getName().endsWith(".TXT")) {
			logger.error(Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			throw new InfraException(Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA.getMensagemErro());
		}

		if (arquivo.getName().startsWith(TipoArquivoEnum.REMESSA.getConstante()) ||
				arquivo.getName().startsWith(TipoArquivoEnum.CONFIRMACAO.getConstante()) ||
				arquivo.getName().startsWith(TipoArquivoEnum.RETORNO.getConstante())) {
			if (tamanhoNome != 12) {
				logger.error(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
				throw new InfraException(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
			}
			
			try {
				DataUtil.stringToLocalDate("ddMM.yy", arquivo.getName().substring(4, 10));
			} catch (Exception ex) {
				logger.error(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
				throw new InfraException(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			}
		} else if (arquivo.getName().startsWith(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.getConstante()) ||
				arquivo.getName().startsWith(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.getConstante()) ||
				arquivo.getName().startsWith(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
			if (tamanhoNome != 13) {
				logger.error(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
				throw new InfraException(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
			}
			try {
				DataUtil.stringToLocalDate("ddMM.yy", arquivo.getName().substring(5, 11));
			} catch (Exception ex) {
				logger.error(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
				throw new InfraException(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			}
		}
		
	}
}
