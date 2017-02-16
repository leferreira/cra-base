package br.com.ieptbto.cra.regra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
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
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(arquivo);

		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
			validarTituloRemessa.validar(arquivo, usuario, erros);
		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
			validarTituloConfirmacao.validar(arquivo, usuario, erros);
		} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			validarTituloRetorno.validar(arquivo, usuario, erros);
		}
	}
}
