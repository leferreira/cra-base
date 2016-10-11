package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DecoderString;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RemessaMediator extends BaseMediator {

	protected static final Logger logger = Logger.getLogger(RemessaMediator.class);

	@Autowired
	private RemessaDAO remessaDAO;
	@Autowired
	private TituloDAO tituloDAO;

	@Transactional
	public CabecalhoRemessa carregarCabecalhoRemessaPorId(CabecalhoRemessa cabecalhoRemessa) {
		return remessaDAO.buscarPorPK(cabecalhoRemessa, CabecalhoRemessa.class);
	}

	@Transactional
	public Remessa carregarRemessaPorId(Remessa remessa) {
		return remessaDAO.buscarPorPK(remessa, Remessa.class);
	}

	@Transactional
	public Rodape carregarRodapeRemessaPorId(Rodape rodape) {
		return remessaDAO.buscarPorPK(rodape, Rodape.class);
	}

	public List<Remessa> buscarRemessas(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio, List<TipoArquivoEnum> tiposArquivo, List<SituacaoArquivo> situacoesArquivos) {
		return remessaDAO.buscarRemessaAvancado(usuario, nomeArquivo, dataInicio, dataFim, tipoInstituicao, bancoConvenio, cartorio, tiposArquivo,
				situacoesArquivos);
	}

	public int getNumeroSequencialConvenio(Instituicao convenio, Instituicao instituicaoDestino) {
		return remessaDAO.getNumeroSequencialConvenio(convenio, instituicaoDestino);
	}

	public Remessa alterarParaDevolvidoPelaCRA(Remessa remessa) {
		remessa = carregarRemessaPorId(remessa);
		remessa.setDevolvidoPelaCRA(true);
		remessa.setStatusRemessa(StatusRemessa.RECEBIDO);
		return remessaDAO.update(remessa);
	}

	/**
	 * Método para download de arquivos anexos do título
	 * 
	 * @param user
	 * @param remessa
	 * @return
	 */
	public File decodificarAnexoTitulo(Usuario user, TituloRemessa tituloRemessa) {
		String pathDiretorioIdRemessa = criarDiretoriosAnexos(user, tituloRemessa.getRemessa());
		File diretorioPath = new File(pathDiretorioIdRemessa);

		if (!diretorioPath.exists()) {
			diretorioPath.mkdirs();
		}

		String nomeArquivoZip =
				tituloRemessa.getNomeDevedor().replace(" ", "_").replace("/", "") + "_" + tituloRemessa.getNumeroTitulo().replace("\\", "").replace("/", "");
		try {
			if (tituloRemessa.getAnexo() != null) {
				DecoderString decoderString = new DecoderString();
				decoderString.decode(tituloRemessa.getAnexo().getDocumentoAnexo(), pathDiretorioIdRemessa,
						nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
			}
			return new File(pathDiretorioIdRemessa + ConfiguracaoBase.BARRA + nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);

		} catch (FileNotFoundException e) {
			logger.info(e);
			throw new InfraException("Não foi possível gerar o arquivo de anexos! Por favor, entre em contato com a CRA...");
		} catch (IOException e) {
			logger.info(e);
			throw new InfraException("Não foi possível gerar o arquivo de anexos! Por favor, entre em contato com a CRA...");
		}
	}

	/**
	 * Método para download de arquivos anexos da remessa
	 * 
	 * @param user
	 * @param remessa
	 * @return
	 */
	@SuppressWarnings("resource")
	public File processarArquivosAnexos(Usuario user, Remessa remessa) {
		String pathDiretorioArquivo =
				ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId() + ConfiguracaoBase.BARRA + remessa.getArquivo().getId();

		String pathDiretorioIdRemessa = criarDiretoriosAnexos(user, remessa);
		File diretorioRemessa = new File(pathDiretorioIdRemessa);
		if (!diretorioRemessa.exists()) {
			diretorioRemessa.mkdirs();

			decodificarArquivosAnexos(user, pathDiretorioIdRemessa, remessa);
		}

		try {
			if (diretorioRemessa.exists()) {
				if (!Arrays.asList(diretorioRemessa.listFiles()).isEmpty()) {

					String nomeArquivoZip = remessa.getArquivo().getNomeArquivo().replace(".", "_") + "_" + remessa.getCabecalho().getCodigoMunicipio();
					FileOutputStream fileOutputStream =
							new FileOutputStream(pathDiretorioArquivo + ConfiguracaoBase.BARRA + nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
					ZipOutputStream zipOut = new ZipOutputStream(fileOutputStream);

					for (File arq : diretorioRemessa.listFiles()) {
						zipOut.putNextEntry(new ZipEntry(arq.getName().toString()));
						FileInputStream fis = new FileInputStream(arq);
						int content;
						while ((content = fis.read()) != -1) {
							zipOut.write(content);
						}
						zipOut.closeEntry();
					}
					zipOut.close();

					return new File(pathDiretorioArquivo + ConfiguracaoBase.BARRA + nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
				}
			}
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
			throw new InfraException("Não foi possível fazer o download dos arquivos anexos a remessa! Favor entrar em contato com a CRA...");
		}
		return null;
	}

	private String criarDiretoriosAnexos(Usuario user, Remessa remessa) {
		String pathDiretorioIdInstituicao = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId();
		String pathDiretorioIdArquivo = pathDiretorioIdInstituicao + ConfiguracaoBase.BARRA + remessa.getArquivo().getId();
		String pathDiretorioIdRemessa = pathDiretorioIdArquivo + ConfiguracaoBase.BARRA + remessa.getId();

		File diretorioBaseArquivo = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioBaseInstituicao = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO);
		File diretorioInstituicaoEnvio = new File(pathDiretorioIdInstituicao);
		File diretorioArquivo = new File(pathDiretorioIdArquivo);

		if (!diretorioBaseArquivo.exists()) {
			diretorioBaseArquivo.mkdirs();
		}
		if (!diretorioBaseInstituicao.exists()) {
			diretorioBaseInstituicao.mkdirs();
		}
		if (!diretorioInstituicaoEnvio.exists()) {
			diretorioInstituicaoEnvio.mkdirs();
		}
		if (!diretorioArquivo.exists()) {
			diretorioArquivo.mkdirs();
		}
		return pathDiretorioIdRemessa;
	}

	private void decodificarArquivosAnexos(Usuario user, String pathDiretorioRemessa, Remessa remessa) {

		try {
			List<TituloRemessa> titulos = tituloDAO.carregarTitulosRemessaComDocumentosAnexos(remessa);
			if (!titulos.isEmpty()) {
				for (TituloRemessa tituloRemessa : titulos) {
					if (tituloRemessa.getAnexo() != null) {
						DecoderString decoderString = new DecoderString();
						String nomeArquivoZip = tituloRemessa.getNomeDevedor().replace(" ", "_").replace("/", "") + "_"
								+ tituloRemessa.getNumeroTitulo().replace("\\", "").replace("/", "");

						decoderString.decode(tituloRemessa.getAnexo().getDocumentoAnexo(), pathDiretorioRemessa + ConfiguracaoBase.BARRA,
								nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
					}
				}
			}

		} catch (FileNotFoundException e) {
			logger.info("O arquivo ZIP em anexo não pode ser criado.");
			throw new InfraException("Não foi possível gerar o arquivo de anexos! Favor entrar em contato com a CRA...");
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
			throw new InfraException("Não foi possível gerar o arquivo de anexos! Favor entrar em contato com a CRA...");
		}
	}

	public Anexo verificarAnexosRemessa(Remessa remessa) {
		return remessaDAO.verificarAnexosRemessa(remessa);
	}

	public List<Remessa> buscarRemssasPorArquivo(Arquivo arquivo) {
		return remessaDAO.buscarRemessasPorArquivo(arquivo);
	}
}