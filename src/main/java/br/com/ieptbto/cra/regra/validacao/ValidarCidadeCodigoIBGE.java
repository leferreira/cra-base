package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
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
		if (arquivo.getRemessas() == null || arquivo.getRemessas().isEmpty()) {
			erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_ARQUIVO_VAZIO_OU_FORA_DO_LAYOUT_DE_TRANSMISSAO));
			return;
		}

		for (Remessa remessa : arquivo.getRemessas()) {

			Municipio municipio = municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio());
			if (municipio == null) {
				addErro(new CabecalhoRodapeException(CodigoErro.CARTORIO_CODIGO_MUNICIPIO_INVÁLIDO_OU_DIFERE_INSTITUICAO));
			}

			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				if (!TipoArquivoEnum.REMESSA.equals(getTipoArquivo(arquivo))) {
					Municipio municipioEnvio = municipioMediator.carregarMunicipio(remessa.getInstituicaoOrigem().getMunicipio());
					if (!municipio.equals(municipioEnvio)) {
						addErro(new CabecalhoRodapeException(CodigoErro.CARTORIO_CODIGO_MUNICIPIO_INVÁLIDO_OU_DIFERE_INSTITUICAO));
					}
				}
			}
		}

	}

}