package br.com.ieptbto.cra.mediator;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.logger.LoggerCra;

public class BaseMediator {

	@Autowired
	protected LoggerCra loggerCra;
}
