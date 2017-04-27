package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.GrupoUsuarioDAO;
import br.com.ieptbto.cra.entidade.GrupoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoUsuarioMediator {

	@Autowired
	private GrupoUsuarioDAO grupoUsuarioDAO;

	public GrupoUsuario buscarGrupoInicial(String grupo) {
		return grupoUsuarioDAO.buscarGrupoInicial(grupo);
	}

	public GrupoUsuario buscarGrupo(String grupo) {
		return grupoUsuarioDAO.buscarGrupo(grupo);
	}

	public List<GrupoUsuario> listaDeGrupos() {
		return grupoUsuarioDAO.listarGrupos();
	}
}
