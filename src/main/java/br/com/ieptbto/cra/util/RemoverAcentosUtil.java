package br.com.ieptbto.cra.util;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Utilitario para acentuacao
 * 
 * @author Thasso Araújo
 */
public class RemoverAcentosUtil implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	public static String removeAcentos(String retorno) {

		if (retorno != null || retorno != StringUtils.EMPTY) {
			retorno = retorno.replaceAll("[ÂÀÁÄÃ]", "A");
			retorno = retorno.replaceAll("[âãàáä]", "a");
			retorno = retorno.replaceAll("[ÊÈÉË]", "E");
			retorno = retorno.replaceAll("[êèéë]", "e");
			retorno = retorno.replaceAll("[ÎÍÌÏ]", "I");
			retorno = retorno.replaceAll("[îíìï]", "i");
			retorno = retorno.replaceAll("[ÔÕÒÓÖ]", "O");
			retorno = retorno.replaceAll("[ôõòóö]", "o");
			retorno = retorno.replaceAll("[ÛÙÚÜ]", "U");
			retorno = retorno.replaceAll("[ûúùü]", "u");
			retorno = retorno.replaceAll("Ç", "C");
			retorno = retorno.replaceAll("ç", "c");
			retorno = retorno.replaceAll("[ýÿ]", "y");
			retorno = retorno.replaceAll("Ý", "Y");
			retorno = retorno.replaceAll("ñ", "n");
			retorno = retorno.replaceAll("Ñ", "N");
			retorno = retorno.replace("*", " ");
			retorno = retorno.replace("º", " ");
			retorno = retorno.replace("ª", " ");

			String nfdNormalizedString = Normalizer.normalize(retorno, Normalizer.Form.NFD);
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			retorno = pattern.matcher(nfdNormalizedString).replaceAll(" ");
		}
		return retorno;
	}
}