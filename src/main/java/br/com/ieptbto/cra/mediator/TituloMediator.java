package br.com.ieptbto.cra.mediator;

import java.util.List;

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
    private TituloDAO tituloDAO;

    public TituloRemessa carregarTituloRemessaPorId(TituloRemessa titulo) {
	return tituloDAO.buscarPorPK(titulo, TituloRemessa.class);
    }

    public Confirmacao carregarTituloConfirmacaoPorId(Confirmacao confirmacao) {
	return tituloDAO.buscarPorPK(confirmacao, Confirmacao.class);
    }

    public Retorno carregarTituloRetornoPorId(Retorno retorno) {
	return tituloDAO.buscarPorPK(retorno, Retorno.class);
    }

    public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Municipio pracaProtesto, Usuario user) {
	return tituloDAO.buscarListaTitulos(titulo, pracaProtesto, user);
    }

    @SuppressWarnings("rawtypes")
    public List<Titulo> carregarTitulosGenerico(Arquivo arquivo) {
	return tituloDAO.carregarTitulosGenerico(arquivo);
    }

    @SuppressWarnings("rawtypes")
    public List<Titulo> carregarTitulosGenerico(Remessa remessa) {
	return tituloDAO.carregarTitulosGenerico(remessa);
    }

    public TituloRemessa buscarTituloPorChave(TituloRemessa titulo) {
	return tituloDAO.buscarTituloPorChave(titulo);
    }
}
