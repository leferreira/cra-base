package br.com.ieptbto.cra.regra.titulo;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.exception.TituloException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ValidarTituloConfirmacao extends RegraTitulo {

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	@Override
	protected void executar() {
		if (arquivo.getRemessas() == null || arquivo.getRemessas().isEmpty()) {
			this.erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_VAZIO_OU_FORA_DO_LAYOUT_DE_TRANSMISSAO));
			return;
		}

		for (Remessa remessa : arquivo.getRemessas()) {
			if (remessa.getTitulos() == null || remessa.getTitulos().isEmpty()) {
				this.erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_ENVIADO_SEM_TITULOS));
			}

			List<Titulo> titulosProcessados = new ArrayList<Titulo>();
			for (Titulo titulo : remessa.getTitulos()) {
				if (Confirmacao.class.isInstance(titulo)) {
					Confirmacao tituloConfirmacao = Confirmacao.class.cast(titulo);
					
					verificarTipoOcorrenciaProtocoloCodigoIrregularidade(tituloConfirmacao);
					verificarDuplicidadeDeTitulosNoArquivo(titulosProcessados, tituloConfirmacao);
				}
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
			tipoOcorrencia = TipoOcorrencia.get(tituloConfirmacao.getTipoOcorrencia());
			if (numeroProtocoloCartorio.equals(ZERO)) {
				if (tipoOcorrencia == null) {
					this.erros.add(new TituloException(CodigoErro.CARTORIO_TIPO_OCORRENCIA_INVALIDO, tituloConfirmacao.getNossoNumero(), 
							numeroProtocoloCartorio, tituloConfirmacao.getNumeroSequencialArquivo()));
				}
			}
		}

		CodigoIrregularidade codigoIrregularidade = null;
		if (tituloConfirmacao.getCodigoIrregularidade() != null) {
			codigoIrregularidade = CodigoIrregularidade.getIrregularidade(tituloConfirmacao.getCodigoIrregularidade());
			if (tipoOcorrencia != null) {
				if (TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.equals(tipoOcorrencia)) {
					if (codigoIrregularidade == null || codigoIrregularidade == CodigoIrregularidade.IRREGULARIDADE_0) {
						this.erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_DEVOLVIDO_SEM_CODIGO_IRREGULARIDADE, tituloConfirmacao.getNossoNumero(),
								numeroProtocoloCartorio, tituloConfirmacao.getNumeroSequencialArquivo()));
					}
				}
			} else if (numeroProtocoloCartorio.equals(ZERO)) {
				if (!TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.equals(tipoOcorrencia)) {
					this.erros.add(new TituloException(CodigoErro.CARTORIO_PROTOCOLO_VAZIO_SEM_OCORRENCIA_DE_DEVOLUCAO, tituloConfirmacao.getNossoNumero(),
							numeroProtocoloCartorio, tituloConfirmacao.getNumeroSequencialArquivo()));
				}
				if (codigoIrregularidade == null || codigoIrregularidade == CodigoIrregularidade.IRREGULARIDADE_0) {
					this.erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_DEVOLVIDO_SEM_CODIGO_IRREGULARIDADE, tituloConfirmacao.getNossoNumero(),
							numeroProtocoloCartorio, tituloConfirmacao.getNumeroSequencialArquivo()));
				}
			}
		}

	}

	private void verificarDuplicidadeDeTitulosNoArquivo(List<Titulo> titulosProcessados, Confirmacao tituloConfirmacao) {
		Integer numeroProtocoloCartorio = Integer.valueOf(tituloConfirmacao.getNumeroProtocoloCartorio().trim());
		if (titulosProcessados.contains(tituloConfirmacao)) {
			erros.add(new TituloException(CodigoErro.CARTORIO_TITULOS_DUPLICADOS_NO_ARQUIVO, tituloConfirmacao.getNossoNumero(), 
					numeroProtocoloCartorio, tituloConfirmacao.getNumeroSequencialArquivo()));
		} else {
			titulosProcessados.add(tituloConfirmacao);
		}
	}
}