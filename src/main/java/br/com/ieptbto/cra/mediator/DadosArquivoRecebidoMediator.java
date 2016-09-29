package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.DadosArquivoRecebidoDao;
import br.com.ieptbto.cra.entidade.DadosArquivoRecebido;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.ZipFile;

/**
 * 
 * @author Leandro
 *
 */
@Service
public class DadosArquivoRecebidoMediator extends BaseMediator {

	@Autowired
	private DadosArquivoRecebidoDao dadosArquivoRecebidoDao;

	/**
	 * Método responsável por salvar os dados recebido pelo WS
	 * 
	 * @param dadosArquivoRecebido
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public DadosArquivoRecebido salvarDados(DadosArquivoRecebido dadosArquivoRecebido) {
		return dadosArquivoRecebidoDao.salvar(dadosArquivoRecebido);
	}

	public List<DadosArquivoRecebido> buscarDados(CraAcao acao, Date dataInicio, Date dataFim) {
		return dadosArquivoRecebidoDao.buscarDados(acao, dataInicio, dataFim);
	}

	public File downloadConteudoRequisicao(Usuario user, DadosArquivoRecebido requisicao) {

		try {
			String pathDiretorioIdInstituicao = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + user.getInstituicao().getId();
			String pathDiretorioIdUsuario = pathDiretorioIdInstituicao + ConfiguracaoBase.BARRA + user.getId();

			File diretorioBaseInstituicao = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO);
			File diretorioIdUsuario = new File(pathDiretorioIdUsuario);

			if (!diretorioBaseInstituicao.exists()) {
				diretorioBaseInstituicao.mkdirs();
			}
			if (!diretorioIdUsuario.exists()) {
				diretorioIdUsuario.mkdirs();
			}

			String nomeArquivoDownload = "CRA_PROC_" + DataUtil.localDateToStringddMMyyyy(new LocalDate(requisicao.getDataRecebimento())) + "_SERVICE_"
					+ requisicao.getServico().toUpperCase() + "_" + requisicao.getLogin().toUpperCase() + ".xml";
			File file = new File(pathDiretorioIdUsuario + ConfiguracaoBase.BARRA + nomeArquivoDownload);

			byte[] conteudo = ZipFile.unzip(requisicao.getDados());
			FileOutputStream in = new FileOutputStream(file);
			in.write(conteudo);
			in.close();

			return file;
		} catch (IOException ex) {
			logger.info(ex.getMessage(), ex);
			throw new InfraException("Não foi possível favor o download do contéudo da requisição! Favor entrar em contato com a CRA...");
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw new InfraException("Não foi possível favor o download do contéudo da requisição! Favor entrar em contato com a CRA...");
		}
	}
}