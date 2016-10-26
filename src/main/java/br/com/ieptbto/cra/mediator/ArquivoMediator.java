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

	@Transactional
	public Arquivo carregarArquivoPorId(Arquivo arquivo) {
		return arquivoDAO.buscarPorPK(arquivo, Arquivo.class);
	}

	public List<Arquivo> buscarArquivos(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, List<TipoArquivoEnum> tiposArquivo, List<SituacaoArquivo> situacoesArquivos) {
		return arquivoDAO.buscarArquivos(usuario, nomeArquivo, dataInicio, dataFim, tipoInstituicao, bancoConvenio, tiposArquivo, situacoesArquivos);
	}

	public List<Arquivo> buscarArquivosDesistenciaCancelamento(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim,
			TipoInstituicaoCRA tipoInstituicao, Instituicao bancoConvenio, List<TipoArquivoEnum> tiposArquivo, List<SituacaoArquivo> situacoesArquivos) {
		return arquivoDAO.buscarArquivosDesistenciaCancelamento(usuario, nomeArquivo, dataInicio, dataFim, tipoInstituicao, bancoConvenio, tiposArquivo,
				situacoesArquivos);
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
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public Arquivo arquivosPendentes(Instituicao instituicao) {
		instituicao.setMunicipio(municipioDAO.buscarPorPK(instituicao.getMunicipio(), Municipio.class));

		List<Remessa> remessas = remessaDAO.confirmacoesPendentes(instituicao);
		List<DesistenciaProtesto> desistenciasProtesto = desistenciaDAO.buscarRemessaDesistenciaProtestoPendenteDownload(instituicao);
		List<CancelamentoProtesto> cancelamentoProtesto = cancelamentoDAO.buscarRemessaCancelamentoPendenteDownload(instituicao);
		List<AutorizacaoCancelamento> autorizacaoCancelamento = autorizacaoCancelamentoDAO.buscarRemessaAutorizacaoCancelamentoPendenteDownload(instituicao);

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
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public Arquivo salvar(Arquivo arquivo, FileUpload uploadedFile, Usuario usuario, List<Exception> erros) {
		arquivo.setNomeArquivo(uploadedFile.getClientFileName());
		arquivo.setTipoArquivo(getTipoArquivo(arquivo));
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setStatusArquivo(setStatusArquivo());
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(getInstituicaoEnvioArquivo(usuario, uploadedFile));

		arquivo = processadorArquivo.processarArquivo(uploadedFile, arquivo, erros);
		if (erros.isEmpty()) {
			arquivo = arquivoDAO.salvar(arquivo, usuario, erros);
		}
		return arquivo;
	}

	/**
	 * Salvar arquivo pelo ws.
	 * 
	 * @param arquivoRecebido
	 * @param usuario
	 * @param nomeArquivo
	 * @return MensagemCra
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public Arquivo salvarWS(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo, List<Exception> erros) {
		Arquivo arquivo = new Arquivo();
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setTipoArquivo(tipoArquivoDAO.buscarTipoArquivo(nomeArquivo));
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setStatusArquivo(setStatusArquivo());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());

		arquivo = processadorArquivo.processarArquivoWS(arquivoRecebido, arquivo, erros);
		if (erros.isEmpty()) {
			arquivo = arquivoDAO.salvar(arquivo, usuario, erros);
		}
		return arquivo;
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
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public RemessaVO buscarRemessaParaCartorio(Usuario usuario, String nomeArquivo) {
		Remessa remessa = null;
		logger.info("Usuario " + usuario.getLogin() + " está buscando a remessa " + nomeArquivo + " na CRA.");
		if (nomeArquivo.startsWith(TipoArquivoEnum.REMESSA.getConstante())) {
			remessa = remessaDAO.baixarArquivoCartorioRemessa(usuario.getInstituicao(), nomeArquivo);
		}

		if (remessa == null) {
			return null;
		}
		if (!StatusRemessa.RECEBIDO.equals(remessa.getStatusRemessa())) {
			remessa.setStatusRemessa(StatusRemessa.RECEBIDO);
			remessaDAO.alterarSituacaoRemessa(remessa);
		}

		ArrayList<Arquivo> arquivos = new ArrayList<>();
		arquivos.add(remessa.getArquivo());
		logger.info("O Usuario " + usuario.getLogin() + " da instituição " + usuario.getInstituicao().getNomeFantasia() + " fez o download do arquivo "
				+ nomeArquivo + " que foi enviado para " + remessa.getInstituicaoDestino().getNomeFantasia() + ".");
		return conversorRemessaArquivo.converterArquivoXMLRemessaVO(remessa);
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
		if (arquivo.getStatusArquivo() != null) {
			if (!SituacaoArquivo.RECEBIDO.equals(arquivo.getStatusArquivo().getSituacaoArquivo())) {
				StatusArquivo statusArquivo = new StatusArquivo();
				statusArquivo.setSituacaoArquivo(SituacaoArquivo.RECEBIDO);
				statusArquivo.setData(new LocalDateTime());
				arquivo.setStatusArquivo(statusArquivo);
				arquivoDAO.alterarStatusArquivo(arquivo);
			}
		}
		return conversorRemessaArquivo.converterArquivoVO(arquivo.getRemessaBanco());
	}
}