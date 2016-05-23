package br.com.ieptbto.cra.validacao;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.RemessaCnp;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class FabricaValidacaoCNP {

	@Autowired
	CentralNacionalProtestoMediator centralNacionalProtestoMediator;
	@Autowired
	InstituicaoMediator instituicaoMediator;

	private ArquivoCnp arquivo;

	public void validarArquivoCnpCartorio(ArquivoCnp arquivoCnp) {
		this.arquivo = arquivoCnp;
		validar();
	}

	private void validar() {
		validarSequencialArquivoComarca();

	}

	private void validarSequencialArquivoComarca() {
		for (RemessaCnp remessaCnp : getArquivo().getRemessasCnp()) {
			if (StringUtils.isEmpty(remessaCnp.getCabecalho().getEmBranco53().trim())
					|| StringUtils.isBlank(remessaCnp.getCabecalho().getEmBranco53().trim())) {
				throw new InfraException(
						"O campo 'emBranco53' não pode ser vazio! Neste campo deverá ser enviado o Código IBGE do município.");
			}
			if (instituicaoMediator.getCartorioPorCodigoIBGE(remessaCnp.getCabecalho().getEmBranco53()) == null) {
				throw new InfraException("Código da comarca " + remessaCnp.getCabecalho().getEmBranco53() + " inválido !");
			}

			int sequencialRemessa = centralNacionalProtestoMediator.buscarSequencialCabecalhoCnp(remessaCnp.getCabecalho().getEmBranco53());
			if (StringUtils.isEmpty(remessaCnp.getCabecalho().getNumeroRemessaArquivo().trim())
					|| StringUtils.isBlank(remessaCnp.getCabecalho().getNumeroRemessaArquivo().trim())) {
				throw new InfraException(
						"Número de Sequêncial do Arquivo não pode ser vazio ! Último sequencial correto foi " + sequencialRemessa + ".");
			}
			sequencialRemessa++;
			if (sequencialRemessa != Integer.valueOf(remessaCnp.getCabecalho().getNumeroRemessaArquivo())) {
				throw new InfraException("Número de Sequêncial " + remessaCnp.getCabecalho().getNumeroRemessaArquivo()
						+ " da CNP é inválido. Último sequencial correto foi " + (sequencialRemessa - 1) + ".");
			}
			remessaCnp.getCabecalho().setEmBranco2("01");
		}
	}

	public ArquivoCnp getArquivo() {
		return arquivo;
	}
}
