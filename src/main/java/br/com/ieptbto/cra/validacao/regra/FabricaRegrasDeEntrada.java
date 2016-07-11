package br.com.ieptbto.cra.validacao.regra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.validacao.RegraValidarHoraEnvio;
import br.com.ieptbto.cra.validacao.RegraVerificarDuplicidade;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaRegrasDeEntrada {

	@Autowired
	private RegraValidarInstituicaoEnvio regraValidarInstituicaoEnvio;
	@Autowired
	private RegraNomeArquivo regraNomeArquivo;
	@Autowired
	private RegraValidarUsuarioEnvio regraValidarUsuarioEnvio;
	@Autowired
	private RegraVerificarDuplicidade regraValidarDuplicidade;
	@Autowired
	private RegraValidarHoraEnvio regraHoraEnvio;
	@Autowired
	private RegraNumeroSequencialRemessa regraNumeroSequencialRemessaCabecalho;

	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		regraNomeArquivo.validar(arquivo, usuario, erros);
		regraValidarInstituicaoEnvio.validar(arquivo, usuario, erros);
		regraValidarUsuarioEnvio.validar(arquivo, usuario, erros);
		regraValidarDuplicidade.validar(arquivo, usuario, erros);
		regraHoraEnvio.validar(arquivo, usuario, erros);
		regraNumeroSequencialRemessaCabecalho.validar(arquivo, usuario, erros);
	}

}
