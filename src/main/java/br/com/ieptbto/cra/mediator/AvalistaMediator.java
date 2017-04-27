package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.AvalistaDAO;
import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class AvalistaMediator extends BaseMediator {

	@Autowired
	AvalistaDAO avalistaDAO;

	/**
	 * Removar avalista
	 * 
	 * @param avalista
	 */
	public Avalista removerAvalista(Avalista avalista) {
		return avalistaDAO.remover(avalista);
	}

	/**
	 * Busca avalistas cadastrados e vínculados por título filiado
	 * 
	 * @param titulo
	 * @return
	 */
	public List<Avalista> buscarAvalistasPorTitulo(TituloFiliado titulo) {
		return avalistaDAO.buscarAvalistasPorTitulo(titulo);
	}
}
