package br.com.ieptbto.cra.mediator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.logger.LoggerCra;

public class BaseMediator {

	protected static final Logger logger = Logger.getLogger(BaseMediator.class);

	@Autowired
	protected LoggerCra loggerCra;
}
