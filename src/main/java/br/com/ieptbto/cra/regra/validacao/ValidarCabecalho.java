package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ValidarCabecalho extends RegraValidacao {

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	@Override
	protected void executar() {
		TipoArquivoEnum tipoArquivo = getTipoArquivo(arquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
			validarQuantidadeRegistrosArquivo();
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
			verificarCamposEmBrancoEQuantidadeDeRegistros();
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			verificarCamposEmBrancoEQuantidadeDeRegistros();
		}
	}

	private void validarQuantidadeRegistrosArquivo() {

		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {

			for (Remessa remessa : arquivo.getRemessas()) {
				if (remessa.getCabecalho().getQtdRegistrosRemessa() != remessa.getTitulos().size()) {
					addErro(new CabecalhoRodapeException(
							CodigoErro.CRA_ARQUIVO_CORROMPIDO_SOMA_DE_REGISTROS_DE_TRANSACAO_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER,
							remessa.getCabecalho().getCodigoMunicipio()));
				}
			}
		}
	}

	private void verificarCamposEmBrancoEQuantidadeDeRegistros() {
		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {

			for (Remessa remessa : arquivo.getRemessas()) {

				if (remessa.getCabecalho().getNumeroCodigoPortador() != null) {
					if (remessa.getCabecalho().getNumeroCodigoPortador().trim().isEmpty()) {
						addErro(new CabecalhoRodapeException(CodigoErro.CARTORIO_CODIGO_PORTADOR_CABECALHO_INVALIDO));
					}
				}
				if (remessa.getCabecalho().getNomePortador() != null) {
					if (remessa.getCabecalho().getNomePortador().trim().isEmpty()) {
						addErro(new CabecalhoRodapeException(CodigoErro.CARTORIO_NOME_PORTADOR_CABECALHO_INVALIDO));
					}
				}
				if (remessa.getCabecalho().getQtdRegistrosRemessa() != remessa.getTitulos().size()) {
					remessa.getCabecalho().setQtdRegistrosRemessa(remessa.getTitulos().size());
				}
			}
		}

	}
}
