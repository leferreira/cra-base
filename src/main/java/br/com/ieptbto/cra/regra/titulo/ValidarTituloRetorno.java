package br.com.ieptbto.cra.regra.titulo;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.exception.TituloException;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ValidarTituloRetorno extends RegraTitulo {

	private static final String SEFAZ = "801";

	@SpringBean
	DesistenciaProtestoMediator desistenciaMediator;
	
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
			erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_VAZIO_OU_FORA_DO_LAYOUT_DE_TRANSMISSAO));
			return;
		}

		for (Remessa remessa : arquivo.getRemessas()) {
			if (remessa.getTitulos() == null || remessa.getTitulos().isEmpty()) {
				erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_ENVIADO_SEM_TITULOS));
			}

			List<Titulo> titulosProcessados = new ArrayList<Titulo>();
			for (Titulo titulo : remessa.getTitulos()) {
				if (Retorno.class.isInstance(titulo)) {
					Retorno tituloRetorno = Retorno.class.cast(titulo);
                    TipoOcorrencia tipoOcorrencia = TipoOcorrencia.get(tituloRetorno.getTipoOcorrencia());

                    verificarPagoOuCancelado(remessa, tituloRetorno, tipoOcorrencia);
					verificarTipoOcorrenciaProtocoloCodigoIrregularidade(tituloRetorno, tipoOcorrencia);
					verificarDuplicidadeDeTitulosNoArquivo(titulosProcessados, tituloRetorno);
				}
			}
		}
	}

    /**
     * Verifica se o título é pago ou cancelado. Caso seja pago sera definida a regra do batimento da entidade remessa. Caso seja cancelado
     * será atribuido true a flag de remessa de titulos cancelamento
     *
     * @param remessa
     * @param tituloRetorno
     * @param tipoOcorrencia
     */
    private void verificarPagoOuCancelado(Remessa remessa, Retorno tituloRetorno, TipoOcorrencia tipoOcorrencia) {

        if (tipoOcorrencia == null) {
            erros.add(new TituloException(CodigoErro.CARTORIO_TIPO_OCORRENCIA_INVALIDO, tituloRetorno.getNossoNumero(),
                    getProtocolo(tituloRetorno), tituloRetorno.getNumeroSequencialArquivo()));

        } else if (tipoOcorrencia.equals(TipoOcorrencia.PAGO) && remessa.getContemPago().equals(false)) {
            TipoBatimento tipoBatimento = remessa.getInstituicaoDestino().getTipoBatimento();
            if (!tipoBatimento.equals(TipoBatimento.LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO)) {
                remessa.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.NAO_CONFIRMADO);
            }
            remessa.setContemPago(true);

        } else if (tipoOcorrencia.equals(TipoOcorrencia.PROTESTO_DO_BANCO_CANCELADO) && remessa.getContemCancelamento().equals(false)) {
            remessa.setContemCancelamento(true);
        }
    }

    private void verificarTipoOcorrenciaProtocoloCodigoIrregularidade(Retorno tituloRetorno, TipoOcorrencia tipoOcorrencia) {
		CodigoIrregularidade codigoIrregularidade = null;
		if (tituloRetorno.getCodigoIrregularidade() != null) {
			codigoIrregularidade = CodigoIrregularidade.getIrregularidade(tituloRetorno.getCodigoIrregularidade());
			if (TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.equals(tipoOcorrencia)
					|| TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_COM_CUSTAS.equals(tipoOcorrencia)) {
				if (codigoIrregularidade == null || codigoIrregularidade == CodigoIrregularidade.IRREGULARIDADE_0) {
					if (tituloRetorno.getCodigoPortador().equals(SEFAZ)) {
						tituloRetorno.setCodigoIrregularidade("67");
					} else {
						erros.add(new TituloException(CodigoErro.CARTORIO_TITULO_DEVOLVIDO_SEM_CODIGO_IRREGULARIDADE, tituloRetorno.getNossoNumero(),
								getProtocolo(tituloRetorno), tituloRetorno.getNumeroSequencialArquivo()));
					}
				}
			}
		}
	}

	private void verificarDuplicidadeDeTitulosNoArquivo(List<Titulo> titulosProcessados, Retorno tituloRetorno) {
		if (titulosProcessados.contains(tituloRetorno)) {
			erros.add(new TituloException(CodigoErro.CARTORIO_TITULOS_DUPLICADOS_NO_ARQUIVO, tituloRetorno.getNossoNumero(), 
					getProtocolo(tituloRetorno), tituloRetorno.getNumeroSequencialArquivo()));
		} else {
			titulosProcessados.add(tituloRetorno);
		}
	}

    /**
     * Retorna o protocoloco como inteiro
     * @param tituloRetorno
     * @return
     */
    private Integer getProtocolo(Retorno tituloRetorno) {
        return Integer.valueOf(tituloRetorno.getNumeroProtocoloCartorio().trim());
    }
}