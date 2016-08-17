package br.com.ieptbto.cra.regra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.regra.titulo.ValidarTituloConfirmacao;
import br.com.ieptbto.cra.regra.titulo.ValidarTituloRemessa;
import br.com.ieptbto.cra.regra.titulo.ValidarTituloRetorno;

@Service
public class FabricaValidacaoTitulo {

	@Autowired
	private ValidarTituloRemessa validarTituloRemessa;
	@Autowired
	private ValidarTituloConfirmacao validarTituloConfirmacao;
	@Autowired
	private ValidarTituloRetorno validarTituloRetorno;

	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo);

		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
			validarTituloRemessa.validar(arquivo, usuario, erros);
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
			validarTituloConfirmacao.validar(arquivo, usuario, erros);
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			validarTituloRetorno.validar(arquivo, usuario, erros);
		}
	}
}
