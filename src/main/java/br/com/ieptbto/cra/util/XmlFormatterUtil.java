package br.com.ieptbto.cra.util;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Utilitário de formatação de string xml
 */
public class XmlFormatterUtil {

    public static String format(String xml) {

    	 try{
    		 Document doc = DocumentHelper.parseText(xml);  
    		 StringWriter sw = new StringWriter();  
    		 OutputFormat format = OutputFormat.createPrettyPrint();  
    		 XMLWriter xw = new XMLWriter(sw, format);  
    		 xw.write(doc);  
    		 return sw.toString();
         }catch(Exception e){
             return xml;
         }
    }
}