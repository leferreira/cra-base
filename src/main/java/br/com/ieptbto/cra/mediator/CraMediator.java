package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.CraServiceDAO;
import br.com.ieptbto.cra.entidade.CraServiceConfig;
import br.com.ieptbto.cra.enumeration.CraServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CraMediator extends BaseMediator {

	@Autowired
	CraServiceDAO craServiceDAO;

	public void atualizarStatusServico(CraServiceConfig service) {
		craServiceDAO.atualizarStatusServico(service);
	}

	public void salvarServicos(List<CraServiceConfig> services) {
		craServiceDAO.salvarServicos(services);
	}

	public List<CraServiceConfig> carregarServicos() {
		return craServiceDAO.carregarServicos();
	}

	public boolean verificarServicoIndisponivel(CraServices craService) {
		CraServiceConfig service = craServiceDAO.verificarServicoIndisponivel(craService);
		if (service != null && service.getStatus() == true) {
			return false; 
		}
		return true;
	}
}