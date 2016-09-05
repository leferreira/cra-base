package br.com.ieptbto.cra.regra.titulo;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.exception.TituloException;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ValidarTituloRetorno extends RegraTitulo {

	@Autowired
	private TituloMediator tituloMediator;
	@Autowired
	private DesistenciaProtestoMediator desistenciaProtestoMediator;

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
		for (Remessa remessa : arquivo.getRemessas()) {

			if (remessa.getTitulos().isEmpty()) {
				erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_ENVIADO_SEM_TITULOS));
				return;
			}

			this.titulosProcessados = new ArrayList<Titulo>();
			for (Titulo titulo : remessa.getTitulos()) {
				Retorno tituloRetorno = Retorno.class.cast(titulo);

				verificarTipoOcorrenciaProtocoloCodigoIrregularidade(tituloRetorno);
				verificarProtestoIndevidoERetirado(titulo, tituloRetorno);
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

	private void verificarProtestoIndevidoERetirado(Titulo titulo, Retorno tituloRetorno) {
		TituloRemessa tituloRemessa = tituloMediator.buscarTituloRemessaPorDadosRetorno(tituloRetorno);
		List<PedidoDesistencia> pedidosDesistencia = desistenciaProtestoMediator.buscarPedidosDesistenciaProtestoPorTitulo(tituloRemessa);

		TipoOcorrencia tipoOcorrencia = TipoOcorrencia.getTipoOcorrencia(tituloRetorno.getTipoOcorrencia());
		if (TipoOcorrencia.PROTESTADO.equals(tipoOcorrencia)) {
			for (PedidoDesistencia pedido : pedidosDesistencia) {
				LocalDate dataOcorrenciaProtesto = tituloRetorno.getDataOcorrencia();
				LocalDate dataEnvioDesistencia = pedido.getDesistenciaProtesto().getRemessaDesistenciaProtesto().getCabecalho().getDataMovimento();
				if (dataOcorrenciaProtesto.isAfter(dataEnvioDesistencia) || dataOcorrenciaProtesto.equals(dataEnvioDesistencia)) {
					erros.add(new TituloException(CodigoErro.CARTORIO_PROTESTO_INDEVIDO, tituloRetorno.getNossoNumero(),
							tituloRetorno.getNumeroSequencialArquivo()));
				}
			}
		}

		if (TipoOcorrencia.RETIRADO.equals(tipoOcorrencia)) {
			if (pedidosDesistencia.isEmpty()) {
				titulo.setTipoOcorrencia(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante());
				titulo.setCodigoIrregularidade(CodigoIrregularidade.IRREGULARIDADE_22.getCodigoIrregularidade());
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