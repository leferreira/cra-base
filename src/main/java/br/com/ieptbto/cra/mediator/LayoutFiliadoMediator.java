package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.LayoutFiliadoDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LayoutFiliado;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class LayoutFiliadoMediator {

	@Autowired
	LayoutFiliadoDAO layoutFiliadoDAO;

	public void salvar(List<LayoutFiliado> listaCampos) {
		layoutFiliadoDAO.salvar(listaCampos);
	}

	public List<LayoutFiliado> buscarEmpresasComLayout() {
		return layoutFiliadoDAO.buscarEmpresas();
	}

	public List<LayoutFiliado> buscarLayout(Instituicao instituicao) {
		return layoutFiliadoDAO.buscarLayout(instituicao);
	}
}
