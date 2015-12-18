package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.MunicipioDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;

@Service
public class MunicipioMediator {

	@Autowired
	MunicipioDAO municipioDao;
	@Autowired
	InstituicaoDAO instituicaoDao;

	public Municipio adicionarMunicipio(Municipio municipio) {
		return municipioDao.salvar(municipio);
	}

	public Municipio alterarMunicipio(Municipio municipio) {
		return municipioDao.alterar(municipio);
	}

	public boolean isMunicipioNaoExiste(Municipio municipio) {
		Municipio municipioNovo = municipioDao.buscarMunicipioPorNome(municipio.getNomeMunicipio());
		if (municipioNovo == null) {
			return true;
		}
		return false;
	}

	public Instituicao isMunicipioTemCartorio(Municipio municipio) {
		Instituicao cartorio = instituicaoDao.buscarCartorioPorMunicipio(municipio.getNomeMunicipio());
		if (cartorio != null) {
			return cartorio;
		}
		return null;
	}

	public Municipio buscarMunicipio(String nomeMunicipio) {
		if (nomeMunicipio == null) {
			return null;
		}

		return municipioDao.buscarMunicipioPorNome(nomeMunicipio);
	}

	public List<Municipio> listarTodos() {
		return municipioDao.listarTodos();
	}

	public List<Municipio> getMunicipiosTocantins() {
		return municipioDao.listarTodosTocantins();
	}

	public Municipio salvar(Municipio municipio) {
		return municipioDao.salvar(municipio);
	}

	public Municipio buscaMunicipioPorCodigoIBGE(String codigoMunicipio) {
		return municipioDao.buscaMunicipioPorCodigoIBGE(codigoMunicipio);
	}

	public Municipio buscarMunicipioDoCartorio(Instituicao cartorio) {
		return municipioDao.buscarMunicipioDoCartorio(cartorio);
	}

	public List<Municipio> buscarMunicipiosAtivos() {
		return municipioDao.buscarMunicipiosAtivos();
	}
}
