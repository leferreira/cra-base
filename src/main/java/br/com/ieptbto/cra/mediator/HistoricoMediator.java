package br.com.ieptbto.cra.mediator;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.HistoricoOcorrenciaDAO;
import br.com.ieptbto.cra.entidade.HistoricoOcorrenciaTitulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class HistoricoMediator {

	@Autowired
	private HistoricoOcorrenciaDAO historicoOcorrenciaDAO;
	
	public HistoricoOcorrenciaTitulo salvarHistoricoOcorrencia(TituloRemessa tituloRemessa,
			TipoOcorrencia tipoOcorrencia, LocalDate dataOcorrencia) {
		HistoricoOcorrenciaTitulo historicoOcorrenciaTitulo = new HistoricoOcorrenciaTitulo();
		historicoOcorrenciaTitulo.setTitulo(tituloRemessa);
		historicoOcorrenciaTitulo.setTipoOcorrencia(tipoOcorrencia);
		historicoOcorrenciaTitulo.setDataOcorrencia(dataOcorrencia);
		
		return 	historicoOcorrenciaDAO.salvarHistoricoOcorrencia(historicoOcorrenciaTitulo);	
	}

}
