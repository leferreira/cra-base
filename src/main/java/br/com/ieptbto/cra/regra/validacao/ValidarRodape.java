package br.com.ieptbto.cra.regra.validacao;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidarRodape extends RegraValidacao {

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {

		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(arquivo);
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {

		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
			validarDadosRodape(arquivo, usuario, erros);
		} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			validarDadosRodape(arquivo, usuario, erros);
		}
	}

	private void validarDadosRodape(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {

			for (Remessa remessa : arquivo.getRemessas()) {
				if (remessa.getRodape().getNumeroCodigoPortador() != null) {
					if (remessa.getRodape().getNumeroCodigoPortador().trim().isEmpty()) {
						erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_CODIGO_PORTADOR_RODAPE_INVALIDO));
					}
				}

				if (remessa.getRodape().getNomePortador() != null) {
					if (remessa.getRodape().getNomePortador().trim().isEmpty()) {
						erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_NOME_PORTADOR_RODAPE_INVALIDO));
					}
				}
			}
		}
	}
}