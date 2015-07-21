package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.AvalistaDAO;
import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.TituloFiliado;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class AvalistaMediator {

	@Autowired
	AvalistaDAO avalistaDAO;

	public void removerAvalista(Avalista avalista) {
		avalistaDAO.remover(avalista);
	}
	
	public List<Avalista> buscarAvalistasPorTitulo(TituloFiliado titulo) {
		return avalistaDAO.buscarAvalistasPorTitulo(titulo);
	}
}
