package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.FiliadoDAO;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class FiliadoMediator {

	@Autowired
	FiliadoDAO filiadoDAO;
	
	public Filiado salvarFiliado(Filiado filiado){
		return filiadoDAO.salvar(filiado);
	}
	
	public List<Filiado> buscarListaFiliados(Instituicao instituicao) {
		return filiadoDAO.buscarListaFiliadosPorConvenio(instituicao);
	}

}
