package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.validacao.regra.RegraCabecalho;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RegraVerificarDadosCabecalho extends RegraCabecalho {

	public void executar(CabecalhoRemessa cabecalho, List<Exception> erros) {
		setErros(erros);
		setCabecalhoRemessa(cabecalho);

		executar();
	}

	@Override
	protected void executar() {
		verificarCamposEmBranco();
	}

	private void verificarCamposEmBranco() {
		if (getCabecalhoRemessa().getNumeroCodigoPortador() != null) {
			if (getCabecalhoRemessa().getNumeroCodigoPortador().trim().isEmpty()) {
				logger.error(Erro.CODIGO_PORTADOR_CABECALHO_INVALIDO.getMensagemErro());
				throw new InfraException(Erro.CODIGO_PORTADOR_CABECALHO_INVALIDO.getMensagemErro());
			}
		}

		if (getCabecalhoRemessa().getNomePortador() != null) {
			if (getCabecalhoRemessa().getNomePortador().trim().isEmpty()) {
				logger.error(Erro.NOME_APRESENTANTE_CABECALHO_INVALIDO.getMensagemErro());
				throw new InfraException(Erro.NOME_APRESENTANTE_CABECALHO_INVALIDO.getMensagemErro());
			}
		}
	}
}
