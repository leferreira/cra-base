package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TipoArquivoMediator {

	@Autowired
	private TipoArquivoDAO tipoArquivoDao;

	public TipoArquivo alterarTipoArquivo(TipoArquivo tipoArquivo) {
		return tipoArquivoDao.alterar(tipoArquivo);
	}

	public List<TipoArquivo> getTiposArquivos() {
		return tipoArquivoDao.buscarTiposArquivo();
	}

	public TipoArquivo buscarTipoPorNome(TipoArquivoFebraban tipoArquivo) {
		return tipoArquivoDao.buscarPorTipoArquivo(tipoArquivo);
	}
}
