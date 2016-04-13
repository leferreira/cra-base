package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
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

	public TituloRemessa carregarTituloRemessa(TituloRemessa titulo) {
		return tituloDAO.buscarPorPK(titulo, TituloRemessa.class);
	}

	public Confirmacao buscarConfirmacao(TituloRemessa titulo) {
		return tituloDAO.buscarConfirmacao(titulo);
	}

	public Confirmacao carregarTituloConfirmacao(Confirmacao confirmacao) {
		return tituloDAO.buscarPorPK(confirmacao, Confirmacao.class);
	}

	public Retorno buscarRetorno(TituloRemessa titulo) {
		return tituloDAO.buscarRetorno(titulo);
	}

	public Retorno carregarTituloRetorno(Retorno retorno) {
		return tituloDAO.buscarPorPK(retorno, Retorno.class);
	}

	public List<TituloRemessa> carregarTitulos(Remessa remessa) {
		return tituloDAO.carregarTitulos(remessa);
	}

	public List<TituloRemessa> buscarListaTitulos(LocalDate dataInicio, LocalDate dataFim, TituloRemessa titulo,
			Municipio pracaProtesto, Usuario user) {
		return tituloDAO.buscarListaTitulos(dataInicio, dataFim, titulo, pracaProtesto, user);
	}

	@SuppressWarnings("rawtypes")
	public List<Titulo> carregarTitulosGenerico(Arquivo arquivo) {
		return tituloDAO.carregarTitulosGenerico(arquivo);
	}

	public TituloRemessa buscarTituloPorChave(TituloRemessa titulo) {
		return tituloDAO.buscarTituloPorChave(titulo);
	}
}
