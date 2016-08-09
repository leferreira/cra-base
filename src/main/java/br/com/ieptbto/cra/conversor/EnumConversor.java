package br.com.ieptbto.cra.conversor;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.enumeration.AbstractCraEnum;
import br.com.ieptbto.cra.exception.ConvertException;

/**
 * 
 * @author Lefer
 *
 */
public class EnumConversor extends AbstractConversor<AbstractCraEnum> {

	private static final String VAZIO = " ";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractCraEnum getValorConvertido(String valor) {
		BeanWrapper propertyAccess = PropertyAccessorFactory.forBeanPropertyAccess(getArquivo());
		Class<?> propertyType = propertyAccess.getPropertyType(getFieldName());

		return getValorConvertido(valor, propertyType);

	}

	/**
	 * 
	 * @param valor
	 * @param propertyType
	 * @return
	 */
	@Override
	public AbstractCraEnum getValorConvertido(String valor, Class<?> propertyType) {
		try {
			if (AbstractCraEnum.class.isAssignableFrom(propertyType)) {
				Object[] enumConstants = propertyType.getEnumConstants();
				for (Object object : enumConstants) {
					AbstractCraEnum enumObject = (AbstractCraEnum) object;
					if (enumObject.getConstante().equals(valor)) {
						return enumObject;
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new ConvertException(ErroConversao.CONVERSAO_ENUM, e, getFieldName());
		}
		throw new ConvertException(ErroConversao.CONVERSAO_ENUM, getFieldName());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValorConvertidoParaString(AbstractCraEnum objeto) {
		int tamanho = getAnotacaoAtributo().tamanho();
		if (objeto != null) {
			String codigo = objeto.getConstante();
			return StringUtils.leftPad(codigo, tamanho, VAZIO);
		}
		return StringUtils.repeat(VAZIO, tamanho);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(AbstractCraEnum.class.cast(objeto));
	}
}
