package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.FiliadoDAO;
import br.com.ieptbto.cra.dao.GrupoUsuarioDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.dao.TipoInstituicaoDAO;
import br.com.ieptbto.cra.dao.UsuarioDAO;
import br.com.ieptbto.cra.dao.UsuarioFiliadoDAO;
import br.com.ieptbto.cra.entidade.GrupoUsuario;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.security.CraRoles;

@Service
public class UsuarioMediator {

	private static final Logger logger = Logger.getLogger(UsuarioMediator.class);
	@Autowired 
	UsuarioDAO usuarioDao;
	@Autowired
	InstituicaoDAO instituicaoDao;
	@Autowired
	TipoInstituicaoDAO tipoInstituicaoDao;
	@Autowired
	GrupoUsuarioDAO grupoUsuarioDao;
	@Autowired
	TipoArquivoDAO tipoArquivoDao;
	@Autowired
	MunicipioMediator municipioMediator;
	@Autowired
	UsuarioFiliadoDAO usuarioFiliadoDAO;
	@Autowired
	FiliadoDAO filiadoDAO;

	public Usuario autenticarWS(String login, String senha) {
		Usuario usuario = usuarioDao.buscarUsuarioPorLogin(login);
		if (usuario != null && usuario.isSenha(senha)) {
			if (instituicaoDao.isInstituicaoAtiva(usuario.getInstituicao())) {
				if (usuario.isStatus() == true) {
					logger.info("O usuário <<" + usuario.getLogin() + ">> entrou na CRA.");
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
			if (!TipoInstituicaoCRA.CONVENIO.equals(usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao())) {
				throw new InfraException("Esse usuário não é de uma conveniada");
			}
			if (instituicaoDao.isInstituicaoAtiva(usuario.getInstituicao())) {
				if (usuario.isStatus() == true) {
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
		} else {
			UsuarioFiliado filiado = usuarioFiliadoDAO.buscarUsuarioFiliadoPorLogin(login);
			if (filiado != null && filiado.getUsuario().isSenha(senha)) {
				if (filiado.getFiliado().isAtivo()) {
					if (filiado.getUsuario().isStatus() == true) {
						logger.info("O usuário <<" + filiado.getUsuario().getLogin() + ">> entrou na CRA.");
						return filiado.getUsuario();
					} else {
						logger.error(Erro.USUARIO_INATIVO.getMensagemErro());
						throw new InfraException(Erro.USUARIO_INATIVO.getMensagemErro());
					}
				} else {
					logger.error(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
					throw new InfraException(Erro.INSTITUICAO_NAO_ATIVA.getMensagemErro());
				}
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
	 * */
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
		/*
		 * Inserindo os Tipos da Instituição
		 */
		tipoInstituicaoDao.inserirTipoInstituicaoInicial(TipoInstituicaoCRA.CRA.getConstante());
		tipoInstituicaoDao.inserirTipoInstituicaoInicial(TipoInstituicaoCRA.CARTORIO.getConstante());
		tipoInstituicaoDao.inserirTipoInstituicaoInicial(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.getConstante());
		tipoInstituicaoDao.inserirTipoInstituicaoInicial(TipoInstituicaoCRA.CONVENIO.getConstante());

		/*
		 * Inserindo os Tipos da Instituição
		 */
		tipoArquivoDao.inserirTipoArquivo("B");
		tipoArquivoDao.inserirTipoArquivo("C");
		tipoArquivoDao.inserirTipoArquivo("R");
		tipoArquivoDao.inserirTipoArquivo("CP");
		tipoArquivoDao.inserirTipoArquivo("DP");
		tipoArquivoDao.inserirTipoArquivo("AC");

		/*
		 * Inserindo os Grupos dos Usuário e as Permissões
		 */
		GrupoUsuario grupo1 = new GrupoUsuario();
		grupo1.setGrupo("Super Administrador");
		String[] roles = { CraRoles.ADMIN, CraRoles.USER, CraRoles.SUPER };
		grupo1.setRoles(new Roles(roles));
		grupoUsuarioDao.inserirGruposCargaInicial(grupo1);

		GrupoUsuario grupo2 = new GrupoUsuario();
		grupo2.setGrupo("Administrador");
		String[] roles1 = { Roles.ADMIN, CraRoles.USER };
		grupo2.setRoles(new Roles(roles1));
		grupoUsuarioDao.inserirGruposCargaInicial(grupo2);

		GrupoUsuario grupo3 = new GrupoUsuario();
		grupo3.setGrupo("Usuário");
		String[] roles2 = { CraRoles.USER };
		grupo3.setRoles(new Roles(roles2));
		grupoUsuarioDao.inserirGruposCargaInicial(grupo3);

		GrupoUsuario grupo4 = new GrupoUsuario();
		grupo4.setGrupo("Convênio");
		String[] roles4 = { Roles.ADMIN };
		grupo4.setRoles(new Roles(roles4));
		grupoUsuarioDao.inserirGruposCargaInicial(grupo4);

		/*
		 * Inserindo o usuário de teste
		 */
		usuarioDao.incluirUsuarioDeTeste();
	}

	public List<Usuario> listarTodos() {
		return usuarioDao.listarTodosUsuarios();
	}

	public boolean isUsuatioAtivo(Usuario usuario) {
		return usuario.isStatus();
	}

}
