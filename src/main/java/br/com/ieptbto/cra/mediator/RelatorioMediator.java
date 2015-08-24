package br.com.ieptbto.cra.mediator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.hibernate.HibernateException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.RelatorioDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.ireport.RelatorioUtil;
import br.com.ieptbto.cra.ireport.SinteticoJRDataSource;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RelatorioMediator {

	@Autowired
	RemessaDAO remessaDao;
	@Autowired
	RelatorioDAO relatorioDao;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TituloDAO tituloDao;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Instituicao instituicao;
	private Municipio pracaProtesto;

	public JasperPrint novoRelatorioSintetico(Instituicao instituicao, TipoArquivoEnum tipoArquivo, LocalDate dataInicio, LocalDate dataFim)
	        throws JRException, HibernateException, SQLException, IOException {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.instituicao = instituicao;
		return chamarRelatorioSinteticoPorTipoArquivo(tipoArquivo);
	}

	public JasperPrint novoRelatorioSinteticoPorMunicipio(Municipio municipio, TipoArquivoEnum tipoArquivo, LocalDate dataInicio, LocalDate dataFim)
	        throws JRException, HibernateException, SQLException, IOException {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.pracaProtesto = municipio;
		return chamarRelatorioSinteticoPorTipoArquivoDeMunicipios(tipoArquivo);
	}

	private JasperPrint chamarRelatorioSinteticoPorTipoArquivo(TipoArquivoEnum tipoArquivo) throws JRException, IOException {
		List<SinteticoJRDataSource> beans = new ArrayList<SinteticoJRDataSource>();
		JasperPrint jasperPrint = null;

		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			beans = relatorioDao.relatorioDeRemessaSintetico(instituicao, dataInicio, dataFim);
			jasperPrint = RelatorioUtil.relatorioSinteticoDeRemessa(beans, instituicao, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			beans = relatorioDao.relatorioDeConfirmacaoSintetico(instituicao, dataInicio, dataFim);
			jasperPrint = RelatorioUtil.relatorioSinteticoDeConfirmacao(beans, instituicao, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			beans = relatorioDao.relatorioDeRetornoSintetico(instituicao, dataInicio, dataFim);
			jasperPrint = RelatorioUtil.relatorioSinteticoDeRetorno(beans, instituicao, dataInicio, dataFim);
		}

		if (beans.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");

		return jasperPrint;
	}

	private JasperPrint chamarRelatorioSinteticoPorTipoArquivoDeMunicipios(TipoArquivoEnum tipoArquivo) throws JRException, IOException {
		List<SinteticoJRDataSource> beans = new ArrayList<SinteticoJRDataSource>();
		JasperPrint jasperPrint = null;

		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			beans = relatorioDao.relatorioDeRemessaSinteticoPorMunicipio(pracaProtesto, dataInicio, dataFim);
			jasperPrint = RelatorioUtil.relatorioSinteticoDeRemessaPorMunicipio(beans, pracaProtesto, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			beans = relatorioDao.relatorioDeConfirmacaoSinteticoPorMunicipio(pracaProtesto, dataInicio, dataFim);
			jasperPrint = RelatorioUtil.relatorioSinteticoDeConfirmacaoPorMunicipio(beans, pracaProtesto, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			beans = relatorioDao.relatorioDeRetornoSinteticoPorMunicipio(pracaProtesto, dataInicio, dataFim);
			jasperPrint = RelatorioUtil.relatorioSinteticoDeRetornoPorMunicipio(beans, pracaProtesto, dataInicio, dataFim);
		}

		if (beans.isEmpty())
			throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");

		return jasperPrint;
	}

	public List<TituloRemessa> buscarTitulosParaRelatorio(Instituicao instituicao, TipoRelatorio situacaoTitulos, LocalDate dataInicio, LocalDate dataFim, Usuario usuario) {
		return tituloDao.buscarTitulosParaRelatorio(instituicao, situacaoTitulos, dataInicio, dataFim, usuario);
	}
}
