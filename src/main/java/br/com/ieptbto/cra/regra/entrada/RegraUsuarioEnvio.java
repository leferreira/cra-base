package br.com.ieptbto.cra.regra.entrada;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraUsuarioEnvio extends RegraEntrada {

	@Autowired
	private UsuarioMediator usuarioMediator;
	@Autowired
	private InstituicaoMediator instituicaoMediator;

	@Override
	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.file = file;
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	@Override
	protected void executar() {
		verificaInstituicaoDoUsuario();
		verificarPermissaoDoUsuario();
		verificarHorarioDeEnvio();
	}

	/**
	 * Verifica se o usuário tem permissão para enviar o arquivo.
	 */
	private void verificarPermissaoDoUsuario() {
		if (!usuarioMediator.isUsuarioAtivo(usuario)) {
			throw new InfraException(Erro.USUARIO_INATIVO.getMensagemErro());
		}
	}

	/**
	 * Verifica se a instituição do usuário de envio tem permissão para envio do
	 * arquivo.
	 */
	private void verificaInstituicaoDoUsuario() {
		if (instituicaoMediator.isInstituicaoNaoExiste(usuario.getInstituicao())) {
			throw new InfraException(Erro.INSTITUICAO_NAO_CADASTRADA.getMensagemErro());
		}

		if (!instituicaoMediator.isInstituicaoAtiva(usuario.getInstituicao())) {
			throw new InfraException(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
		}
		
		TipoInstituicaoCRA tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (!arquivo.getTipoArquivo().getTipoArquivo().isPermitidoEnvioParaTipoInstituicao(tipoInstituicao)) {
			throw new InfraException(Erro.USUARIO_SEM_PERMISSAO_DE_ENVIO_DE_ARQUIVO.getMensagemErro());
		}
	}

	private void verificarHorarioDeEnvio() {

	}
}