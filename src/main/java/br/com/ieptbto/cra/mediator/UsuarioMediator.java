package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.UsuarioDAO;
import br.com.ieptbto.cra.dao.UsuarioFiliadoDAO;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioMediator extends BaseMediator {

	@Autowired
	UsuarioDAO usuarioDao;
	@Autowired
	InstituicaoDAO instituicaoDao;
	@Autowired
	UsuarioFiliadoDAO usuarioFiliadoDAO;

	public Usuario buscarUsuarioPorPK(Usuario usuario) {
		return usuarioDao.buscarPorPK(usuario);
	}

	public Usuario autenticarWS(String login, String senha) {
		Usuario usuario = usuarioDao.buscarUsuarioPorLogin(login);
		if (usuario != null && usuario.isSenha(senha)) {
			if (instituicaoDao.isInstituicaoAtiva(usuario.getInstituicao())) {
				if (usuario.isStatus() == true) {
					logger.info("O usuário <<" + usuario.getLogin() + ">> acessou o WS.");
					return usuario;
				} else {
					logger.error(Erro.USUARIO_INATIVO.getMensagemErro());
					new InfraException(Erro.USUARIO_INATIVO.getMensagemErro());
				}
			} else {
				logger.error(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
				new InfraException(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
			}
		}
		new InfraException("Login ou senha inválido(s) ou não ativo.");
		return null;
	}

	public Usuario autenticar(String login, String senha) {
		Usuario usuario = usuarioDao.buscarUsuarioPorLogin(login);
		
		if (usuario != null && usuario.isSenha(senha)) {
			if (instituicaoDao.isInstituicaoAtiva(usuario.getInstituicao())) {
				if (usuario.isStatus() == true) {
					TipoInstituicaoCRA tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
					if (tipoInstituicao == TipoInstituicaoCRA.CONVENIO) {
						throw new InfraException(Erro.USUARIO_CONVENIO.getMensagemErro());
					}
					logger.info("O usuário <<" + usuario.getLogin() + ">> entrou na CRA.");
					return usuario;
				} else {
					logger.error(Erro.USUARIO_INATIVO.getMensagemErro());
					throw new InfraException(Erro.USUARIO_INATIVO.getMensagemErro());
				}
			} else {
				logger.error(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
				throw new InfraException(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
			}
		}
		throw new InfraException("Login ou senha inválido(s) ou não ativo.");
	}

	public Usuario autenticarConvenio(String login, String senha) {
		Usuario usuario = usuarioDao.buscarUsuarioPorLogin(login);
		
		if (usuario != null && usuario.isSenha(senha)) {
			if (instituicaoDao.isInstituicaoAtiva(usuario.getInstituicao())) {
				if (usuario.isStatus() == true) {
					TipoInstituicaoCRA tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
					if (tipoInstituicao != TipoInstituicaoCRA.CONVENIO) {
						throw new InfraException(Erro.USUARIO_CRA.getMensagemErro());
					}
					logger.info("O usuário <<" + usuario.getLogin() + ">> entrou na IEPTB-Convênio.");
					return usuario;
				} else {
					logger.error(Erro.USUARIO_INATIVO.getMensagemErro());
					throw new InfraException(Erro.USUARIO_INATIVO.getMensagemErro());
				}
			} else {
				logger.error(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
				throw new InfraException(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
			}
		}
		throw new InfraException("Login ou senha inválido(s) ou não ativo.");
	}

	public Usuario alterar(Usuario usuario) {
		return usuarioDao.alterar(usuario);
	}

	public Usuario salvar(Usuario usuario) {
		if (isLoginNaoExiste(usuario)) {
			return usuarioDao.criarUsuario(usuario);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<Usuario> buscarUsuario(String login, String nome) {
		return usuarioDao.buscarUsuario(login, nome);
	}

	/**
	 * Verificar se as senha e a confirmação da senha coincidem
	 * 
	 * @param usuario
	 * @return
	 */
	public boolean isSenhasIguais(Usuario usuario) {
		if (usuario.getSenha().equals(usuario.getConfirmarSenha())) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica se o login que será criado não existe no sistema
	 * 
	 * @param usuario
	 * @return
	 */
	public boolean isLoginNaoExiste(Usuario usuario) {
		Usuario usuarioPesquisado = usuarioDao.buscarUsuarioPorLogin(usuario.getLogin());
		if (usuarioPesquisado == null) {
			return true;
		}
		return false;
	}

	public Usuario trocarSenha(Usuario usuario) {
		return usuarioDao.trocarSenha(usuario);
	}

	public void cargaInicial() {
		usuarioDao.incluirUsuarioDeTeste();
	}

	public List<Usuario> listarTodos() {
		return usuarioDao.listarTodosUsuarios();
	}

	public boolean isUsuarioAtivo(Usuario usuario) {
		return usuario.isStatus();
	}
}