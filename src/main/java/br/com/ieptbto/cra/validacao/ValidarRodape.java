package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;

@Service
public class ValidarRodape {

	protected static final Logger logger = Logger.getLogger(ValidarRodape.class);

	private Rodape rodape;
	private List<Exception> erros;

	public void validar(Rodape rodape, List<Exception> erros) {
		this.rodape = rodape;
		this.erros = erros;

		executarRegras();
	}

	private void executarRegras() {
		validarDadosRodape();
	}

	private void validarDadosRodape() {
		if (getRodape().getNumeroCodigoPortador() != null) {
			if (getRodape().getNumeroCodigoPortador().trim().isEmpty()) {
				logger.error(Erro.CODIGO_PORTADOR_RODAPE_INVALIDO.getMensagemErro());
				throw new InfraException(Erro.CODIGO_PORTADOR_RODAPE_INVALIDO.getMensagemErro());
			}
		}

		if (getRodape().getNomePortador() != null) {
			if (getRodape().getNomePortador().trim().isEmpty()) {
				logger.error(Erro.NOME_APRESENTANTE_RODAPE_INVALIDO.getMensagemErro());
				throw new InfraException(Erro.NOME_APRESENTANTE_RODAPE_INVALIDO.getMensagemErro());
			}
		}
	}

	public Rodape getRodape() {
		return rodape;
	}

	public List<Exception> getErros() {
		return erros;
	}

}
