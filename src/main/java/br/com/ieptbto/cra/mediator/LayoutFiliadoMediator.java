package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.LayoutFiliadoDao;
import br.com.ieptbto.cra.entidade.LayoutFiliado;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class LayoutFiliadoMediator {

	@Autowired
	LayoutFiliadoDao layoutFiliadoDAO;

	public void salvar(List<LayoutFiliado> listaCampos) {
		layoutFiliadoDAO.salvar(listaCampos);
	}
}