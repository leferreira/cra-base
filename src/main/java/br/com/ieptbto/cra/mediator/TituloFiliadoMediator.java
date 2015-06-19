package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloFiliadoDAO;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloFiliadoMediator {

	@Autowired
	TituloFiliadoDAO tituloFiliadoDAO;
	
	public TituloFiliado salvarTituloFiliado(TituloFiliado titulo){
		return tituloFiliadoDAO.salvar(titulo);
	}

	public TituloFiliado alterarTituloFiliado(TituloFiliado titulo) {
		return tituloFiliadoDAO.alterar(titulo);
	}

	public TituloRemessa buscarTituloDoConvenioNaCra(TituloFiliado tituloFiliado) {
		return tituloFiliadoDAO.buscarTituloDoConvenioNaCra(tituloFiliado);
	}
	
	public List<TituloFiliado> titulosParaEnvioAoConvenio(Filiado empresaFiliada) {
		return tituloFiliadoDAO.buscarTitulosParaEnvioAoConvenio(empresaFiliada);
	}

	public void removerTituloFiliado(TituloFiliado titulo) {
		tituloFiliadoDAO.removerTituloFiliado(titulo);
	}

	public void enviarTitulosPendentes(List<TituloFiliado> listaTitulosFiliado) {
		tituloFiliadoDAO.enviarTitulosPendentes(listaTitulosFiliado);
	}

	public List<TituloFiliado> consultarTitulosConvenio(Instituicao instituicao, String nomeDevedor,
			String numeroDocumento, String numeroTitulo, LocalDate dataEmissao,
			Municipio pracaProtesto, Filiado filiado) {
		return tituloFiliadoDAO.consultarTitulosConvenio(instituicao, nomeDevedor, numeroDocumento, numeroTitulo, dataEmissao, pracaProtesto, filiado);
	}
	
	public List<TituloFiliado> consultarTitulosFiliado(Usuario usuarioFiliado, String nomeDevedor,
			String numeroDocumento, String numeroTitulo, LocalDate dataEmissao,
			Municipio pracaProtesto) {
		return tituloFiliadoDAO.consultarTitulosFiliado(usuarioFiliado, nomeDevedor, numeroDocumento, numeroTitulo, dataEmissao, pracaProtesto);
	}

	public List<TituloFiliado> buscarTitulosParaRelatorioFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
			Municipio pracaProtesto) {
		return tituloFiliadoDAO.buscarTitulosParaRelatorioFiliado(filiado, dataInicio, dataFim, pracaProtesto);
	}

	public List<TituloFiliado> buscarTitulosParaRelatorioConvenio(Instituicao convenio, Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
			Municipio pracaProtesto) {
		return tituloFiliadoDAO.buscarTitulosParaRelatorioConvenio(convenio, filiado ,dataInicio, dataFim, pracaProtesto);
	}
}
