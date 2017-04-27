package br.com.ieptbto.cra.conversor;

import br.com.ieptbto.cra.exception.ConvertException;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Lefer
 *
 */
public class StringConversor extends AbstractConversor<String> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValorConvertido(String valor) {
		return valor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValorConvertidoParaString(String objeto) {
		if (getAnotacaoAtributo() != null) {
			int tamanho = getAnotacaoAtributo().tamanho();
			if (objeto != null) {
				if (objeto.length() > tamanho) {
					return objeto.substring(0, tamanho);
				}
				return StringUtils.rightPad(objeto, tamanho, ' ');
			}
			return StringUtils.repeat(" ", tamanho);
		}
		return objeto;
	}

	@Override
	public String getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(String.class.cast(objeto));
	}

}
