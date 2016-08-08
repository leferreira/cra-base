package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;

@Service
public class ValidarCidadeCodigoIBGE extends RegraValidacao {

	@Autowired
	private MunicipioMediator municipioMediator;

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
			verificarCodigoIBGE();
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			verificarCodigoIBGE();
		}
	}

	private void verificarCodigoIBGE() {
		for (Remessa remessa : arquivo.getRemessas()) {

			Municipio municipio = municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio());
			if (municipio == null) {
				throw new InfraException("Código do Município " + remessa.getCabecalho().getCodigoMunicipio() + " não encontrado ou inválido!");
			}

			if (!TipoArquivoEnum.REMESSA.equals(getTipoArquivo(arquivo))) {
				Municipio municipioEnvio = municipioMediator.carregarMunicipio(remessa.getInstituicaoOrigem().getMunicipio());
				if (!municipio.equals(municipioEnvio)) {
					throw new InfraException("Código do Município no cabeçalho não pertence a instituição que está enviando o arquivo!");
				}
			}
		}

	}

}