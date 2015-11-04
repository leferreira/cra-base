package br.com.ieptbto.cra.util;

import java.io.Serializable;
import java.text.Normalizer;

/**
 * Utilitario para acentuacao
 */
@SuppressWarnings("serial")
public class RemoveAcentosUtil implements Serializable {


	public static String removeAcentos(String str) {
		 
	  str = Normalizer.normalize(str, Normalizer.Form.NFD);
	  str = str.replaceAll("[^\\p{ASCII}]", "");
	  return str;
	}
}
