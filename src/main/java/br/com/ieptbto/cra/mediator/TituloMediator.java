package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloMediator {

	@Autowired
	TituloDAO tituloDAO;
	
	public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Municipio pracaProtesto, Usuario user){
		return tituloDAO.buscarListaTitulos(titulo, pracaProtesto, user);
	}
	
	public List<TituloRemessa> buscarTitulosPorRemessa(Remessa remessa, Instituicao instituicaoCorrente){
		return tituloDAO.buscarTitulosPorRemessa(remessa, instituicaoCorrente);
	}
	
	public List<TituloRemessa> buscarTitulosPorRemessa(Remessa remessa, Instituicao instituicaoOrigem, Instituicao instituicaoDestino){
		return tituloDAO.buscarTitulosPorRemessa(remessa, instituicaoOrigem, instituicaoDestino);
	}
	
	public List<TituloRemessa> buscarTitulosPorArquivo(Arquivo arquivo, Instituicao instituicaoCorrente){
		return tituloDAO.buscarTitulosPorArquivo(arquivo, instituicaoCorrente);
	}
	
	public TituloRemessa buscarTituloPorChave(TituloRemessa titulo){
		return tituloDAO.buscarTituloPorChave(titulo);
	}
	
	public TituloRemessa carregarTitulo(TituloRemessa titulo){
		return tituloDAO.carregarTitulo(titulo);
	}

	public TituloRemessa carregarDadosHistoricoTitulo(TituloRemessa titulo) {
		return tituloDAO.carregarDadosHistoricoTitulo(titulo);
	}
}
