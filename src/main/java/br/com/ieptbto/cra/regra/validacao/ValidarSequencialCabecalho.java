package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.CabecalhoMediator;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ValidarSequencialCabecalho extends RegraValidacao {

	@Autowired
	private CabecalhoMediator cabecalhoMediator;

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {

		TipoArquivoEnum tipoArquivo = getTipoArquivo(arquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {

		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {

		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			verificarSequencialRetorno(arquivo, usuario, erros);
		}
	}

	private void verificarSequencialRetorno(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {

			for (Remessa remessa : arquivo.getRemessas()) {
				CabecalhoRemessa ultimoCabecalhoRetorno =
						cabecalhoMediator.buscarUltimoCabecalhoRetornoPorMunicipio(remessa.getCabecalho());

				if (ultimoCabecalhoRetorno != null) {
					if (remessa.getCabecalho().getNumeroSequencialRemessa() <= ultimoCabecalhoRetorno.getNumeroSequencialRemessa()) {
						remessa.getCabecalho().setNumeroSequencialRemessa(ultimoCabecalhoRetorno.getNumeroSequencialRemessa() + 1);
					}
				}
			}
		}
	}
}
