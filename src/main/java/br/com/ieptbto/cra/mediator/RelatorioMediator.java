package br.com.ieptbto.cra.mediator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.HibernateException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.bean.RelatorioSinteticoBean;
import br.com.ieptbto.cra.bean.TituloBean;
import br.com.ieptbto.cra.dao.RelatorioDAO;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RelatorioMediator {
	
	@Autowired
	RelatorioDAO relatorioDAO;
	@Autowired
	RetornoDAO retornoDAO;
	@Autowired
	TituloDAO tituloDAO;
	private HashMap<String, Object> parametros = new HashMap<String, Object>();
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Instituicao instituicao;
	private Municipio pracaProtesto;
	
	public JasperPrint relatorioRemessa(JasperReport jasperReport, Remessa remessa, Instituicao instituicaoCorrente) throws JRException {
		List<TituloRemessa> titulos = tituloDAO.buscarTitulosPorRemessa(remessa, remessa.getInstituicaoOrigem());
		if (titulos.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. O arquivo não contém titulos !");
		BigDecimal valorTotal = new BigDecimal(0);
		List<TituloBean> titulosJR = new ArrayList<TituloBean>();
		for (TituloRemessa tituloRemessa : titulos) {
			TituloBean tituloJR = new TituloBean();
			tituloJR.parseToTituloRemessa(tituloRemessa);
			valorTotal = valorTotal.add(tituloRemessa.getSaldoTitulo());
			titulosJR.add(tituloJR);
		}
		parametros.put("NOME_ARQUIVO", remessa.getArquivo().getNomeArquivo());
		parametros.put("DATA_ENVIO", DataUtil.localDateToString(remessa.getDataRecebimento()));
		parametros.put("INSTITUICAO", remessa.getInstituicaoOrigem().getNomeFantasia().toUpperCase());
		parametros.put("TOTAL_TITULOS", Integer.class.cast(titulosJR.size()));
		parametros.put("VALOR_TOTAL", valorTotal);
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulosJR);
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	public JasperPrint relatorioRemessa(Arquivo arquivo, Instituicao instituicaoCorrente) {
		return null;
	}
	
	public JasperPrint relatorioConfirmacao(JasperReport jasperReport, Remessa remessa, Instituicao instituicaoCorrente) throws JRException {
		List<TituloRemessa> titulos = tituloDAO.buscarTitulosPorRemessa(remessa, instituicaoCorrente);
		if (titulos.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. O arquivo não contém titulos !");
		Integer numeroApontados = 0;
		Integer numeroDevolvidos = 0;
		BigDecimal valorTotal = new BigDecimal(0);
		List<TituloBean> titulosJR = new ArrayList<TituloBean>();
		for (TituloRemessa tituloRemessa : titulos) {
			TituloBean tituloJR = new TituloBean();
			tituloJR.parseToTituloRemessa(tituloRemessa);
			if (tituloRemessa.getConfirmacao().getTipoOcorrencia().equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
				numeroDevolvidos = numeroDevolvidos + 1;
			} else {
				numeroApontados = numeroApontados + 1;
				valorTotal = valorTotal.add(tituloRemessa.getSaldoTitulo());
			}
			titulosJR.add(tituloJR);
		}
		parametros.put("NOME_ARQUIVO", remessa.getArquivo().getNomeArquivo());
		parametros.put("DATA_ENVIO", DataUtil.localDateToString(remessa.getDataRecebimento()));
		parametros.put("INSTITUICAO", remessa.getInstituicaoOrigem().getNomeFantasia().toUpperCase());
		parametros.put("QTD_TITULOS", Integer.class.cast(titulosJR.size()));
		parametros.put("QTD_APONTADOS", numeroApontados);
		parametros.put("QTD_DEVOLVIDOS", numeroDevolvidos);
		parametros.put("TOTAL_APONTADOS", valorTotal);
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulosJR);
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	public JasperPrint relatorioConfirmacao(Arquivo arquivo, Instituicao instituicaoCorrente) {
		return null;
	}
	
	public JasperPrint relatorioRetorno(JasperReport jasperReport, Remessa remessa, Instituicao instituicaoCorrente) throws JRException {
		List<TituloRemessa> titulos = tituloDAO.buscarTitulosPorRemessa(remessa, instituicaoCorrente);
		if (titulos.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. O arquivo não contém titulos !");
		
		Integer numeroPagos = 0;
		Integer numeroProtestadosRetirados = 0;
		List<TituloBean> titulosJR = new ArrayList<TituloBean>();
		for (TituloRemessa tituloRemessa : titulos) {
			TituloBean tituloJR = new TituloBean();
			tituloJR.parseToTituloRemessa(tituloRemessa);
			if (TipoOcorrencia.getTipoOcorrencia(tituloRemessa.getRetorno().getTipoOcorrencia()).equals(TipoOcorrencia.PAGO)) {
				numeroPagos = numeroPagos + 1;
			} else if ((TipoOcorrencia.getTipoOcorrencia(tituloRemessa.getRetorno().getTipoOcorrencia()).equals(TipoOcorrencia.PROTESTADO)) ||
					(TipoOcorrencia.getTipoOcorrencia(tituloRemessa.getRetorno().getTipoOcorrencia()).equals(TipoOcorrencia.RETIRADO))) {
				numeroProtestadosRetirados = numeroProtestadosRetirados + 1;
			}
			titulosJR.add(tituloJR);
		}
		parametros.put("NOME_ARQUIVO", remessa.getArquivo().getNomeArquivo());
		parametros.put("DATA_ENVIO", DataUtil.localDateToString(remessa.getDataRecebimento()));
		parametros.put("INSTITUICAO", remessa.getInstituicaoOrigem().getNomeFantasia().toUpperCase());
		parametros.put("TOTAL_TITULOS", Integer.class.cast(titulosJR.size()));
		parametros.put("TOTAL_PAGOS", retornoDAO.buscarValorDeTitulosPagos(remessa));
		parametros.put("TOTAL_DEMAIS_DESPESAS", retornoDAO.buscarValorDemaisDespesas(remessa));
		parametros.put("TOTAL_CUSTAS", retornoDAO.buscarValorDeCustasCartorio(remessa));
		parametros.put("QTD_PAGOS", numeroPagos);
		parametros.put("QTD_PROTESTADOS_RETIRADOS", numeroProtestadosRetirados);

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulosJR);
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	public JasperPrint relatorioRetorno(JasperReport jasperReport, Arquivo arquivo, Instituicao instituicaoCorrente) throws JRException {
		List<TituloRemessa> titulos = tituloDAO.buscarTitulosPorArquivo(arquivo, instituicaoCorrente);
		
		if (titulos.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. O arquivo não contém titulos !");
		
		Integer numeroPagos = 0;
		Integer numeroProtestadosRetirados = 0;
		List<TituloBean> titulosJR = new ArrayList<TituloBean>();
		for (TituloRemessa tituloRemessa : titulos) {
			TituloBean tituloJR = new TituloBean();
			tituloJR.parseToTituloRemessa(tituloRemessa);
			if (TipoOcorrencia.getTipoOcorrencia(tituloRemessa.getRetorno().getTipoOcorrencia()).equals(TipoOcorrencia.PAGO)) {
				numeroPagos = numeroPagos + 1;
			} else if ((TipoOcorrencia.getTipoOcorrencia(tituloRemessa.getRetorno().getTipoOcorrencia()).equals(TipoOcorrencia.PROTESTADO)) ||
					(TipoOcorrencia.getTipoOcorrencia(tituloRemessa.getRetorno().getTipoOcorrencia()).equals(TipoOcorrencia.RETIRADO))) {
				numeroProtestadosRetirados = numeroProtestadosRetirados + 1;
			}
			titulosJR.add(tituloJR);
		}
		parametros.put("NOME_ARQUIVO", arquivo.getNomeArquivo());
		parametros.put("DATA_ENVIO", DataUtil.localDateToString(arquivo.getDataEnvio()));
		parametros.put("INSTITUICAO", arquivo.getInstituicaoEnvio().getNomeFantasia().toUpperCase());
		parametros.put("TOTAL_TITULOS", Integer.class.cast(titulosJR.size()));
		parametros.put("TOTAL_PAGOS", retornoDAO.buscarValorDeTitulosPagos(arquivo));
		parametros.put("TOTAL_DEMAIS_DESPESAS", retornoDAO.buscarValorDemaisDespesas(arquivo));
		parametros.put("TOTAL_CUSTAS", retornoDAO.buscarValorDeCustasCartorio(arquivo));
		parametros.put("QTD_PAGOS", numeroPagos);
		parametros.put("QTD_PROTESTADOS_RETIRADOS", numeroProtestadosRetirados);

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulosJR);
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	public List<TituloRemessa> buscarTitulosParaRelatorio(Instituicao instituicao, TipoRelatorio situacaoTitulosRelatorio, LocalDate dataInicio, LocalDate dataFim, Usuario usuario) {
		return relatorioDAO.buscarTitulosParaRelatorio(instituicao, situacaoTitulosRelatorio, dataInicio, dataFim, usuario);
		
//		if (situacaoTitulosRelatorio.equals(TipoRelatorio.GERAL)) {
//			return relatorioDAO.relatorioTitulosGeral(usuario, instituicao, dataInicio, dataFim);
//		} else if (situacaoTitulosRelatorio.equals(TipoRelatorio.SEM_CONFIRMACAO)) {
//			return relatorioDAO.relatorioTitulosSemConfirmacao(usuario, instituicao, dataInicio, dataFim);
//		} else if (situacaoTitulosRelatorio.equals(TipoRelatorio.COM_CONFIRMACAO)) {
//			return relatorioDAO.relatorioTitulosComConfirmacao(usuario, instituicao, dataInicio, dataFim);
//		} else if (situacaoTitulosRelatorio.equals(TipoRelatorio.SEM_RETORNO)) {
//			return relatorioDAO.relatorioTitulosSemRetorno(usuario, instituicao, dataInicio, dataFim);
//		} else if (situacaoTitulosRelatorio.equals(TipoRelatorio.COM_RETORNO)) {
//			return relatorioDAO.relatorioTitulosComRetorno(usuario, instituicao, dataInicio, dataFim);
//		} else if (situacaoTitulosRelatorio.equals(TipoRelatorio.PAGOS)) {
//			return relatorioDAO.relatorioTitulosPagos(usuario, instituicao, dataInicio, dataFim);
//		} else if (situacaoTitulosRelatorio.equals(TipoRelatorio.PROTESTADOS)) {
//			return relatorioDAO.relatorioTitulosProtestados(usuario, instituicao, dataInicio, dataFim);
//		} else if (situacaoTitulosRelatorio.equals(TipoRelatorio.RETIRADOS_DEVOLVIDOS)) {
//			return relatorioDAO.relatorioTitulosRetiradosDevolvidos(usuario, instituicao, dataInicio, dataFim);
//		} else if (situacaoTitulosRelatorio.equals(TipoRelatorio.DESISTÊNCIA_DE_PROTESTO)) {
//			return relatorioDAO.relatorioTitulosPedidosDeDesistencias(usuario, instituicao, dataInicio, dataFim);
//		}
//		
//		return null;
	}
	
	public JasperPrint relatorioSintetico(Instituicao instituicao, TipoArquivoEnum tipoArquivo, LocalDate dataInicio, LocalDate dataFim)
	        throws JRException, HibernateException, SQLException, IOException {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.instituicao = instituicao;
		return chamarRelatorioSinteticoPorTipoArquivo(tipoArquivo);
	}

	public JasperPrint relatorioSinteticoPorMunicipio(Municipio municipio, TipoArquivoEnum tipoArquivo, LocalDate dataInicio, LocalDate dataFim)
	        throws JRException, HibernateException, SQLException, IOException {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.pracaProtesto = municipio;
		return chamarRelatorioSinteticoPorTipoArquivoDeMunicipios(tipoArquivo);
	}
	
	private JasperPrint chamarRelatorioSinteticoPorTipoArquivo(TipoArquivoEnum tipoArquivo) throws JRException, IOException {
		List<RelatorioSinteticoBean> beans = new ArrayList<RelatorioSinteticoBean>();
		JasperPrint jasperPrint = null;

		parametros.put("BANCO", getInstituicao().getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			beans = relatorioDAO.relatorioDeRemessaSintetico(getInstituicao(), getDataInicio(), getDataFim());
			JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
			JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../relatorio/RelatorioSinteticoRemessa.jrxml"));
			jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			beans = relatorioDAO.relatorioDeConfirmacaoSintetico(getInstituicao(), getDataInicio(), getDataFim());
//			jasperPrint = RelatorioSinteticoUtil.relatorioSinteticoDeConfirmacao(beans, instituicao, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			beans = relatorioDAO.relatorioDeRetornoSintetico(getInstituicao(), getDataInicio(), getDataFim());
			JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
			JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../relatorio/RelatorioSinteticoRetorno.jrxml"));
			jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
		}

		if (beans.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");

		return jasperPrint;
	}

	private JasperPrint chamarRelatorioSinteticoPorTipoArquivoDeMunicipios(TipoArquivoEnum tipoArquivo) throws JRException, IOException {
		List<RelatorioSinteticoBean> beans = new ArrayList<RelatorioSinteticoBean>();
		JasperPrint jasperPrint = null;

		parametros.put("MUNICIPIO", pracaProtesto.getNomeMunicipio());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			beans = relatorioDAO.relatorioDeRemessaSinteticoPorMunicipio(getPracaProtesto(), getDataInicio(), getDataFim());
			JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
			JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../relatorio/RelatorioSinteticoRemessaPorMunicipio.jrxml"));
			jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			beans = relatorioDAO.relatorioDeConfirmacaoSinteticoPorMunicipio(getPracaProtesto(), getDataInicio(), getDataFim());
			JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
			JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../relatorio/RelatorioSinteticoConfirmacaoPorMunicipio.jrxml"));
			jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			beans = relatorioDAO.relatorioDeRetornoSinteticoPorMunicipio(getPracaProtesto(), getDataInicio(), getDataFim());
			JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
			JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../relatorio/RelatorioSinteticoRetornoPorMunicipio.jrxml"));
			jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
		}

		if (beans.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");

		return jasperPrint;
	}
	
	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public LocalDate getDataFim() {
		return dataFim;
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public Municipio getPracaProtesto() {
		return pracaProtesto;
	}
}
