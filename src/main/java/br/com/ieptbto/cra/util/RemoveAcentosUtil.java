package br.com.ieptbto.cra.util;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Utilitario para acentuacao
 */
public class RemoveAcentosUtil implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	public static String removeAcentos(String str) {
		if (str != null || str != StringUtils.EMPTY) {
	       str = str.replaceAll("[ÂÀÁÄÃ]", "A");
	       str = str.replaceAll("[âãàáä]", "a");
	       str = str.replaceAll("[ÊÈÉË]", "E");
	       str = str.replaceAll("[êèéë]", "e");
	       str = str.replaceAll("[ÎÍÌÏ]", "I");
	       str = str.replaceAll("[îíìï]", "i");
	       str = str.replaceAll("[ÔÕÒÓÖ]", "O");
	       str = str.replaceAll("[ôõòóö]", "o");
	       str = str.replaceAll("[ÛÙÚÜ]", "U");
	       str = str.replaceAll("[ûúùü]", "u");
	       str = str.replaceAll("Ç", "C");
	       str = str.replaceAll("ç", "c");
	       str = str.replaceAll("[ýÿ]", "y");
	       str = str.replaceAll("Ý", "Y");
	       str = str.replaceAll("ñ", "n");
	       str = str.replaceAll("Ñ", "N");
	       str = str.replaceAll("Ñ", "N");
	       str = str.replaceAll("º", " ");
	       str = str.replaceAll("ª", " ");
	       str = str.replaceAll("[-+=*&amp;%$#@!_]", " ");
	       str = str.replaceAll("['\"]", " ");
	       str = str.replaceAll("[<>()\\{\\}]", " ");
	       str = str.replaceAll("['\\\\.,()|/]", " ");
	       str = str.replaceAll("[^!-ÿ]{1}[^ -ÿ]{0,}[^!-ÿ]{1}|[^!-ÿ]{1}", " ");
		}
		return str;
	}
}
