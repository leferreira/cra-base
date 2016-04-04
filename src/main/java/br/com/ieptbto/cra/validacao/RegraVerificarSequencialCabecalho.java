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
public class RegraVerificarSequencialCabecalho extends RegraCabecalho {

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
			verificarSequencialRetorno();
		}
	}

	private void verificarSequencialRetorno() {
		CabecalhoRemessa ultimoCabecalhoRetorno = cabecalhoMediator.buscarUltimoCabecalhoRetornoPorMunicipio(getCabecalhoRemessa());

		if (ultimoCabecalhoRetorno != null) {
			if (getCabecalhoRemessa().getNumeroSequencialRemessa() <= ultimoCabecalhoRetorno.getNumeroSequencialRemessa()) {
				getCabecalhoRemessa().setNumeroSequencialRemessa(ultimoCabecalhoRetorno.getNumeroSequencialRemessa() + 1);
			}
		}
	}
}
