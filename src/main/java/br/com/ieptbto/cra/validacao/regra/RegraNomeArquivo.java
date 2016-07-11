package br.com.ieptbto.cra.validacao.regra;

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
	protected void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.usuario = usuario;
		this.arquivo = arquivo;
		setErros(erros);
		executar();
	}

	@Override
	protected void executar() {
		validarNomeArquivo();
	}

	private void validarNomeArquivo() {
		int tamanhoNome = arquivo.getNomeArquivo().length();

		if (arquivo.getNomeArquivo().endsWith(".txt") || arquivo.getNomeArquivo().endsWith(".TXT")) {
			logger.error(Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			throw new InfraException(Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA.getMensagemErro());
		}

		if (arquivo.getNomeArquivo().startsWith(TipoArquivoEnum.REMESSA.getConstante())
				|| arquivo.getNomeArquivo().startsWith(TipoArquivoEnum.CONFIRMACAO.getConstante())
				|| arquivo.getNomeArquivo().startsWith(TipoArquivoEnum.RETORNO.getConstante())) {
			if (tamanhoNome != 12) {
				logger.error(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
				throw new InfraException(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
			}

			try {
				DataUtil.stringToLocalDate("ddMM.yy", arquivo.getNomeArquivo().substring(4, 10));
			} catch (Exception ex) {
				logger.error(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
				throw new InfraException(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			}
		} else if (arquivo.getNomeArquivo().startsWith(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.getConstante())
				|| arquivo.getNomeArquivo().startsWith(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.getConstante())
				|| arquivo.getNomeArquivo().startsWith(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
			if (tamanhoNome != 13) {
				logger.error(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
				throw new InfraException(Erro.TAMANHO_NOME_ARQUIVO_INVALIDO.getMensagemErro());
			}
			try {
				DataUtil.stringToLocalDate("ddMM.yy", arquivo.getNomeArquivo().substring(5, 12));
			} catch (Exception ex) {
				logger.error(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
				throw new InfraException(Erro.DATA_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			}
		}

	}
}
