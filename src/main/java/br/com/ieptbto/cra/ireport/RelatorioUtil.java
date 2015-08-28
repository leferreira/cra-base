package br.com.ieptbto.cra.ireport;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioUtil {

	private static HashMap<String, Object> parametros = new HashMap<String, Object>();
	
	public static JasperPrint relatorioSinteticoDeRemessa(List<RelatorioSinteticoBean> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException{
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(RelatorioUtil.class.getClass().getResourceAsStream("../relatorio/RelatorioSinteticoRemessa.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public static JasperPrint relatorioSinteticoDeConfirmacao(List<RelatorioSinteticoBean> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(RelatorioUtil.class.getClass().getResourceAsStream("../relatorio/RelatorioConfirmacaoDetalhado.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public static JasperPrint relatorioSinteticoDeRetorno(List<RelatorioSinteticoBean> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException, IOException{
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(RelatorioUtil.class.getClass().getResourceAsStream("../relatorio/RelatorioSinteticoRetorno.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	public static JasperPrint relatorioSinteticoDeRemessaPorMunicipio(List<RelatorioSinteticoBean> beans, Municipio pracaProtesto,
			LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("MUNICIPIO", pracaProtesto.getNomeMunicipio());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(RelatorioUtil.class.getClass().getResourceAsStream("../relatorio/RelatorioSinteticoRemessaPorMunicipio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	public static JasperPrint relatorioSinteticoDeConfirmacaoPorMunicipio(List<RelatorioSinteticoBean> beans, Municipio pracaProtesto,LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("MUNICIPIO", pracaProtesto.getNomeMunicipio());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(RelatorioUtil.class.getClass().getResourceAsStream("../relatorio/RelatorioSinteticoConfirmacaoPorMunicipio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public static JasperPrint relatorioSinteticoDeRetornoPorMunicipio(List<RelatorioSinteticoBean> beans, Municipio pracaProtesto,LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("MUNICIPIO", pracaProtesto.getNomeMunicipio());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(RelatorioUtil.class.getClass().getResourceAsStream("../relatorio/RelatorioSinteticoRetornoPorMunicipio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
}
