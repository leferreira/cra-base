package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.UsuarioFiliadoDAO;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class UsuarioFiliadoMediator {

	@Autowired
	private UsuarioFiliadoDAO usuarioFiliadoDAO;

	public UsuarioFiliado salvarUsuarioFiliado(UsuarioFiliado usuarioFiliado) {
		return usuarioFiliadoDAO.salvar(usuarioFiliado);
	}

	public UsuarioFiliado alterarUsuarioFiliado(UsuarioFiliado usuarioFiliado) {
		return usuarioFiliadoDAO.alterar(usuarioFiliado);
	}

	public List<UsuarioFiliado> buscarUsuariosDoConvenio(Usuario usuario) {
		return usuarioFiliadoDAO.listarUsuariosDoConvenio(usuario);
	}

	public Filiado buscarEmpresaFiliadaDoUsuario(Usuario user) {
		return usuarioFiliadoDAO.buscarEmpresaFiliadaDoUsuario(user);
	}

	public UsuarioFiliado buscarUsuarioFiliado(Usuario usuario) {
		return usuarioFiliadoDAO.buscarUsuarioFiliado(usuario);
	}

	public UsuarioFiliado confirmarAceiteTermosContrato(Usuario usuario) {
		UsuarioFiliado usuarioFiliado = usuarioFiliadoDAO.buscarUsuarioFiliado(usuario);
		return usuarioFiliadoDAO.confirmarAceiteTermosContrato(usuarioFiliado);
	}

	public UsuarioFiliado naoAceiteTermosContrato(Usuario usuario) {
		UsuarioFiliado usuarioFiliado = usuarioFiliadoDAO.buscarUsuarioFiliado(usuario);
		return usuarioFiliadoDAO.naoAceiteTermosContrato(usuarioFiliado);
	}
}
