package br.com.ieptbto.cra.mediator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
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
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.MunicipioDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoMediator extends BaseMediator {

	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private RemessaDAO remessaDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private DesistenciaDAO desistenciaDAO;
	@Autowired
	private CancelamentoDAO cancelamentoDAO;
	@Autowired
	private MunicipioDAO municipioDAO;
	@Autowired
	private AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	@Autowired
	private ProcessadorArquivo processadorArquivo;
	@Autowired
	private ConversorRemessaArquivo conversorRemessaArquivo;

	private List<Exception> erros;
	private Arquivo arquivo;

	@Transactional
	public Arquivo carregarArquivoPorId(Arquivo arquivo) {
		return arquivoDAO.buscarPorPK(arquivo, Arquivo.class);
	}

	public List<Arquivo> buscarArquivosAvancado(Arquivo arquivo, Usuario usuario, ArrayList<TipoArquivoEnum> tipoArquivos, Municipio pracaProtesto,
			LocalDate dataInicio, LocalDate dataFim, ArrayList<SituacaoArquivo> situacoes) {
		return arquivoDAO.buscarArquivosAvancado(arquivo, usuario, tipoArquivos, pracaProtesto, dataInicio, dataFim, situacoes);
	}

	public Arquivo buscarArquivoEnviado(Usuario usuario, String nomeArquivo) {
		return arquivoDAO.buscarArquivosPorNomeArquivoInstituicaoEnvio(usuario.getInstituicao(), nomeArquivo);
	}

	/**
	 * Arquivos de Remessa pendentes de confirmação dos cartórios
	 * 
	 * @param instituicao
	 * @return
	 */
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

	/**
	 * Salvar arquivo pela aplicação.
	 * 
	 * @param arquivo
	 * @param uploadedFile
	 * @param usuario
	 * @return
	 */
	public ArquivoMediator salvar(Arquivo arquivo, FileUpload uploadedFile, Usuario usuario) {
		this.erros = new ArrayList<Exception>();

		this.arquivo = arquivo;
		this.arquivo.setNomeArquivo(uploadedFile.getClientFileName());
		this.arquivo.setTipoArquivo(getTipoArquivo(arquivo));
		this.arquivo.setHoraEnvio(new LocalTime());
		this.arquivo.setDataEnvio(new LocalDate());
		this.arquivo.setDataRecebimento(new LocalDate().toDate());
		this.arquivo.setStatusArquivo(setStatusArquivo());
		this.arquivo.setUsuarioEnvio(usuario);
		this.arquivo.setInstituicaoEnvio(getInstituicaoEnvioArquivo(usuario, uploadedFile));

		this.arquivo = processadorArquivo.processarArquivo(uploadedFile, arquivo, getErros());
		this.arquivo = arquivoDAO.salvar(arquivo, usuario, getErros());

		if (getErros().isEmpty()) {
			loggerCra.sucess(arquivo.getInstituicaoEnvio(), usuario, getTipoAcaoEnvio(arquivo), "Arquivo " + arquivo.getNomeArquivo()
					+ ", enviado por " + arquivo.getInstituicaoEnvio().getNomeFantasia() + ", recebido com sucesso via aplicação.");
		}
		return this;
	}

	/**
	 * Salvar arquivo pelo ws.
	 * 
	 * @param arquivoRecebido
	 * @param usuario
	 * @param nomeArquivo
	 * @return MensagemCra
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ArquivoMediator salvarWS(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo) {
		this.erros = new ArrayList<Exception>();

		this.arquivo = new Arquivo();
		this.arquivo.setNomeArquivo(nomeArquivo);
		this.arquivo.setUsuarioEnvio(usuario);
		this.arquivo.setRemessas(new ArrayList<Remessa>());
		this.arquivo.setHoraEnvio(new LocalTime());
		this.arquivo.setDataEnvio(new LocalDate());
		this.arquivo.setDataRecebimento(new LocalDate().toDate());
		this.arquivo.setInstituicaoEnvio(usuario.getInstituicao());

		this.arquivo = processadorArquivo.processarArquivoWS(arquivoRecebido, arquivo, getErros());
		this.arquivo = arquivoDAO.salvar(arquivo, usuario, getErros());

		return this;
	}

	private Instituicao getInstituicaoEnvioArquivo(Usuario usuario, FileUpload uploadedFile) {
		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			return usuario.getInstituicao();
		}

		String nomeArquivo = uploadedFile.getClientFileName();
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
			return instituicaoDAO.getInstituicaoPorCodigo(nomeArquivo.substring(1, 4));
		}
		if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo) || TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			return identificarMunicipioEnvioPeloCabecalho(uploadedFile);
		}
		if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo) || TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)
				|| TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
			return instituicaoDAO.getInstituicaoPorCodigo(nomeArquivo.substring(2, 5));
		} else {
			throw new InfraException("Não é possível identificar o nome do arquivo ou não segue os padrões FEBRABAN.");
		}
	}

	private Instituicao identificarMunicipioEnvioPeloCabecalho(FileUpload uploadedFile) {
		String codigoMunicipio = StringUtils.EMPTY;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedFile.getInputStream()));
			String linha = reader.readLine();
			if (LayoutArquivo.TXT.equals(LayoutArquivo.get(linha))) {
				codigoMunicipio = linha.substring(92, 99);
			} else if (LayoutArquivo.XML.equals(LayoutArquivo.get(linha))) {
				while (!linha.contains("h15") && (linha = reader.readLine()) != null) {
					Pattern pattern = null;
					if (linha.contains("'")) {
						pattern = Pattern.compile("'[0-9][0-9][0-9][0-9][0-9][0-9][0-9]'");
					} else {
						pattern = Pattern.compile("\"[0-9][0-9][0-9][0-9][0-9][0-9][0-9]\"");
					}
					Matcher m = pattern.matcher(linha);
					if (m.find()) {
						codigoMunicipio = m.group().replace("\"", "").replace("'", "");
					}
				}
			} else {
				throw new InfraException(
						"Não foi possível identificar o layout do arquivo. Os dados internos podem estar ilegíveis ou não segue o manual FEBRABAN.");
			}
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new InfraException("Não foi possível ler o cabeçalho do arquivo enviado! Por favor entre em contato com o IEPTB!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new InfraException("Não foi possível ler o cabeçalho do arquivo enviado! Por favor entre em contato com o IEPTB!");
		}

		if (StringUtils.isEmpty(codigoMunicipio) || StringUtils.isBlank(codigoMunicipio) || codigoMunicipio.trim().length() != 7) {
			throw new InfraException("Código do município do cabeçalho inválido.");
		}
		Instituicao instituicao = instituicaoDAO.getCartorioPeloCodigoMunicipio(codigoMunicipio);
		if (instituicao == null) {
			throw new InfraException("Não foi possível identificar o cartório com o código do município [ " + codigoMunicipio + " ].");
		}
		return instituicao;
	}

	private CraAcao getTipoAcaoEnvio(Arquivo arquivo) {
		CraAcao tipoAcao = null;
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo);
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_REMESSA;
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_CONFIRMACAO;
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_RETORNO;
		} else if (tipoArquivo.equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO;
		} else if (tipoArquivo.equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO;
		} else if (tipoArquivo.equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
			tipoAcao = CraAcao.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO;
		}
		return tipoAcao;
	}

	private StatusArquivo setStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	private TipoArquivo getTipoArquivo(Arquivo arquivo) {
		return tipoArquivoDAO.buscarTipoArquivo(arquivo);
	}

	/**
	 * Download Remessa XML para cartórios via ws
	 * 
	 * @param usuario
	 * @param nomeArquivo
	 * @return
	 */
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

	/**
	 * Buscar Arquivos Confirmacao e Retorno WS para Instituicao e COnvênios
	 * 
	 * @param nomeArquivo
	 * @param instituicao
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<RemessaVO> buscarArquivos(String nomeArquivo, Instituicao instituicao) {
		Arquivo arquivo = null;

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
		return conversorRemessaArquivo.converterArquivoVO(arquivo.getRemessaBanco());
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

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}
}