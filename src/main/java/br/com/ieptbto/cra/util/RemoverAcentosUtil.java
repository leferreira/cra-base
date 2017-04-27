package br.com.ieptbto.cra.util;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utilitario para acentuacao
 * 
 * @author Thasso Araújo
 */
public class RemoverAcentosUtil implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		System.out.println(RemoverAcentosUtil.removeAcentos("t♫e£s↓t♀e• m♠u↨i♫t►o  l*o#u♂c?o  '"));
	}

	public static String removeAcentos(String text) {
		if (text != null) {
			if (!StringUtils.isEmpty(text.trim())) {
				text = text.replaceAll("[ÂÀÁÄÃ]", "A");
				text = text.replaceAll("[âãàáä]", "a");
				text = text.replaceAll("[ÊÈÉË]", "E");
				text = text.replaceAll("[êèéë]", "e");
				text = text.replaceAll("[ÎÍÌÏ]", "I");
				text = text.replaceAll("[îíìï]", "i");
				text = text.replaceAll("[ÔÕÒÓÖ]", "O");
				text = text.replaceAll("[ôõòóö]", "o");
				text = text.replaceAll("[ÛÙÚÜ]", "U");
				text = text.replaceAll("[ûúùü]", "u");
				text = text.replaceAll("Ç", "C");
				text = text.replaceAll("ç", "c");
				text = text.replaceAll("[ýÿ]", "y");
				text = text.replaceAll("Ý", "Y");
				text = text.replaceAll("ñ", "n");
				text = text.replaceAll("Ñ", "N");
				text = text.replace("*", " ");
				text = text.replace("º", " ");
				text = text.replace("ª", " ");
				text = text.replace("�", "");
				text = text.replace("_", " ");
				text = text.replace("#", " ");
				text = text.replace("?", " ");
				text = text.replace("'", "");

				String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD);
				Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
				text = pattern.matcher(nfdNormalizedString).replaceAll(" ");

				pattern = Pattern.compile("[^\\p{ASCII}]");
				text = pattern.matcher(nfdNormalizedString).replaceAll(" ");
			}
		}
		return text;
	}
}