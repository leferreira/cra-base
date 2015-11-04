package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.exception.ConvertException;

public class IntConversor extends AbstractConversor<Integer> {

	@Override
	public Integer getValorConvertido(String valor) throws ConvertException {
		return Integer.parseInt(valor);
	}

	@Override
	public Integer getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	@Override
	public String getValorConvertidoParaString(Integer objeto) {
		return objeto.toString();
	}

	@Override
	public String getValorConvertidoString(Object objeto) {
		return objeto.toString();
	}

}
