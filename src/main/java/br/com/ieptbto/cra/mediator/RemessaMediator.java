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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
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
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.DecoderString;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;
import br.com.ieptbto.cra.webservice.VO.ComarcaDetalhamentoSerpro;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;
import br.com.ieptbto.cra.webservice.VO.MensagemXmlSerpro;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RemessaMediator {

	protected static final Logger logger = Logger.getLogger(RemessaMediator.class);

	@Autowired
	RemessaDAO remessaDAO;
	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	DesistenciaDAO desistenciaDAO;
	@Autowired
	CancelamentoDAO cancelamentoDAO;
	@Autowired
	MunicipioDAO municipioDAO;
	@Autowired
	AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	@Autowired
	ConversorRemessaArquivo conversorRemessaArquivo;
	@Autowired
	ProcessadorArquivo processadorArquivo;

	private List<Exception> erros;

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
	public RemessaVO buscarRemessaParaCartorio(Remessa remessa, Instituicao instituicao, String nomeArquivo) {
		if (nomeArquivo.startsWith(TipoArquivoEnum.REMESSA.getConstante())) {
			remessa = remessaDAO.baixarArquivoCartorioRemessa(instituicao, nomeArquivo);
		} else if (nomeArquivo.startsWith(TipoArquivoEnum.CONFIRMACAO.getConstante())) {
			remessa = remessaDAO.baixarArquivoCartorioConfirmacao(instituicao, nomeArquivo);
		} else if (nomeArquivo.startsWith(TipoArquivoEnum.RETORNO.getConstante())) {
			remessa = remessaDAO.baixarArquivoCartorioRetorno(instituicao, nomeArquivo);
		}

		if (remessa == null) {
			return null;
		}
		remessa.setStatusRemessa(StatusRemessa.RECEBIDO);
		remessaDAO.alterarSituacaoRemessa(remessa);

		ArrayList<Arquivo> arquivos = new ArrayList<>();
		arquivos.add(remessa.getArquivo());
		return conversorRemessaArquivo.converterRemessaVO(remessa);
	}

	/**
	 * 
	 * @param arquivoRecebido
	 * @param usuario
	 * @param nomeArquivo
	 * @return object
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Object processarArquivoXML(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo) {
		Arquivo arquivo = processarArquivoXMLManual(arquivoRecebido, usuario, nomeArquivo);

		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			return gerarRespostaSerpro(arquivo, usuario);
		}
		return gerarResposta(arquivo, usuario);
	}

	/**
	 * 
	 * @param arquivoRecebido
	 * @param usuario
	 * @param nomeArquivo
	 * @return
	 */
	public Arquivo processarArquivoXMLManual(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo) {
		Arquivo arquivo = new Arquivo();
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());

		logger.info("Iniciar processador do arquivo " + nomeArquivo);
		processadorArquivo.processarArquivo(arquivoRecebido, usuario, nomeArquivo, arquivo, getErros());
		logger.info("Fim processador do arquivo " + nomeArquivo);
		arquivo = salvarArquivo(arquivo, usuario);
		return arquivo;
	}

	private MensagemXml gerarResposta(Arquivo arquivo, Usuario usuario) {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		MensagemXml mensagemRetorno = new MensagemXml();
		Descricao desc = new Descricao();
		Detalhamento detal = new Detalhamento();
		detal.setMensagem(mensagens);

		mensagemRetorno.setDescricao(desc);
		mensagemRetorno.setDetalhamento(detal);
		mensagemRetorno.setCodigoFinal("0000");
		mensagemRetorno.setDescricaoFinal("Arquivo processado com sucesso");

		desc.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		desc.setTipoArquivo(Descricao.XML_UPLOAD_REMESSA);
		desc.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		desc.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		desc.setUsuario(usuario.getNome());

		for (Remessa remessa : arquivo.getRemessas()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(getMunicipio(remessa));
			mensagem.setDescricao(formatarMensagemRetorno(remessa));
			mensagens.add(mensagem);
		}

		for (Exception ex : getErros()) {
			XmlCraException exception = XmlCraException.class.cast(ex);
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo(exception.getErro().getCodigo());
			mensagem.setMunicipio(exception.getCodigoIbge());
			mensagem.setDescricao("Município: " + exception.getCodigoIbge() + " - " + exception.getMunicipio() + " - "
					+ exception.getErro().getDescricao());
			mensagens.add(mensagem);
		}

		return mensagemRetorno;
	}

	private MensagemXmlSerpro gerarRespostaSerpro(Arquivo arquivo, Usuario usuario) {
		MensagemXmlSerpro msgSucesso = new MensagemXmlSerpro();
		msgSucesso.setNomeArquivo(arquivo.getNomeArquivo());

		List<ComarcaDetalhamentoSerpro> listaComarcas = new ArrayList<ComarcaDetalhamentoSerpro>();
		for (Remessa remessa : arquivo.getRemessas()) {
			ComarcaDetalhamentoSerpro comarcaDetalhamento = new ComarcaDetalhamentoSerpro();
			comarcaDetalhamento.setCodigoMunicipio(remessa.getCabecalho().getCodigoMunicipio());
			comarcaDetalhamento
					.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
			comarcaDetalhamento.setRegistro(StringUtils.EMPTY);

			CodigoErro codigoErroSerpro = getCodigoErroSucessoSerpro(arquivo.getNomeArquivo());
			comarcaDetalhamento.setCodigo(codigoErroSerpro.getCodigo());
			comarcaDetalhamento.setOcorrencia(codigoErroSerpro.getDescricao());
			comarcaDetalhamento.setTotalRegistros(remessa.getCabecalho().getQtdRegistrosRemessa());
			listaComarcas.add(comarcaDetalhamento);
		}

		msgSucesso.setComarca(listaComarcas);
		return msgSucesso;
	}

	private CodigoErro getCodigoErroSucessoSerpro(String nomeArquivo) {
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);

		CodigoErro codigoErro = null;
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_SUCESSO_REMESSA;
		} else if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)
				|| TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO;
		} else {
			codigoErro = CodigoErro.CRA_SUCESSO;
		}
		return codigoErro;
	}

	private String getMunicipio(Remessa remessa) {
		if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString();
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())
				|| TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return remessa.getCabecalho().getNumeroCodigoPortador();
		}
		return StringUtils.EMPTY;
	}

	private String formatarMensagemRetorno(Remessa remessa) {
		if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return "Município: " + remessa.getInstituicaoDestino().getMunicipio().getCodigoIBGE().toString() + " - "
					+ remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio() + " - "
					+ remessa.getCabecalho().getQtdTitulosRemessa() + " Títulos.";
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())
				|| TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
			return "Instituicao: " + remessa.getInstituicaoDestino().getNomeFantasia() + " - "
					+ remessa.getCabecalho().getQtdTitulosRemessa() + " títulos confirmados.";
		}
		return StringUtils.EMPTY;

	}

	public Arquivo salvarArquivo(Arquivo arquivo, Usuario usuario) {
		return arquivoDAO.salvar(arquivo, usuario, new ArrayList<Exception>());
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

	public File baixarRemessaTXT(Usuario usuario, Remessa remessa) {
		TipoInstituicaoCRA tipoInstituicaoUsuario = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (!tipoInstituicaoUsuario.equals(TipoInstituicaoCRA.CRA) && !remessa.getStatusRemessa().equals(StatusRemessa.ENVIADO)) {
			remessa = remessaDAO.buscarPorPK(remessa, Remessa.class);
			remessa.setStatusRemessa(StatusRemessa.RECEBIDO);
			remessaDAO.alterarSituacaoRemessa(remessa);
		}
		if (tipoInstituicaoUsuario.equals(TipoInstituicaoCRA.CARTORIO) && remessa.getDevolvidoPelaCRA().equals(true)) {
			throw new InfraException("O arquivo " + remessa.getArquivo().getNomeArquivo() + " já foi devolvido pela CRA !");
		}

		TipoArquivoEnum tipoArquivo = remessa.getArquivo().getTipoArquivo().getTipoArquivo();
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			remessa = remessaDAO.baixarArquivoCartorioRemessa(remessa);
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			remessa = remessaDAO.baixarArquivoCartorioConfirmacao(remessa);
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			remessa = remessaDAO.baixarArquivoCartorioRetorno(remessa);
		}
		return processadorArquivo.processarArquivoTXT(remessa);
	}

	public int getNumeroSequencialConvenio(Instituicao convenio, Instituicao instituicaoDestino) {
		return remessaDAO.getNumeroSequencialConvenio(convenio, instituicaoDestino);
	}

	@Transactional
	public Arquivo arquivosPendentes(Instituicao instituicao) {
		instituicao.setMunicipio(municipioDAO.buscarPorPK(instituicao.getMunicipio(), Municipio.class));

		// List<Remessa> remessas =
		// remessaDAO.confirmacoesPendentes(instituicao);
		List<Remessa> remessas = new ArrayList<Remessa>();
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
		return conversorRemessaArquivo.converter(arquivos);
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

					return new File(
							pathDiretorioIdArquivo + ConfiguracaoBase.BARRA + nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private void decodificarArquivosAnexos(Usuario user, String path, Remessa remessa) {

		try {
			List<Titulo> titulos = tituloDAO.carregarTitulosGenerico(remessa);
			if (!titulos.isEmpty()) {
				for (Titulo titulo : titulos) {
					TituloRemessa tituloRemessa = TituloRemessa.class.cast(titulo);
					if (tituloRemessa.getAnexo() != null) {
						DecoderString decoderString = new DecoderString();
						String nomeArquivoZip = tituloRemessa.getNomeDevedor() + "_"
								+ tituloRemessa.getNumeroTitulo().replaceAll("\\\\", "").replaceAll("\\/", "");

						decoderString.decode(tituloRemessa.getAnexo().getDocumentoAnexo(),
								ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId()
										+ ConfiguracaoBase.BARRA + remessa.getArquivo().getId() + ConfiguracaoBase.BARRA + remessa.getId()
										+ ConfiguracaoBase.BARRA,
								nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
					}
				}
			}

		} catch (FileNotFoundException e) {
			logger.info("O arquivo ZIP em anexo não pode ser criado.");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("O arquivo ZIP em anexo não pode ser criado.");
			e.printStackTrace();
		}
	}

	public List<Anexo> verificarAnexosRemessa(Remessa remessa) {
		return remessaDAO.verificarAnexosRemessa(remessa);
	}

	public List<Remessa> buscarRemssasPorArquivo(Arquivo arquivo) {
		return remessaDAO.buscarRemessasPorArquivo(arquivo);
	}
}