package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.exception.InfraException;

@Service
public class InstituicaoMediator {

	@Autowired
	InstituicaoDAO instituicaoDAO;

	public Instituicao salvar(Instituicao instituicao) {
		return instituicaoDAO.salvar(instituicao);
	}

	public Instituicao alterar(Instituicao instituicao) {
		return instituicaoDAO.alterar(instituicao);
	}

	public boolean isInstituicaoNaoExiste(Instituicao instituicao) {
		Instituicao instituicaoNova = instituicaoDAO.buscarInstituicao(instituicao.getNomeFantasia());
		if (instituicaoNova == null) {
			return true;
		}
		return false;
	}

	public Instituicao buscarInstituicao(Instituicao instituicao) {
		return instituicaoDAO.buscarInstituicao(instituicao);
	}

	public Instituicao buscarInstituicaoIncial(String instituicao) {
		return instituicaoDAO.buscarInstituicaoInicial(instituicao);
	}

	public List<Instituicao> getInstituicoesAtivas() {
		return instituicaoDAO.buscarListaInstituicaoAtivas();
	}

	public List<Instituicao> listarTodas() {
		return instituicaoDAO.listarTodas();
	}

	public List<Instituicao> listarTodasInstituicoes() {
		return instituicaoDAO.buscarListaInstituicao();
	}

	public List<Instituicao> getCartorios() {
		return instituicaoDAO.getCartorios();
	}

	public Instituicao getCartorioPorCodigoIBGE(String codigoMunicipio) {
		Instituicao instituicao = instituicaoDAO.getInstituicao(codigoMunicipio);
		if (instituicao == null) {
			throw new InfraException("Instituição não cadastrada com o código IBGE [" + codigoMunicipio + "]");
		}

		return instituicao;
	}

	public Instituicao getInstituicaoPorCodigoPortador(String codigoPortador) {
		Instituicao instituicao = instituicaoDAO.getInstituicaoPorCodigo(codigoPortador);
		if (instituicao == null) {
			throw new InfraException("Instituição não cadastrada com o código de compesação [" + codigoPortador + "]");
		}

		return instituicao;
	}

	public List<Instituicao> getInstituicoesFinanceiras() {
		return instituicaoDAO.getInstituicoesFinanceiras();
	}
	
	public List<Instituicao> getInstituicoesFinanceirasEConvenios() {
		return instituicaoDAO.getInstituicoesFinanceirasEConvenios();
	}
	
	public List<Instituicao> getConvenios() {
		return instituicaoDAO.getConvenios();
	}

	public boolean isInstituicaoAtiva(Instituicao instituicao) {
		return instituicaoDAO.isInstituicaoAtiva(instituicao);
	}

	public Instituicao buscarCRA() {
		Instituicao instituicao = new Instituicao();
		instituicao.setNomeFantasia("CRA");
		return instituicaoDAO.buscarInstituicao(instituicao);
	}

}
