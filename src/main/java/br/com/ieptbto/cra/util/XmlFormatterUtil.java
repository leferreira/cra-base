package br.com.ieptbto.cra.util;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;

/**
 * Utilitário de formatação de string xml
 */
public class XmlFormatterUtil {

	public static String format(String xml) {
		String xmlFormatado = StringUtils.EMPTY;

		try {
			Document doc = DocumentHelper.parseText(xml);
			StringWriter sw = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("ISO-8859-1");
			format.setNewLineAfterDeclaration(false);

			XMLWriter xw = new XMLWriter(sw, format);
			xw.write(doc);
			xmlFormatado = sw.toString();

		} catch (Exception e) {
			return xml;
		}
		return xmlFormatado;
	}
}