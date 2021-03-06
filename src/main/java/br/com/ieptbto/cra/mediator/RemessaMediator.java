package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DecoderString;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RemessaMediator extends BaseMediator {

	@Autowired
	RemessaDAO remessaDAO;
	@Autowired
	TituloMediator tituloMediator;

	@Transactional
	public CabecalhoRemessa carregarCabecalhoRemessaPorId(CabecalhoRemessa cabecalhoRemessa) {
		return remessaDAO.buscarPorPK(cabecalhoRemessa, CabecalhoRemessa.class);
	}

	@Transactional
	public Remessa buscarRemessaPorPK(Remessa remessa) {
		return remessaDAO.buscarPorPK(remessa, Remessa.class);
	}
	
	@Transactional
	public Remessa buscarRemessaPorPK(Integer id) {
		return remessaDAO.buscarPorPK(id, Remessa.class);
	}

	@Transactional
	public Rodape carregarRodapeRemessaPorId(Rodape rodape) {
		return remessaDAO.buscarPorPK(rodape, Rodape.class);
	}

	public List<Remessa> buscarRemessas(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio, List<TipoArquivoFebraban> tiposArquivo, List<StatusDownload> statusDownload) {
		return remessaDAO.buscarRemessaAvancado(usuario, nomeArquivo, dataInicio, dataFim, tipoInstituicao, bancoConvenio, cartorio, tiposArquivo, statusDownload);
	}

	public int getNumeroSequencialConvenio(Instituicao convenio, Instituicao instituicaoDestino) {
		return remessaDAO.getNumeroSequencialConvenio(convenio, instituicaoDestino);
	}

	public Remessa alterarParaDevolvidoPelaCRA(Remessa remessa) {
		remessa = buscarRemessaPorPK(remessa);
		remessa.setDevolvidoPelaCRA(true);
		remessa.setStatusDownload(StatusDownload.RECEBIDO);
		return remessaDAO.update(remessa);
	}

	/**
	 * Método para download de arquivos anexos do título
	 * 
	 * @param user
	 * @param remessa
	 * @return
	 */
	public File decodificarAnexoTitulo(Usuario user, TituloRemessa tituloRemessa, Anexo anexo) {
		String pathDiretorioIdRemessa = criarDiretoriosAnexos(user, tituloRemessa.getRemessa());
		File diretorioPath = new File(pathDiretorioIdRemessa);

		if (!diretorioPath.exists()) {
			diretorioPath.mkdirs();
		}

		String nomeArquivoZip =
				tituloRemessa.getNomeDevedor().replace(" ", "_").replace("/", "") + "_" + tituloRemessa.getNumeroTitulo().replace("\\", "").replace("/", "");
		try {
			if (anexo != null) {
				DecoderString decoderString = new DecoderString();
				decoderString.decode(anexo.getDocumentoAnexo(), pathDiretorioIdRemessa, nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
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
		}
		decodificarArquivosAnexos(user, pathDiretorioIdRemessa, remessa);

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
			List<TituloRemessa> titulos = tituloMediator.carregarTitulos(remessa);
			if (titulos != null) {
				for (TituloRemessa tituloRemessa : titulos) {
					String nomeArquivoZip = tituloRemessa.getNomeDevedor().replace(" ", "_").replace("/", "") + "_"
							+ tituloRemessa.getNumeroTitulo().replace("\\", "").replace("/", "") + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP;
					File file = new File(pathDiretorioRemessa + ConfiguracaoBase.BARRA + nomeArquivoZip);
					if (!file.exists()) {
						Anexo anexo = tituloMediator.buscarAnexo(tituloRemessa);
						if (anexo != null) {
							DecoderString decoderString = new DecoderString();

							decoderString.decode(anexo.getDocumentoAnexo(), pathDiretorioRemessa + ConfiguracaoBase.BARRA, nomeArquivoZip);
						}
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