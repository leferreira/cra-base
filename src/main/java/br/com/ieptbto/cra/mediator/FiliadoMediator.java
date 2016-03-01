package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.FiliadoDAO;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.SetorFiliado;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class FiliadoMediator {

	@Autowired
	private FiliadoDAO filiadoDAO;

	public Filiado salvarFiliado(Filiado filiado) {
		return filiadoDAO.salvar(filiado);
	}

	public Filiado alterarFiliado(Filiado filiado) {
		return filiadoDAO.alterar(filiado);
	}

	public List<Filiado> buscarListaFiliados(Instituicao instituicao) {
		return filiadoDAO.buscarListaFiliadosPorConvenio(instituicao);
	}

	public List<Filiado> buscarTodosFiliados() {
		return filiadoDAO.getAll(Filiado.class);
	}
	
	public void removerSetor(SetorFiliado setor) {
		filiadoDAO.removerSertorFiliado(setor);
	}

	public List<SetorFiliado> buscarSetoresFiliado(Filiado filiado) {
		return filiadoDAO.buscarSetoresFiliado(filiado);
	}
	
	public List<SetorFiliado> buscarSetoresAtivosFiliado(Filiado filiado) {
		return filiadoDAO.buscarSetoresAtivosFiliado(filiado);
	}

	public SetorFiliado buscarSetorPadraoFiliado(Filiado filiado) {
		return filiadoDAO.buscarSetorPadraoFiliado(filiado);
	}
}
