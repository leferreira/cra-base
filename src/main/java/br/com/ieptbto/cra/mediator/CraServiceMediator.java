package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.CraServiceDAO;
import br.com.ieptbto.cra.entidade.CraServiceConfig;

@Service
public class CraServiceMediator {

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

}
