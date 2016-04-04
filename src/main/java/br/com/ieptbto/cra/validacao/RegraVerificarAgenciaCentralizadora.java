package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.CabecalhoMediator;
import br.com.ieptbto.cra.validacao.regra.RegraCabecalho;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraVerificarAgenciaCentralizadora extends RegraCabecalho {

	@Autowired
	private CabecalhoMediator cabecalhoMediator;

	public void executar(CabecalhoRemessa cabecalho, List<Exception> erros) {
		setErros(erros);
		setCabecalhoRemessa(cabecalho);

		executar();
	}

	@Override
	protected void executar() {
		TipoArquivoEnum tipoArquivo = getCabecalhoRemessa().getRemessa().getArquivo().getTipoArquivo().getTipoArquivo();

		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {

		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {

		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			verificarAgencia();
		}
	}

	private void verificarAgencia() {
		CabecalhoRemessa ultimoCabecalhoRemessa = cabecalhoMediator.buscarUltimoCabecalhoRemessa(getCabecalhoRemessa());

		if (ultimoCabecalhoRemessa != null) {
			if (getCabecalhoRemessa().getAgenciaCentralizadora() != ultimoCabecalhoRemessa.getAgenciaCentralizadora()) {
				getCabecalhoRemessa().setAgenciaCentralizadora(ultimoCabecalhoRemessa.getAgenciaCentralizadora());
			}
		}
	}
}
