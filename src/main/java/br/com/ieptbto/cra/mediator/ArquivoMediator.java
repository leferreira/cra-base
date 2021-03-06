package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.conversor.arquivo.ConversorRemessaArquivo;
import br.com.ieptbto.cra.dao.*;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.view.ViewArquivoPendente;
import br.com.ieptbto.cra.entidade.vo.ArquivoRemessaConvenioVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;
import br.com.ieptbto.cra.processador.ProcessadorArquivoConvenio;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private AutorizacaoCancelamentoDAO autorizacaoDAO;
	@Autowired
	private ProcessadorArquivo processadorArquivo;
	@Autowired
	private ProcessadorArquivoConvenio processadorArquivoConvenio;
	@Autowired
	private ConversorRemessaArquivo conversorRemessaArquivo;

	@Transactional
	public Arquivo buscarArquivoPorPK(Arquivo arquivo) {
		return arquivoDAO.buscarPorPK(arquivo, Arquivo.class);
	}
	
	@Transactional
	public Arquivo buscarArquivoPorPK(Integer id) {
		return arquivoDAO.buscarPorPK(id, Arquivo.class);
	}

	public List<Arquivo> buscarArquivos(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim,
			TipoInstituicaoCRA tipoInstituicao, Instituicao bancoConvenio, List<TipoArquivoFebraban> tiposArquivo,
			List<StatusDownload> situacoesArquivos) {
		return arquivoDAO.buscarArquivos(usuario, nomeArquivo, dataInicio, dataFim, tipoInstituicao, bancoConvenio, tiposArquivo,
				situacoesArquivos);
	}
	
	public List<Arquivo> buscarRetornoParaLayoutRecebimentoEmpresa(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
		return arquivoDAO.buscarRetornoParaLayoutRecebimentoEmpresa(usuario, dataInicio, dataFim);
	}

	public List<Arquivo> buscarArquivosDesistenciaCancelamento(Usuario usuario, String nomeArquivo, LocalDate dataInicio, LocalDate dataFim,
			TipoInstituicaoCRA tipoInstituicao, Instituicao bancoConvenio, List<TipoArquivoFebraban> tiposArquivo,
			List<StatusDownload> situacoesArquivos) {
		return arquivoDAO.buscarArquivosDesistenciaCancelamento(usuario, nomeArquivo, dataInicio, dataFim, tipoInstituicao, bancoConvenio,
				tiposArquivo, situacoesArquivos);
	}

	public Arquivo buscarArquivoPorNomeInstituicaoEnvio(Usuario usuario, String nomeArquivo) {
		return arquivoDAO.buscarArquivoPorNomeInstituicaoEnvio(usuario.getInstituicao(), nomeArquivo);
	}
	
	public Arquivo buscarArquivoEnviadoConvenio(Usuario usuario) {
		return arquivoDAO.buscarArquivoEnviadoDataAtual(usuario.getInstituicao());
	}

	/**
	 * Arquivos de Remessa, Desistencia e Cancelamentos pendentes dos cartórios
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
		List<AutorizacaoCancelamento> autorizacaoCancelamento = autorizacaoDAO.buscarRemessaAutorizacaoCancelamentoPendenteDownload(instituicao);

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
	 * Arquivos de Remessa, Desistencia e Cancelamentos pendentes dos cartórios
	 * 
	 * @param instituicao
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<ViewArquivoPendente> consultarViewArquivosPendentes(Instituicao instituicao) {
		TipoInstituicaoCRA tipoInstituicao = instituicao.getTipoInstituicao().getTipoInstituicao();
		if (TipoInstituicaoCRA.CRA == tipoInstituicao) {
			return arquivoDAO.consultarArquivosPendentes(instituicao);
		} else if (TipoInstituicaoCRA.CARTORIO == tipoInstituicao) {
			return arquivoDAO.consultarArquivosPendentesCartorio(instituicao);
		} else if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA == tipoInstituicao 
						|| TipoInstituicaoCRA.CONVENIO == tipoInstituicao) {
			return arquivoDAO.consultarArquivosPendentesBancoConvenio(instituicao);
		}
		return null;
	}

	/**
	 * Arquivos de Desistencias pendentes dos cartórios
	 * 
	 * @param instituicao
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public Arquivo desistenciaPendentes(Instituicao instituicao) {
		instituicao.setMunicipio(municipioDAO.buscarPorPK(instituicao.getMunicipio(), Municipio.class));

		List<DesistenciaProtesto> desistenciasProtesto = desistenciaDAO.buscarRemessaDesistenciaProtestoPendenteDownload(instituicao);
		Arquivo arquivo = new Arquivo();
		RemessaDesistenciaProtesto remessaDesistenciaProtesto = new RemessaDesistenciaProtesto();
		remessaDesistenciaProtesto.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>(desistenciasProtesto));
		arquivo.setRemessaDesistenciaProtesto(remessaDesistenciaProtesto);
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
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString()));
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setStatusArquivo(setStatusArquivo());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());

		arquivo = processadorArquivo.processarArquivoWS(arquivoRecebido, arquivo, erros);
		if (erros.isEmpty()) {
			arquivo = arquivoDAO.salvar(arquivo, usuario, erros);
		}
		return arquivo;
	}

	/**
	 * Salvar arquivo de remessa dos convênios pelo ws.
	 * 
	 * @param usuario
	 * @param nomeArquivo
	 * @return MensagemCra
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public Arquivo salvarWSConvenio(ArquivoRemessaConvenioVO arquivoVO, Usuario usuario, String nomeArquivo, List<Exception> erros) {
		Arquivo arquivo = new Arquivo();
		arquivo.setNomeArquivo(TipoArquivoFebraban.generateNomeArquivoFebraban
                (TipoArquivoFebraban.REMESSA, usuario.getInstituicao().getCodigoCompensacao(), "1"));
		arquivo.setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoFebraban.REMESSA));
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString()));
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setStatusArquivo(setStatusArquivo());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo = processadorArquivoConvenio.processarArquivoWS(arquivoVO, arquivo, usuario, erros);
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
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(nomeArquivo);
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
			return instituicaoDAO.getInstituicaoPorCodigo(nomeArquivo.substring(1, 4));
		}
		if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo) || TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			return identificarMunicipioEnvioPeloCabecalho(uploadedFile);
		}
		if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo) || TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)
				|| TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
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
				throw new InfraException("Não foi possível identificar o layout do arquivo. Os dados internos podem estar ilegíveis ou não segue o manual FEBRABAN.");
			}
			reader.close();

		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new InfraException("Não foi possível ler o cabeçalho do arquivo enviado! Por favor entre em contato com o IEPTB!");
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
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
		status.setStatusDownload(StatusDownload.ENVIADO);
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
		if (nomeArquivo.startsWith(TipoArquivoFebraban.REMESSA.getConstante())) {
			remessa = remessaDAO.baixarArquivoCartorioRemessa(usuario.getInstituicao(), nomeArquivo);
		}

		if (remessa == null || remessa.getDevolvidoPelaCRA()) {
			return null;
		}
		if (!StatusDownload.RECEBIDO.equals(remessa.getStatusDownload())) {
			remessa.setStatusDownload(StatusDownload.RECEBIDO);
			remessaDAO.alterarSituacaoRemessa(remessa);
		}

		ArrayList<Arquivo> arquivos = new ArrayList<>();
		arquivos.add(remessa.getArquivo());
		logger.info("O Usuario " + usuario.getLogin() + " da instituição " + usuario.getInstituicao().getNomeFantasia()
				+ " fez o download do arquivo " + nomeArquivo + " que foi enviado para " + remessa.getInstituicaoDestino().getNomeFantasia()
				+ ".");
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

		if (nomeArquivo.startsWith(TipoArquivoFebraban.REMESSA.getConstante())) {
			arquivo = arquivoDAO.buscarArquivoInstituicaoRemessa(nomeArquivo, instituicao);
			return conversorRemessaArquivo.converterArquivoVO(arquivo.getRemessas());
		} else if (nomeArquivo.startsWith(TipoArquivoFebraban.CONFIRMACAO.getConstante())) {
			arquivo = arquivoDAO.buscarArquivoInstituicaoConfirmacao(nomeArquivo, instituicao);
		} else if (nomeArquivo.startsWith(TipoArquivoFebraban.RETORNO.getConstante())) {
			arquivo = arquivoDAO.buscarArquivoInstituicaoRetorno(nomeArquivo, instituicao);
		}
		if (arquivo == null) {
			return new ArrayList<RemessaVO>();
		}
		if (arquivo.getStatusArquivo() != null) {
			if (!StatusDownload.RECEBIDO.equals(arquivo.getStatusArquivo().getStatusDownload())) {
				StatusArquivo statusArquivo = new StatusArquivo();
				statusArquivo.setStatusDownload(StatusDownload.RECEBIDO);
				statusArquivo.setData(new LocalDateTime());
				arquivo.setStatusArquivo(statusArquivo);
				arquivoDAO.alterarStatusArquivo(arquivo);
			}
		}
		return conversorRemessaArquivo.converterArquivoVO(arquivo.getRemessaBanco());
	}
}