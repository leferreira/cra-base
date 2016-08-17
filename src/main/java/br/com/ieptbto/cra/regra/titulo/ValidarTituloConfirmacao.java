package br.com.ieptbto.cra.regra.titulo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.exception.TituloException;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ValidarTituloConfirmacao extends RegraTitulo {

	private List<Titulo> titulosProcessados;

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;
		this.titulosProcessados = new ArrayList<Titulo>();

		executar();
	}

	@Override
	protected void executar() {
		for (Remessa remessa : arquivo.getRemessas()) {

			if (remessa.getTitulos().isEmpty()) {
				erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_ENVIADO_SEM_TITULOS));
				return;
			}

			for (Titulo titulo : remessa.getTitulos()) {
				Confirmacao tituloConfirmacao = Confirmacao.class.cast(titulo);

				verificarTipoOcorrenciaProtocoloCodigoIrregularidade(tituloConfirmacao);
				verificarDuplicidadeDeTitulosNoArquivo(tituloConfirmacao);
			}
		}
	}

	private void verificarTipoOcorrenciaProtocoloCodigoIrregularidade(Confirmacao tituloConfirmacao) {
		Integer numeroProtocoloCartorio = 0;
		if (tituloConfirmacao.getNumeroProtocoloCartorio() != null) {
			numeroProtocoloCartorio = Integer.valueOf(tituloConfirmacao.getNumeroProtocoloCartorio().trim());
		}

		TipoOcorrencia tipoOcorrencia = null;
		if (tituloConfirmacao.getTipoOcorrencia() != null) {
			tipoOcorrencia = TipoOcorrencia.getTipoOcorrencia(tituloConfirmacao.getTipoOcorrencia());
			if (numeroProtocoloCartorio.equals(ZERO)) {
				if (tipoOcorrencia == null) {
					erros.add(new TituloException(CodigoErro.CARTORIO_TIPO_OCORRENCIA_INVALIDO, tituloConfirmacao.getNossoNumero(),
							tituloConfirmacao.getNumeroSequencialArquivo()));
				}
			}
		}

		CodigoIrregularidade codigoIrregularidade = null;
		if (tituloConfirmacao.getCodigoIrregularidade() != null) {
			codigoIrregularidade = CodigoIrregularidade.getIrregularidade(tituloConfirmacao.getCodigoIrregularidade());
			if (tipoOcorrencia != null) {
				if (TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.equals(tipoOcorrencia)) {
					if (codigoIrregularidade == null || codigoIrregularidade == CodigoIrregularidade.IRREGULARIDADE_0) {
						erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_DEVOLVIDO_SEM_CODIGO_IRREGULARIDADE,
								tituloConfirmacao.getNossoNumero(), tituloConfirmacao.getNumeroSequencialArquivo()));
					}
				}
			} else if (numeroProtocoloCartorio.equals(ZERO)) {
				if (!TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.equals(tipoOcorrencia)) {
					erros.add(new TituloException(CodigoErro.CARTORIO_PROTOCOLO_VAZIO_SEM_OCORRENCIA_DE_DEVOLUCAO, tituloConfirmacao.getNossoNumero(),
							tituloConfirmacao.getNumeroSequencialArquivo()));
				}
				if (codigoIrregularidade == null || codigoIrregularidade == CodigoIrregularidade.IRREGULARIDADE_0) {
					erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_DEVOLVIDO_SEM_CODIGO_IRREGULARIDADE, tituloConfirmacao.getNossoNumero(),
							tituloConfirmacao.getNumeroSequencialArquivo()));
				}
			}
		}

	}

	private void verificarDuplicidadeDeTitulosNoArquivo(Confirmacao tituloConfirmacao) {
		if (titulosProcessados.contains(tituloConfirmacao)) {
			erros.add(new TituloException(CodigoErro.CARTORIO_TITULOS_DUPLICADOS_NO_ARQUIVO, tituloConfirmacao.getNossoNumero(),
					tituloConfirmacao.getNumeroSequencialArquivo()));
		} else {
			titulosProcessados.add(tituloConfirmacao);
		}
	}
}