package br.com.ieptbto.cra.regra.entrada;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RegraNomeArquivo extends RegraEntrada {

	@Override
	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.file = file;
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	@Override
	protected void executar() {
		validarNomeArquivo();
	}

	private void validarNomeArquivo() {
		if (this.file != null) {

			int tamanhoNome = arquivo.getNomeArquivo().length();
			if (arquivo.getNomeArquivo().endsWith(".txt") || arquivo.getNomeArquivo().endsWith(".TXT")) {
				logger.error(Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA.getMensagemErro());
				throw new InfraException(Erro.EXTENSAO_NOME_ARQUIVO_INVALIDA.getMensagemErro());
			}

			if (arquivo.getNomeArquivo().startsWith(TipoArquivoFebraban.REMESSA.getConstante())
					|| arquivo.getNomeArquivo().startsWith(TipoArquivoFebraban.CONFIRMACAO.getConstante())
					|| arquivo.getNomeArquivo().startsWith(TipoArquivoFebraban.RETORNO.getConstante())) {
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
			} else if (arquivo.getNomeArquivo().startsWith(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.getConstante())
					|| arquivo.getNomeArquivo().startsWith(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.getConstante())
					|| arquivo.getNomeArquivo().startsWith(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
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
}
