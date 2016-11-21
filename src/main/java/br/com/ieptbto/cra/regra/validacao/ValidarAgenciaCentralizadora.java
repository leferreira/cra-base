package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.mediator.CabecalhoMediator;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ValidarAgenciaCentralizadora extends RegraValidacao {

	private static final String AGENCIA_PALMAS = "1886";
	private static final String CODIGO_MUNICIPIO_PALMAS = "1721000";

	@Autowired
	private CabecalhoMediator cabecalhoMediator;

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {

		TipoArquivoEnum tipoArquivo = getTipoArquivo(arquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {

		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
			verificarAgencia(arquivo, usuario, erros);
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			verificarAgencia(arquivo, usuario, erros);
		}
	}

	private void verificarAgencia(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {

			for (Remessa remessa : arquivo.getRemessas()) {
				CabecalhoRemessa ultimoCabecalhoRemessa = cabecalhoMediator.buscarUltimoCabecalhoRemessa(remessa.getCabecalho());
				if (ultimoCabecalhoRemessa != null) {
					if (ultimoCabecalhoRemessa.getAgenciaCentralizadora() != null) {
						remessa.getCabecalho().setAgenciaCentralizadora(ultimoCabecalhoRemessa.getAgenciaCentralizadora());
					}

					if (remessa.getCabecalho().getAgenciaCentralizadora().trim().equals(AGENCIA_PALMAS)
							&& !remessa.getCabecalho().getCodigoMunicipio().trim().equals(CODIGO_MUNICIPIO_PALMAS)) {
						erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_AGÊNCIA_CENTRALIZADORA_INVALIDA));
					}
				} else {
					erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_AGÊNCIA_CENTRALIZADORA_INVALIDA));
				}
			}
		}
	}
}
