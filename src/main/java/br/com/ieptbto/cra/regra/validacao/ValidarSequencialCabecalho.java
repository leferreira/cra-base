package br.com.ieptbto.cra.regra.validacao;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.mediator.CabecalhoMediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(arquivo);
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {

		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {

		} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
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
