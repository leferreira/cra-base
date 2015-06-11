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

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioUtils {

	private HashMap<String, Object> parametros = new HashMap<String, Object>();
	
	/**
	 * Retorna um município
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * */
	public JasperPrint relatorioSinteticoDeRemessa(List<SinteticoJRDataSource> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException{
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRemessa.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	/**
	 * Retorna um município
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * */
	public JasperPrint relatorioSinteticoDeConfirmacao(List<SinteticoJRDataSource> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoConfirmacao.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	/**
	 * Retorna um município
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * */
	public JasperPrint relatorioSinteticoDeRetorno(List<SinteticoJRDataSource> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException, IOException{
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRetorno.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	/**
	 * Retorna um município
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * */
	public JasperPrint relatorioSinteticoDeRemessaPorMunicipio(List<SinteticoJRDataSource> beans, Municipio pracaProtesto,
			LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("MUNICIPIO", pracaProtesto.getNomeMunicipio());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRemessaPorMunicipio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	/**
	 * Retorna um município
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * */
	public JasperPrint relatorioSinteticoDeConfirmacaoPorMunicipio(List<SinteticoJRDataSource> beans, Municipio pracaProtesto,LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("MUNICIPIO", pracaProtesto.getNomeMunicipio());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoConfirmacaoPorMunicipio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	/**
	 * Retorna um município
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * */
	public JasperPrint relatorioSinteticoDeRetornoPorMunicipio(List<SinteticoJRDataSource> beans, Municipio pracaProtesto,LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("MUNICIPIO", pracaProtesto.getNomeMunicipio());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRetornoPorMunicipio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	/**
	 * Retorna um município
	 * 
	 * @param String
	 *            nomeMunicipio
	 * @return
	 * @throws JRException 
	 * */
	public JasperPrint relatorioArquivoDetalhadoRemessa(Arquivo file, List<TituloRemessa> titulos) throws JRException {
		parametros.put("NOME_ARQUIVO", file.getNomeArquivo());
		parametros.put("DATA_ENVIO", DataUtil.localDateToString(file.getDataEnvio()));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulos);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioRemessaDetalhado.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public JasperPrint relatorioArquivoDetalhadoConfirmacao(Arquivo arquivo, List<TituloRemessa> titulos) throws JRException {
		parametros.put("NOME_ARQUIVO", arquivo.getNomeArquivo());
		parametros.put("DATA_ENVIO", DataUtil.localDateToString(arquivo.getDataEnvio()));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulos);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioConfirmacaoDetalhado.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public JasperPrint relatorioArquivoDetalhadoRetorno(Arquivo arquivo, List<TituloRemessa> titulos) throws JRException {
		parametros.put("NOME_ARQUIVO", arquivo.getNomeArquivo());
		parametros.put("DATA_ENVIO", DataUtil.localDateToString(arquivo.getDataEnvio()));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulos);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioRetornoDetalhado.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public JasperPrint relatorioDeTitulosPorInstituicao(Instituicao instituicao, List<TituloRemessa> titulos,LocalDate dataInicio, LocalDate dataFim) throws JRException{
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		parametros.put("INSTITUICAO", instituicao.getNomeFantasia());
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulos);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioTitulos.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	public JasperPrint relatorioDeTitulosPorMunicipio(Municipio municipio, List<TituloRemessa> titulos,LocalDate dataInicio, LocalDate dataFim) throws JRException{
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		parametros.put("MUNICIPIO", municipio.getNomeMunicipio());
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulos);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioTitulosPorMunicipio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
}
