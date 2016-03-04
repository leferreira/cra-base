package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.bean.TituloBean;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RelatorioMediator {
	
	@Autowired
	private RetornoDAO retornoDAO;
	@Autowired
	private TituloDAO tituloDAO;
	private HashMap<String, Object> params;
	
	private void initParams() {
		this.params = new HashMap<String, Object>();
	}
	
	public JasperPrint relatorioRemessa(JasperReport jasperReport, Remessa remessa, Instituicao instituicaoCorrente) throws JRException {
		initParams();
		
		List<TituloRemessa> titulos = tituloDAO.buscarTitulosPorRemessa(remessa, remessa.getInstituicaoOrigem());
	
		if (titulos.isEmpty()) {
			throw new InfraException("Não foi possível gerar o relatório. O arquivo não contém titulos !");
		}
		BigDecimal valorTotal = new BigDecimal(0);
		List<TituloBean> titulosJR = new ArrayList<TituloBean>();
		for (TituloRemessa tituloRemessa : titulos) {
			TituloBean tituloJR = new TituloBean();
			tituloJR.parseToTituloRemessa(tituloRemessa);
			valorTotal = valorTotal.add(tituloRemessa.getSaldoTitulo());
			titulosJR.add(tituloJR);
		}
		params.put("NOME_ARQUIVO", remessa.getArquivo().getNomeArquivo());
		params.put("DATA_ENVIO", DataUtil.localDateToString(remessa.getDataRecebimento()));
		params.put("INSTITUICAO", remessa.getInstituicaoOrigem().getNomeFantasia().toUpperCase());
		params.put("TOTAL_TITULOS", Integer.class.cast(titulosJR.size()));
		params.put("VALOR_TOTAL", valorTotal);
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulosJR);
		return JasperFillManager.fillReport(jasperReport, params, beanCollection);
	}
	
	public JasperPrint relatorioRemessa(Arquivo arquivo, Instituicao instituicaoCorrente) {
		initParams();
		
		return null;
	}
	
	public JasperPrint relatorioConfirmacao(JasperReport jasperReport, Remessa remessa, Instituicao instituicaoCorrente) throws JRException {
		initParams();
		
		List<TituloRemessa> titulos = tituloDAO.buscarTitulosPorRemessa(remessa, instituicaoCorrente);

		if (titulos.isEmpty()) {
			throw new InfraException("Não foi possível gerar o relatório. O arquivo não contém titulos !");
		}
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
		params.put("NOME_ARQUIVO", remessa.getArquivo().getNomeArquivo());
		params.put("DATA_ENVIO", DataUtil.localDateToString(remessa.getDataRecebimento()));
		params.put("INSTITUICAO", remessa.getInstituicaoOrigem().getNomeFantasia().toUpperCase());
		params.put("QTD_TITULOS", Integer.class.cast(titulosJR.size()));
		params.put("QTD_APONTADOS", numeroApontados);
		params.put("QTD_DEVOLVIDOS", numeroDevolvidos);
		params.put("TOTAL_APONTADOS", valorTotal);
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulosJR);
		return JasperFillManager.fillReport(jasperReport, params, beanCollection);
	}
	
	public JasperPrint relatorioConfirmacao(Arquivo arquivo, Instituicao instituicaoCorrente) {
		initParams();
		
		return null;
	}
	
	public JasperPrint relatorioRetorno(JasperReport jasperReport, Remessa remessa, Instituicao instituicaoCorrente) throws JRException {
		initParams();
		
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
		params.put("NOME_ARQUIVO", remessa.getArquivo().getNomeArquivo());
		params.put("DATA_ENVIO", DataUtil.localDateToString(remessa.getDataRecebimento()));
		params.put("INSTITUICAO", remessa.getInstituicaoOrigem().getNomeFantasia().toUpperCase());
		params.put("TOTAL_TITULOS", Integer.class.cast(titulosJR.size()));
		params.put("TOTAL_PAGOS", retornoDAO.buscarValorDeTitulosPagos(remessa));
		params.put("TOTAL_DEMAIS_DESPESAS", retornoDAO.buscarValorDemaisDespesas(remessa));
		params.put("TOTAL_CUSTAS", retornoDAO.buscarValorDeCustasCartorio(remessa));
		params.put("QTD_PAGOS", numeroPagos);
		params.put("QTD_PROTESTADOS_RETIRADOS", numeroProtestadosRetirados);

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulosJR);
		return JasperFillManager.fillReport(jasperReport, params, beanCollection);
	}

	public JasperPrint relatorioRetorno(JasperReport jasperReport, Arquivo arquivo, Instituicao instituicaoCorrente) throws JRException {
		initParams();
		return null;
	}
}
