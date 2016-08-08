package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;

@Service
public class ValidarRodape extends RegraValidacao {

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	@Override
	protected void executar() {
		TipoArquivoEnum tipoArquivo = getTipoArquivo(arquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {

		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
			validarDadosRodape();
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			validarDadosRodape();
		}
	}

	private void validarDadosRodape() {
		for (Remessa remessa : arquivo.getRemessas()) {
			if (remessa.getRodape().getNumeroCodigoPortador() != null) {
				if (remessa.getRodape().getNumeroCodigoPortador().trim().isEmpty()) {
					logger.error(Erro.CODIGO_PORTADOR_RODAPE_INVALIDO.getMensagemErro());
					throw new InfraException(Erro.CODIGO_PORTADOR_RODAPE_INVALIDO.getMensagemErro());
				}
			}

			if (remessa.getRodape().getNomePortador() != null) {
				if (remessa.getRodape().getNomePortador().trim().isEmpty()) {
					logger.error(Erro.NOME_APRESENTANTE_RODAPE_INVALIDO.getMensagemErro());
					throw new InfraException(Erro.NOME_APRESENTANTE_RODAPE_INVALIDO.getMensagemErro());
				}
			}
		}
	}
}