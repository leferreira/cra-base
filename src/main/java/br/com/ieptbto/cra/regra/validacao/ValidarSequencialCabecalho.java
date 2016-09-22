package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
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
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	@Override
	protected void executar() {
		TipoArquivoEnum tipoArquivo = getTipoArquivo(arquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {

		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
			verificarSequencialRetorno();
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			verificarSequencialRetorno();
		}
	}

	private void verificarSequencialRetorno() {
		if (arquivo.getRemessas() == null || arquivo.getRemessas().isEmpty()) {
			erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_VAZIO_OU_FORA_DO_LAYOUT_DE_TRANSMISSAO));
			return;
		}

		for (Remessa remessa : arquivo.getRemessas()) {
			CabecalhoRemessa ultimoCabecalhoRetorno = cabecalhoMediator.buscarUltimoCabecalhoRetornoPorMunicipio(remessa.getCabecalho());

			if (ultimoCabecalhoRetorno != null) {
				if (remessa.getCabecalho().getNumeroSequencialRemessa() <= ultimoCabecalhoRetorno.getNumeroSequencialRemessa()) {
					remessa.getCabecalho().setNumeroSequencialRemessa(ultimoCabecalhoRetorno.getNumeroSequencialRemessa() + 1);
				}
			}
		}
	}
}
