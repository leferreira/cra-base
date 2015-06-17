package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloFiliadoDAO;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ConvenioMediator {

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@Autowired
	TituloFiliadoDAO tituloFiliadoDAO;
	
	public List<TituloFiliado> buscarTitulosConvenios() {
		return tituloFiliadoDAO.buscarTitulosConvenios();
	}

	public void gerarRemessas(Usuario usuario,List<TituloFiliado> listaTitulosConvenios) {
		
	}
}
