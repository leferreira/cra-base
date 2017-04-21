package br.com.ieptbto.cra.conversor;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.exception.ConvertException;

/**
 * 
 * @author Lefer
 *
 */
public class BigDecimalConversor extends AbstractConversor<BigDecimal> {
	private static final int TAMANHO_PADRAO_CAMPO_VALOR = 10;
	private static final String VALOR_ZERO = "0";
	private static final BigDecimal VALOR_CEM = BigDecimal.valueOf(100);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal getValorConvertido(String valor) {
		try {
			if (valor == null || StringUtils.trim(valor).isEmpty()) {
				return BigDecimal.ZERO;
			}
			valor = valor.replace(",", "");
			valor = valor.replace(".", "");
			BigDecimal retorno = new BigDecimal(StringUtils.trim(valor));
			if (BigDecimal.ZERO.equals(retorno)) {
				return BigDecimal.ZERO;
			}
			return retorno.divide(VALOR_CEM);
		} catch (NumberFormatException e) {
			throw new ConvertException(ErroConversao.CONVERSAO_BIG_DECIMAL, e, getFieldName());
		}
	}

    /**
     * Gera o valor monetário apartir de uma string independente da quantidade de casas
     * decimais após a vírgula
     * @param valor
     * @return bigdecimal
     */
    public BigDecimal getValorBigDecimal(String valor) {
        if (valor == null || StringUtils.trim(valor).isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (!valor.matches("\\d+\\,\\d{2}")) {
            if (valor.contains(",")) {
                String[] splitValor = valor.split(",");
                if (splitValor.length > 1) {
                    splitValor[1] = StringUtils.rightPad(splitValor[1], 2, "0");
                    valor = splitValor[0] + "," + splitValor[1];
                } else {
                    valor = valor + "00";
                }
            } else {
                valor = valor + ",00";
            }
        }
	    return this.getValorConvertido(valor);
    }


	/**
	 * {@inheritDoc}
	 */
	public String getValorConvertidoParaString(BigDecimal objeto) {
		int tamanho = TAMANHO_PADRAO_CAMPO_VALOR;
		if (getCampoArquivo() != null) {
			tamanho = getAnotacaoAtributo().tamanho();
		}
		if (objeto != null) {
			BigDecimal retorno = objeto.multiply(VALOR_CEM);
			String numero = retorno.toPlainString().replace(".00", StringUtils.EMPTY);
			return StringUtils.leftPad(numero, tamanho, VALOR_ZERO);
		}
		return StringUtils.repeat(VALOR_ZERO, tamanho);
	}

	public String getValorConvertidoSegundoLayoutFebraban(BigDecimal valor) {
		int tamanho = TAMANHO_PADRAO_CAMPO_VALOR;
		if (valor == null) {
			return StringUtils.EMPTY;
		}
		return StringUtils.leftPad(Double.toString(valor.doubleValue()).replace(".", ""), tamanho, VALOR_ZERO);
	}

	@Override
	public BigDecimal getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(BigDecimal.class.cast(objeto));
	}

}
