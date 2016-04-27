package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.AdministracaoDAO;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.processador.ProcessadorArquivoDeParaBB;

@Service
public class AdministracaoMediator {

	@Autowired
	AdministracaoDAO administracaoDAO;
	@Autowired
	ProcessadorArquivoDeParaBB processadorArquivoDeParaBB;

	public void executaArrumaDataTituloFiliado() {
		List<TituloFiliado> titulos = administracaoDAO.getAll(TituloFiliado.class);
		administracaoDAO.executaArrumaDataTituloFiliado(titulos);
	}

	public void removerArquivo() {
		// TODO Auto-generated method stub

	}
}
