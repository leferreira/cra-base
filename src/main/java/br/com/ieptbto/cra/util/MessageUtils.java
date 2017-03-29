package br.com.ieptbto.cra.util;

import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.jetbrains.annotations.NotNull;

public class MessageUtils {

	/**
	 * Shortcut to create message translation model
	 * 
	 * @param message
	 * @param args
	 * @return
	 */
	@NotNull
	public static StringResourceModel _(String message, Object... args) {
		return new StringResourceModel(message, new Model<Object[]>(args), message);
	}
}