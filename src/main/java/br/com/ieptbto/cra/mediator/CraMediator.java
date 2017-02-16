package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.CraServiceDAO;
import br.com.ieptbto.cra.entidade.CraServiceConfig;
import br.com.ieptbto.cra.enumeration.CraServices;

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