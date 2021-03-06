package br.com.ieptbto.cra.conversor;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.exception.ConvertException;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Lefer
 *
 */
public class IntegerConversor extends AbstractConversor<Integer> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getValorConvertido(String valor) {
		try {
			if (StringUtils.isNotBlank(valor) && StringUtils.isNotEmpty(valor)) {
				return Integer.valueOf(valor);
			}
			return null;
		} catch (NumberFormatException e) {
			getArquivo();
			if (getAnotacaoAtributo().obrigatoriedade()) {
				throw new ConvertException(ErroConversao.CONVERSAO_INTEGER, e, getFieldName());
			}
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValorConvertidoParaString(Integer objeto) {
		if (getAnotacaoAtributo() != null) {
			int tamanho = getAnotacaoAtributo().tamanho();
			if (objeto != null) {
				return StringUtils.leftPad(objeto.toString(), tamanho, '0');
			}
			return StringUtils.repeat("0", tamanho);
		}
		return Integer.toString(objeto);
	}

	@Override
	public Integer getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(Integer.class.cast(objeto));
	}

}
