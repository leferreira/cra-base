package br.com.ieptbto.cra.regra.titulo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
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
public class ValidarTituloRetorno extends RegraTitulo {

	private List<Titulo> titulosProcessados;

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;
		this.titulosProcessados = null;

		executar();
	}

	@Override
	protected void executar() {
		if (arquivo.getRemessas() == null || arquivo.getRemessas().isEmpty()) {
			erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_VAZIO_OU_FORA_DO_LAYOUT_DE_TRANSMISSAO));
			return;
		}

		for (Remessa remessa : arquivo.getRemessas()) {

			if (remessa.getTitulos().isEmpty()) {
				erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_ENVIADO_SEM_TITULOS));
				return;
			}

			this.titulosProcessados = new ArrayList<Titulo>();
			for (Titulo titulo : remessa.getTitulos()) {
				Retorno tituloRetorno = Retorno.class.cast(titulo);

				verificarTipoOcorrenciaProtocoloCodigoIrregularidade(tituloRetorno);
				verificarDuplicidadeDeTitulosNoArquivo(tituloRetorno);
			}
		}
	}

	private void verificarTipoOcorrenciaProtocoloCodigoIrregularidade(Retorno tituloRetorno) {
		TipoOcorrencia tipoOcorrencia = null;
		if (tituloRetorno.getTipoOcorrencia() != null) {
			tipoOcorrencia = TipoOcorrencia.getTipoOcorrencia(tituloRetorno.getTipoOcorrencia());
			if (tipoOcorrencia == null) {
				erros.add(new TituloException(CodigoErro.CARTORIO_TIPO_OCORRENCIA_INVALIDO, tituloRetorno.getNossoNumero(),
						tituloRetorno.getNumeroSequencialArquivo()));
			}
		}

		CodigoIrregularidade codigoIrregularidade = null;
		if (tituloRetorno.getCodigoIrregularidade() != null) {
			codigoIrregularidade = CodigoIrregularidade.getIrregularidade(tituloRetorno.getCodigoIrregularidade());
			if (TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.equals(tipoOcorrencia)
					|| TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_COM_CUSTAS.equals(tipoOcorrencia)) {
				if (codigoIrregularidade == null || codigoIrregularidade == CodigoIrregularidade.IRREGULARIDADE_0) {
					erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_DEVOLVIDO_SEM_CODIGO_IRREGULARIDADE, tituloRetorno.getNossoNumero(),
							tituloRetorno.getNumeroSequencialArquivo()));
				}
			}
		}
	}

	private void verificarDuplicidadeDeTitulosNoArquivo(Retorno tituloRetorno) {
		if (titulosProcessados.contains(tituloRetorno)) {
			erros.add(new TituloException(CodigoErro.CARTORIO_TITULOS_DUPLICADOS_NO_ARQUIVO, tituloRetorno.getNossoNumero(),
					tituloRetorno.getNumeroSequencialArquivo()));
		} else {
			titulosProcessados.add(tituloRetorno);
		}
	}
}