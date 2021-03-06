/*******************************************************************************
 * Copyright (c) 2012 Anton Bessonov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons
 * Attribution 3.0 License which accompanies this distribution,
 * and is available at
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Contributors:
 *     Anton Bessonov - initial API and implementation
 ******************************************************************************/
package br.com.ieptbto.cra.security;

import br.com.ieptbto.cra.conversor.enumeration.ErroValidacao;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

public class UserRoleAuthorizationStrategy implements IAuthorizationStrategy {

	protected final List<IAuthorizationStrategy> strategies = new ArrayList<IAuthorizationStrategy>();

	public UserRoleAuthorizationStrategy(final IRoleCheckingStrategy roleCheckingStrategy) {
		add(new AnnotationsRoleAuthorizationStrategy(roleCheckingStrategy));
		add(new MetaDataRoleAuthorizationStrategy(roleCheckingStrategy));
	}

	public void add(IAuthorizationStrategy strategy) {
		if (strategy == null) {
			throw new IllegalArgumentException("Strategy cannot be null");
		}
		strategies.add(strategy);
	}

	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
		for (IAuthorizationStrategy strategy : strategies) {
			if (strategy.isInstantiationAuthorized(componentClass) == false) {
				ISecureApplication app = (ISecureApplication) Application.get();
				throw new RestartResponseAtInterceptPageException(app.getLoginPage());
			}
		}
		return true;
	}

	@Override
	public boolean isActionAuthorized(Component component, Action action) {
		for (IAuthorizationStrategy strategy : strategies) {
			if (strategy.isActionAuthorized(component, action) == false) {

				// Essa parte do codigo foi alterada para sempre redirecionar
				// para pagina de login quando nao for autorizado.

				// return true (codigo original)

				ISecureApplication app = (ISecureApplication) Application.get();
				PageParameters parameters = new PageParameters();
				parameters.add("error", ErroValidacao.SEM_PERMISSAO_DE_ACESSO_A_PAGINA.getMensagemErro());
				throw new RestartResponseAtInterceptPageException(app.getHomePage(), parameters);

			}
		}
		return true;
	}
}
