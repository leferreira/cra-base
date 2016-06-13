package br.com.ieptbto.cra.util;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Utilitario para acentuacao
 */
public class RemoverAcentosUtil implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	public static String value =
			"À Á Â Ã Ä Å Æ Ç È É Ê Ë Ì Í Î Ï Ð Ñ Ò Ó Ô Õ Ö Ø Ù Ú Û Ü Ý Þ ß à á â ã ä å æ ç è é ê ë ì í î ï ð ñ ò ó ô õ ö ø ù ú û ü ý þ ÿ ";

	public static String removeAcentos(String str) {
		if (str != null || str != StringUtils.EMPTY) {
			String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			return pattern.matcher(nfdNormalizedString).replaceAll("");
		}
		return str;
	}
}
