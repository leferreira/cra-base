package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.TituloFiliadoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.processador.ProcessadorRemessaConveniada;

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
	@Autowired
	ProcessadorRemessaConveniada processadorRemessaConveniada;
	@Autowired
	ArquivoDAO arquivoDAO;

	public List<TituloFiliado> buscarTitulosConvenios() {
		return tituloFiliadoDAO.buscarTitulosConvenios();
	}

	public void gerarRemessas(Usuario usuario, List<TituloFiliado> listaTitulosConvenios) {
		List<Arquivo> arquivos = processadorRemessaConveniada.processar(listaTitulosConvenios, usuario);
		for (Arquivo arquivo : arquivos) {
			arquivoDAO.salvar(arquivo, usuario, new ArrayList<Exception>());
		}
		tituloFiliadoDAO.marcarComoEnviadoParaCRA(listaTitulosConvenios);
	}
}
