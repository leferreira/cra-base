package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.string.Strings;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.bean.TituloTaxaCraBean;
import br.com.ieptbto.cra.dao.RelatorioDAO;
import br.com.ieptbto.cra.dao.TaxaCraDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.XlsUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RelatorioMediator extends BaseMediator {

	@Autowired
	RelatorioDAO relatorioDAO;
	@Autowired
	TaxaCraDAO taxaCraDAO;

	private File arquivoFisico;
	private FileUpload file;

	public List<TituloRemessa> relatorioTitulosPorSituacao(SituacaoTituloRelatorio situacaoTitulo, TipoInstituicaoCRA tipoInstituicao,
			Instituicao instituicao, Instituicao cartorio, LocalDate dataInicio, LocalDate dataFim) {

		if (situacaoTitulo.equals(SituacaoTituloRelatorio.GERAL)) {
			return relatorioDAO.relatorioTitulosGeral(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.SEM_CONFIRMACAO)) {
			return relatorioDAO.relatorioTitulosSemConfirmacao(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.COM_CONFIRMACAO)) {
			return relatorioDAO.relatorioTitulosConfirmadosSemRetorno(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.COM_RETORNO)) {
			return relatorioDAO.relatorioTitulosRetorno(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.PAGOS)) {
			return relatorioDAO.relatorioTitulosPagos(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.PROTESTADOS)) {
			return relatorioDAO.relatorioTitulosProtestados(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS)) {
			return relatorioDAO.relatorioTitulosRetiradosDevolvidos(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.DESISTÊNCIA_DE_PROTESTO)) {
			return relatorioDAO.relatorioTitulosDesistenciaProtesto(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.AUTORIZACAO_CANCELAMENTO)) {
			return relatorioDAO.relatorioTitulosAutorizacaoCancelamento(dataInicio, dataFim, tipoInstituicao, instituicao, cartorio);
		}
		return null;
	}

	public List<TituloTaxaCraBean> relatorioTitulosPagosTaxaCra(Instituicao convenio, Instituicao cartorio, LocalDate dataInicio, LocalDate dataFim) {
		List<TituloRemessa> titulosRemessa = relatorioDAO.relatorioTitulosPagos(dataInicio, dataFim, TipoInstituicaoCRA.CONVENIO, convenio, cartorio);

		List<TituloTaxaCraBean> titulos = new ArrayList<TituloTaxaCraBean>();
		for (TituloRemessa tituloRemessa : titulosRemessa) {
			TituloTaxaCraBean titulo = new TituloTaxaCraBean();
			titulo.parseToTituloRemessa(tituloRemessa);
			titulo.setTaxaCra(taxaCraDAO.buscarTaxaCraVigente(tituloRemessa.getRetorno().getDataOcorrencia()));
			titulos.add(titulo);
		}
		return titulos;
	}

	public List<TituloRemessa> relatorioTitulosPlanilhaPendencias(FileUpload fileUpload) {
		this.file = fileUpload;

		if (getFile() == null) {
			throw new InfraException("A planilha de pendências não pode ser processada! Entre em contato com a CRA !");
		}

		File diretorioTemp = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE);
		if (!diretorioTemp.exists()) {
			diretorioTemp.mkdirs();
		}

		try {
			setArquivoFisico(new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE + fileUpload.getClientFileName()));
			getFile().writeTo(getArquivoFisico());

			File file = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE + fileUpload.getClientFileName());
			Workbook planilha = null;
			FileInputStream fileIn = new FileInputStream(file);

			if (fileUpload.getClientFileName().contains("xlsx")) {
				planilha = new XSSFWorkbook(fileIn);
				fileIn.close();
			} else if (fileUpload.getClientFileName().contains("xls")) {
				planilha = new HSSFWorkbook(fileIn);
				fileIn.close();
			}
			file.delete();
			Sheet worksheet = planilha.getSheetAt(0);
			Row row;

			List<TituloRemessa> titulos = new ArrayList<TituloRemessa>();
			for (int i = 3; i <= worksheet.getLastRowNum(); i++) {
				row = worksheet.getRow(i);
				if (row != null) {
					if (!XlsUtil.isEmptyCell(row, 3) && !XlsUtil.isEmptyCell(row, 4)) {
						String nossoNumero = XlsUtil.getCellToString(row, 3);
						String numeroProtocolo = XlsUtil.getCellToString(row, 4);

						if (!Strings.isEmpty(nossoNumero) || !Strings.isEmpty(numeroProtocolo)) {
							TituloRemessa titulo = relatorioDAO.relatorioTitulosPendentes(nossoNumero, numeroProtocolo);
							if (titulo != null) {
								titulos.add(titulo);
							}
						}
					}
				}
			}
			return titulos;
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + fileUpload.getClientFileName());
		}
	}

	public File getArquivoFisico() {
		if (!arquivoFisico.exists()) {
			try {
				arquivoFisico.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e.getCause());
				throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + arquivoFisico.getName());
			}
		}
		return arquivoFisico;
	}

	public void setArquivoFisico(File arquivo) {
		if (this.arquivoFisico != null && this.arquivoFisico.exists()) {
			this.arquivoFisico.delete();
		}
		this.arquivoFisico = arquivo;
	}

	public FileUpload getFile() {
		return file;
	}

	public void setFile(FileUpload file) {
		this.file = file;
	}
}
