package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TipoInstituicaoDAO;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TipoInstituicaoMediator {

	@Autowired
	private TipoInstituicaoDAO tipoInstituicaoDao;

	public TipoInstituicao alterarPermissoesTipoInstituicao(TipoInstituicao tipoInstituicao) {
		return tipoInstituicaoDao.alterar(tipoInstituicao);
	}

	public TipoInstituicao buscarTipoInstituicao(TipoInstituicaoCRA tipoInstituicao) {
		return tipoInstituicaoDao.buscarTipoInstituicao(tipoInstituicao);
	}

	public List<TipoInstituicao> listaTipoInstituicao() {
		return tipoInstituicaoDao.buscarListaTipoInstituicao();
	}

	public List<TipoInstituicao> listarTipos() {
		return tipoInstituicaoDao.listarTodos();
	}
}