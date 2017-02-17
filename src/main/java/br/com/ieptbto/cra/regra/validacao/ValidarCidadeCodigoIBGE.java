package br.com.ieptbto.cra.regra.validacao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.CabecalhoRodapeException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;

@Service
public class ValidarCidadeCodigoIBGE extends RegraValidacao {

	@Autowired
	private MunicipioMediator municipioMediator;

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {

		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(arquivo);
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {

		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
			verificarCodigoIBGE(arquivo, usuario, erros);
		} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			verificarCodigoIBGE(arquivo, usuario, erros);
		}
	}

	private void verificarCodigoIBGE(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		if (arquivo.getRemessas() != null && !arquivo.getRemessas().isEmpty()) {

			for (Remessa remessa : arquivo.getRemessas()) {
				if (StringUtils.isBlank(remessa.getCabecalho().getCodigoMunicipio())) {
					erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_CODIGO_MUNICIPIO_INVÁLIDO_OU_DIFERE_INSTITUICAO));
				}

				Municipio municipio = municipioMediator.buscaMunicipioPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio());
				if (municipio == null) {
					erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_CODIGO_MUNICIPIO_INVÁLIDO_OU_DIFERE_INSTITUICAO));
				}

				if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
					if (!TipoArquivoFebraban.REMESSA.equals(TipoArquivoFebraban.getTipoArquivoFebraban(arquivo))) {
						Municipio municipioEnvio = municipioMediator.carregarMunicipio(remessa.getInstituicaoOrigem().getMunicipio());
						if (!municipio.equals(municipioEnvio)) {
							erros.add(new CabecalhoRodapeException(CodigoErro.CARTORIO_CODIGO_MUNICIPIO_INVÁLIDO_OU_DIFERE_INSTITUICAO));
						}
					}
				}
			}
		}

	}

}