package br.com.ieptbto.cra.regra.validacao;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ValidarCabecalho extends RegraValidacao {

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {

		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(arquivo);
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
			validarQuantidadeRegistrosArquivo(arquivo, usuario, erros);
		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
			verificarCamposEmBrancoEQuantidadeDeRegistros(arquivo, usuario, erros);
		} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			verificarCamposEmBrancoEQuantidadeDeRegistros(arquivo, usuario, erros);
		}
	}

	private void validarQuantidadeRegistrosArquivo(Arquivo arquivo, Usuario usuario, List<Exception> erros) {

		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {

			for (Remessa remessa : arquivo.getRemessas()) {
				if (remessa.getCabecalho().getQtdRegistrosRemessa() != remessa.getTitulos().size()) {
					erros.add(new CabecalhoRodapeException(
							CodigoErro.CRA_ARQUIVO_CORROMPIDO_SOMA_DE_REGISTROS_DE_TRANSACAO_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER,
							remessa.getCabecalho().getCodigoMunicipio()));
				}
			}
		}
	}

	private void verificarCamposEmBrancoEQuantidadeDeRegistros(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {

			for (Remessa remessa : arquivo.getRemessas()) {

				if (remessa.getCabecalho().getNumeroCodigoPortador() != null) {
					if (remessa.getCabecalho().getNumeroCodigoPortador().trim().isEmpty()) {
						erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_CODIGO_PORTADOR_CABECALHO_INVALIDO));
					}
				}
				if (remessa.getCabecalho().getNomePortador() != null) {
					if (remessa.getCabecalho().getNomePortador().trim().isEmpty()) {
						erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_NOME_PORTADOR_CABECALHO_INVALIDO));
					}
				}
				if (remessa.getCabecalho().getQtdRegistrosRemessa() != remessa.getTitulos().size()) {
					remessa.getCabecalho().setQtdRegistrosRemessa(remessa.getTitulos().size());
				}
			}
		}

	}
}
