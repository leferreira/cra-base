package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.UsuarioFiliadoDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
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
	
	public UsuarioFiliado salvarUsuarioFiliado(Usuario user, UsuarioFiliado usuarioFiliado){
		return usuarioFiliadoDAO.salvar(user, usuarioFiliado);
	}
	
	public UsuarioFiliado alterarUsuarioFiliado(Usuario user, UsuarioFiliado usuarioFiliado){
		return usuarioFiliadoDAO.alterar(user, usuarioFiliado);
	}
	
	public List<UsuarioFiliado> buscarUsuariosDoConvenio(Instituicao convenio) {
		return usuarioFiliadoDAO.listarUsuariosDoConvenio(convenio);
	}
}
