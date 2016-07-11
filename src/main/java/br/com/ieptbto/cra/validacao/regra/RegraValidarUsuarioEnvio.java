package br.com.ieptbto.cra.validacao.regra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.UsuarioMediator;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraValidarUsuarioEnvio extends RegrasDeEntrada {

	@Autowired
	UsuarioMediator usuarioMediator;
	@Autowired
	InstituicaoMediator instituicaoMediator;

	private Arquivo arquivo;
	private Usuario usuario;

	protected void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		setArquivo(arquivo);
		setUsuario(usuario);
		setErros(erros);

		executar();
	}

	/**
	 * Verifica se o usuário tem permissão para enviar o arquivo.
	 */
	private void verificarPermissaoDoUsuario() {
		if (!usuarioMediator.isUsuarioAtivo(getUsuario())) {
			throw new InfraException(Erro.USUARIO_INATIVO.getMensagemErro());
		}
	}

	/**
	 * Verifica se a instituição do usuário de envio tem permissão para envio do
	 * arquivo.
	 */
	private void verificaInstituicaoDoUsuario() {
		if (instituicaoMediator.isInstituicaoNaoExiste(getUsuario().getInstituicao())) {
			throw new InfraException(Erro.INSTITUICAO_NAO_CADASTRADA.getMensagemErro());
		}

		if (!instituicaoMediator.isInstituicaoAtiva(getUsuario().getInstituicao())) {
			throw new InfraException(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
		}
	}

	private void verificarHorarioDeEnvio() {

	}

	@Override
	protected void executar() {
		verificaInstituicaoDoUsuario();
		verificarPermissaoDoUsuario();
		verificarHorarioDeEnvio();
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
