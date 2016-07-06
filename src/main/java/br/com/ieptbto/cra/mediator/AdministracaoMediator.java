package br.com.ieptbto.cra.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.AdministracaoDAO;

@Service
public class AdministracaoMediator extends BaseMediator {

	@Autowired
	AdministracaoDAO administracaoDAO;

	public void removerArquivo() {

	}
}
