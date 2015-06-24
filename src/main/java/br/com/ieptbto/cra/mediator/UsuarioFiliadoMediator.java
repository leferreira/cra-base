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
	UsuarioFiliadoDAO usuarioFiliadoDAO;

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
}
