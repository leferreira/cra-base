package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CabecalhoMediator;
import br.com.ieptbto.cra.validacao.regra.RegraCabecalho;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RegraVerificarAgenciaCentralizadora extends RegraCabecalho {

	private static final String AGENCIA_PALMAS = "1886";
	private static final String CODIGO_MUNICIPIO_PALMAS = "1721000";

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
			verificarAgencia();
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			verificarAgencia();
		}
	}

	private void verificarAgencia() {
		CabecalhoRemessa ultimoCabecalhoRemessa = cabecalhoMediator.buscarUltimoCabecalhoRemessa(getCabecalhoRemessa());
		if (ultimoCabecalhoRemessa != null) {
			if (ultimoCabecalhoRemessa.getAgenciaCentralizadora() != null) {
				getCabecalhoRemessa().setAgenciaCentralizadora(ultimoCabecalhoRemessa.getAgenciaCentralizadora());
			}
			if (getCabecalhoRemessa().getAgenciaCentralizadora().trim().equals(AGENCIA_PALMAS)
					&& !getCabecalhoRemessa().getCodigoMunicipio().trim().equals(CODIGO_MUNICIPIO_PALMAS)) {
				throw new InfraException("Não foi possível identificar a agência centralizadora. Entre em contato com a CRA!");
			}
		} else {
			throw new InfraException("Não foi possível identificar a agência centralizadora. Entre em contato com a CRA!");
		}
	}
}
