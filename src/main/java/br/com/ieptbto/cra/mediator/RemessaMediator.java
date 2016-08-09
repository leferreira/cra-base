package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.conversor.arquivo.ConversorRemessaArquivo;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.AutorizacaoCancelamentoDAO;
import br.com.ieptbto.cra.dao.CancelamentoDAO;
import br.com.ieptbto.cra.dao.DesistenciaDAO;
import br.com.ieptbto.cra.dao.MunicipioDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
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
	private ArquivoDAO arquivoDAO;
	@Autowired
	private TituloDAO tituloDAO;
	@Autowired
	private DesistenciaDAO desistenciaDAO;
	@Autowired
	private CancelamentoDAO cancelamentoDAO;
	@Autowired
	private MunicipioDAO municipioDAO;
	@Autowired
	private AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	@Autowired
	private ConversorRemessaArquivo conversorRemessaArquivo;

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

	public List<Remessa> buscarRemessas(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
			ArrayList<TipoArquivoEnum> tiposArquivo, Usuario usuario, ArrayList<StatusRemessa> situacoes) {
		return remessaDAO.buscarRemessaAvancado(arquivo, municipio, dataInicio, dataFim, usuario, tiposArquivo, situacoes);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RemessaVO buscarRemessaParaCartorio(Usuario usuario, String nomeArquivo) {
		Remessa remessa = null;
		logger.info("Usuario " + usuario.getLogin() + " está buscando a remessa " + nomeArquivo + " na CRA.");
		if (nomeArquivo.startsWith(TipoArquivoEnum.REMESSA.getConstante())) {
			remessa = remessaDAO.baixarArquivoCartorioRemessa(usuario.getInstituicao(), nomeArquivo);
		}

		if (remessa == null) {
			return null;
		}
		remessa.setStatusRemessa(StatusRemessa.RECEBIDO);
		remessaDAO.alterarSituacaoRemessa(remessa);

		ArrayList<Arquivo> arquivos = new ArrayList<>();
		arquivos.add(remessa.getArquivo());
		logger.info("O Usuario " + usuario.getLogin() + " da instituição " + usuario.getInstituicao().getNomeFantasia()
				+ " fez o download do arquivo " + nomeArquivo + " que foi enviado para " + remessa.getInstituicaoDestino().getNomeFantasia() + ".");
		return conversorRemessaArquivo.converterRemessaVO(remessa);
	}

	public int getNumeroSequencialConvenio(Instituicao convenio, Instituicao instituicaoDestino) {
		return remessaDAO.getNumeroSequencialConvenio(convenio, instituicaoDestino);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo arquivosPendentes(Instituicao instituicao) {
		instituicao.setMunicipio(municipioDAO.buscarPorPK(instituicao.getMunicipio(), Municipio.class));

		List<Remessa> remessas = remessaDAO.confirmacoesPendentes(instituicao);
		List<DesistenciaProtesto> desistenciasProtesto = desistenciaDAO.buscarRemessaDesistenciaProtestoPendenteDownload(instituicao);
		List<CancelamentoProtesto> cancelamentoProtesto = cancelamentoDAO.buscarRemessaCancelamentoPendenteDownload(instituicao);
		List<AutorizacaoCancelamento> autorizacaoCancelamento =
				autorizacaoCancelamentoDAO.buscarRemessaAutorizacaoCancelamentoPendenteDownload(instituicao);
		Arquivo arquivo = new Arquivo();
		arquivo.setRemessas(remessas);
		RemessaDesistenciaProtesto remessaDesistenciaProtesto = new RemessaDesistenciaProtesto();
		remessaDesistenciaProtesto.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>(desistenciasProtesto));
		arquivo.setRemessaDesistenciaProtesto(remessaDesistenciaProtesto);
		RemessaAutorizacaoCancelamento remessaAutorizacaoCancelamento = new RemessaAutorizacaoCancelamento();
		remessaAutorizacaoCancelamento.setAutorizacaoCancelamento(autorizacaoCancelamento);
		arquivo.setRemessaAutorizacao(remessaAutorizacaoCancelamento);
		RemessaCancelamentoProtesto remessaCancelamento = new RemessaCancelamentoProtesto();
		remessaCancelamento.setCancelamentoProtesto(cancelamentoProtesto);
		arquivo.setRemessaCancelamentoProtesto(remessaCancelamento);
		return arquivo;
	}

	public List<Remessa> confirmacoesPendentesRelatorio(Instituicao instituicao) {
		return remessaDAO.confirmacoesPendentes(instituicao);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<RemessaVO> buscarArquivos(String nomeArquivo, Instituicao instituicao) {
		Arquivo arquivo = null;
		List<Arquivo> arquivos = new ArrayList<Arquivo>();

		if (nomeArquivo.startsWith(TipoArquivoEnum.CONFIRMACAO.getConstante())) {
			arquivo = arquivoDAO.buscarArquivoInstituicaoConfirmacao(nomeArquivo, instituicao);
		} else if (nomeArquivo.startsWith(TipoArquivoEnum.RETORNO.getConstante())) {
			arquivo = arquivoDAO.buscarArquivoInstituicaoRetorno(nomeArquivo, instituicao);
		}
		if (arquivo == null) {
			return new ArrayList<RemessaVO>();
		}
		StatusArquivo statusArquivo = new StatusArquivo();
		statusArquivo.setSituacaoArquivo(SituacaoArquivo.RECEBIDO);
		statusArquivo.setData(new LocalDateTime());
		arquivo.setStatusArquivo(statusArquivo);
		arquivoDAO.alterarStatusArquivo(arquivo);

		arquivos.add(arquivo);
		return conversorRemessaArquivo.converterParaVO(arquivos);
	}

	public void alterarParaDevolvidoPelaCRA(Remessa remessa) {
		remessa = carregarRemessaPorId(remessa);
		remessa.setDevolvidoPelaCRA(true);
		remessa.setStatusRemessa(StatusRemessa.RECEBIDO);
		remessaDAO.update(remessa);
	}

	@SuppressWarnings("resource")
	public File processarArquivosAnexos(Usuario user, Remessa remessa) {
		String pathDiretorioIdInstituicao = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId();
		String pathDiretorioIdArquivo = pathDiretorioIdInstituicao + ConfiguracaoBase.BARRA + remessa.getArquivo().getId();
		String pathDiretorioIdRemessa = pathDiretorioIdArquivo + ConfiguracaoBase.BARRA + remessa.getId();

		File diretorioBaseArquivo = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioBaseInstituicao = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO);
		File diretorioInstituicaoEnvio = new File(pathDiretorioIdInstituicao);
		File diretorioArquivo = new File(pathDiretorioIdArquivo);
		File diretorioRemessa = new File(pathDiretorioIdRemessa);

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
		if (!diretorioRemessa.exists()) {
			diretorioRemessa.mkdirs();

			decodificarArquivosAnexos(user, pathDiretorioIdRemessa, remessa);
		}

		try {
			if (diretorioRemessa.exists()) {
				if (!Arrays.asList(diretorioRemessa.listFiles()).isEmpty()) {

					String nomeArquivoZip =
							remessa.getArquivo().getNomeArquivo().replace(".", "_") + "_" + remessa.getCabecalho().getCodigoMunicipio();
					FileOutputStream fileOutputStream = new FileOutputStream(
							pathDiretorioIdArquivo + ConfiguracaoBase.BARRA + nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
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

					return new File(pathDiretorioIdArquivo + ConfiguracaoBase.BARRA + nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void decodificarArquivosAnexos(Usuario user, String path, Remessa remessa) {

		try {
			List<TituloRemessa> titulos = tituloDAO.carregarTitulosRemessaComDocumentosAnexos(remessa);
			if (!titulos.isEmpty()) {
				for (TituloRemessa tituloRemessa : titulos) {
					if (tituloRemessa.getAnexo() != null) {
						DecoderString decoderString = new DecoderString();
						String nomeArquivoZip = tituloRemessa.getNomeDevedor().replace(" ", "_").replace("/", "") + "_"
								+ tituloRemessa.getNumeroTitulo().replace("\\", "").replace("/", "");

						decoderString.decode(tituloRemessa.getAnexo().getDocumentoAnexo(),
								ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId() + ConfiguracaoBase.BARRA
										+ remessa.getArquivo().getId() + ConfiguracaoBase.BARRA + remessa.getId() + ConfiguracaoBase.BARRA,
								nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
					}
				}
			}

		} catch (FileNotFoundException e) {
			logger.info("O arquivo ZIP em anexo não pode ser criado.");
			e.printStackTrace();
			throw new InfraException("Não foi possível gerar o arquivo de anexos! Por favor, entre em contato com a CRA...");
		} catch (IOException e) {
			logger.info(e);
			e.printStackTrace();
			throw new InfraException("Não foi possível gerar o arquivo de anexos! Por favor, entre em contato com a CRA...");
		}
	}

	public List<Anexo> verificarAnexosRemessa(Remessa remessa) {
		return remessaDAO.verificarAnexosRemessa(remessa);
	}

	public List<Remessa> buscarRemssasPorArquivo(Arquivo arquivo) {
		return remessaDAO.buscarRemessasPorArquivo(arquivo);
	}
}