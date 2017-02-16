package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.AutorizacaoCancelamentoDAO;
import br.com.ieptbto.cra.dao.CancelamentoDAO;
import br.com.ieptbto.cra.dao.DesistenciaDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoSolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;
import br.com.ieptbto.cra.util.DecoderString;

@Service
public class DownloadMediator extends BaseMediator {

	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	RemessaDAO remessaDAO;
	@Autowired
	DesistenciaDAO desistenciaDAO;
	@Autowired
	CancelamentoDAO cancelamentoDAO;
	@Autowired
	AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	@Autowired
	ProcessadorArquivo processadorArquivo;

	/**
	 * Download de arquivos TXT de Instituições e Convênios
	 * 
	 * @param instituicao
	 * @param arquivo
	 * @return
	 */
	public File baixarArquivoTXT(Usuario usuario, Arquivo arquivo) {
		List<Remessa> remessas = null;
		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)
				&& !arquivo.getStatusArquivo().getStatusDownload().equals(StatusDownload.ENVIADO)) {
			StatusArquivo status = new StatusArquivo();
			status.setData(new LocalDateTime());
			status.setStatusDownload(StatusDownload.RECEBIDO);
			arquivo.setStatusArquivo(status);
			arquivoDAO.alterarStatusArquivo(arquivo);
		}

		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.REMESSA)) {
			remessas = arquivoDAO.baixarArquivoInstituicaoRemessa(arquivo);
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.CONFIRMACAO)) {
			remessas = arquivoDAO.baixarArquivoInstituicaoConfirmacao(arquivo);
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.RETORNO)) {
			remessas = arquivoDAO.baixarArquivoInstituicaoRetorno(arquivo);
		}
		return processadorArquivo.baixarRemessaConfirmacaoRetornoTXT(arquivo, remessas, usuario);
	}

	/**
	 * Download de arquivos TXT de Instituições e Convênios
	 * 
	 * @param instituicao
	 * @param arquivo
	 * @return
	 */
	public File baixarDesistenciaCancelamentoTXT(Usuario usuario, Arquivo arquivo) {

		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)
				&& !arquivo.getStatusArquivo().getStatusDownload().equals(StatusDownload.ENVIADO)) {
			StatusArquivo status = new StatusArquivo();
			status.setData(new LocalDateTime());
			status.setStatusDownload(StatusDownload.RECEBIDO);
			arquivo.setStatusArquivo(status);
			arquivoDAO.alterarStatusArquivo(arquivo);
		}

		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO)) {
			return null;
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO)) {
			return null;
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO)) {
			RemessaAutorizacaoCancelamento remessaAutorizacaoCancelamento = autorizacaoCancelamentoDAO.buscarRemessasAutorizacaoCancelamento(arquivo);
			return processadorArquivo.baixarAutorizacaoCancelamentoTXT(remessaAutorizacaoCancelamento, usuario);
		}
		return null;
	}

	/**
	 * Download de arquivos B,C,R TXT de Cartórios
	 * 
	 * @param usuario
	 * @param remessa
	 * @return
	 */
	public File baixarRemessaTXT(Usuario usuario, Remessa remessa) {
		TipoInstituicaoSistema tipoInstituicaoUsuario = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (!tipoInstituicaoUsuario.equals(TipoInstituicaoSistema.CRA) && !remessa.getStatusDownload().equals(StatusDownload.ENVIADO)) {
			remessa = remessaDAO.buscarPorPK(remessa, Remessa.class);
			remessa.setStatusDownload(StatusDownload.RECEBIDO);
			remessaDAO.alterarSituacaoRemessa(remessa);
			loggerCra.sucess(usuario, CraAcao.DOWNLOAD_ARQUIVO_REMESSA, "Arquivo de Remessa " + remessa.getArquivo().getNomeArquivo()
					+ " recebido com sucesso " + "por " + usuario.getInstituicao() + ", via aplicação.");
		}
		if (tipoInstituicaoUsuario.equals(TipoInstituicaoSistema.CARTORIO) && remessa.getDevolvidoPelaCRA().equals(true)) {
			throw new InfraException("O arquivo " + remessa.getArquivo().getNomeArquivo() + " já foi devolvido pela CRA !");
		}

		TipoArquivoFebraban tipoArquivo = remessa.getArquivo().getTipoArquivo().getTipoArquivo();
		if (tipoArquivo.equals(TipoArquivoFebraban.REMESSA)) {
			remessa = remessaDAO.baixarArquivoCartorioRemessa(remessa);
		} else if (tipoArquivo.equals(TipoArquivoFebraban.CONFIRMACAO)) {
			remessa = remessaDAO.baixarArquivoCartorioConfirmacao(remessa);
		} else if (tipoArquivo.equals(TipoArquivoFebraban.RETORNO)) {
			remessa = remessaDAO.baixarArquivoCartorioRetorno(remessa);
		}
		return processadorArquivo.baixarRemessaConfirmacaoRetornoTXT(remessa, usuario);
	}

	/**
	 * Download de arquivos DP TXT de Cartórios
	 * 
	 * @param usuario
	 * @param desistenciaProtesto
	 * @return
	 */
	public File baixarDesistenciaTXT(Usuario usuario, DesistenciaProtesto desistenciaProtesto) {
		File file = null;

		try {
			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)
					&& desistenciaProtesto.getDownload() == false) {
				desistenciaDAO.alterarSituacaoDesistenciaProtesto(desistenciaProtesto, true);
			}
			desistenciaProtesto = desistenciaDAO.buscarDesistenciaProtesto(desistenciaProtesto);

			BigDecimal valorTotal = BigDecimal.ZERO;
			int totalRegistro = 0;
			for (PedidoDesistencia pedido : desistenciaProtesto.getDesistencias()) {
				valorTotal = valorTotal.add(pedido.getValorTitulo());
				totalRegistro++;
			}

			RemessaDesistenciaProtesto remessa = new RemessaDesistenciaProtesto();
			remessa.setCabecalho(desistenciaProtesto.getRemessaDesistenciaProtesto().getCabecalho());
			remessa.getCabecalho().setQuantidadeDesistencia(1);
			remessa.getCabecalho().setQuantidadeRegistro(totalRegistro);
			remessa.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
			remessa.getDesistenciaProtesto().add(desistenciaProtesto);
			remessa.setRodape(desistenciaProtesto.getRemessaDesistenciaProtesto().getRodape());
			remessa.getRodape().setQuantidadeDesistencia(1);
			remessa.getRodape().setSomatorioValorTitulo(valorTotal);
			remessa.setArquivo(desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo());
			file = processadorArquivo.baixarDesistenciaTXT(remessa, usuario);

			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)) {
				loggerCra.sucess(usuario, CraAcao.DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO,
						"Arquivo " + desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()
								+ ", recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
			}
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, CraAcao.DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO, "Erro Download Manual: " + ex.getMessage(), ex);
			throw new InfraException("Não foi possível fazer o download do arquivo de Desistência de Protesto! Entre em contato com a CRA !");
		}
		return file;
	}

	/**
	 * Download de arquivos CP TXT de Cartórios
	 * 
	 * @param usuario
	 * @param cancelamentoProtesto
	 * @return
	 */
	public File baixarCancelamentoTXT(Usuario usuario, CancelamentoProtesto cancelamentoProtesto) {
		File file = null;

		try {
			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)
					&& cancelamentoProtesto.getDownload() == false) {
				cancelamentoDAO.alterarSituacaoCancelamentoProtesto(cancelamentoProtesto, true);
			}
			cancelamentoProtesto = cancelamentoDAO.buscarRemessaCancelamentoProtesto(cancelamentoProtesto);

			BigDecimal valorTotal = BigDecimal.ZERO;
			int totalRegistro = 0;
			for (PedidoCancelamento pedido : cancelamentoProtesto.getCancelamentos()) {
				valorTotal = valorTotal.add(pedido.getValorTitulo());
				totalRegistro++;
			}

			RemessaCancelamentoProtesto remessa = new RemessaCancelamentoProtesto();
			remessa.setCabecalho(cancelamentoProtesto.getRemessaCancelamentoProtesto().getCabecalho());
			remessa.getCabecalho().setQuantidadeDesistencia(1);
			remessa.getCabecalho().setQuantidadeRegistro(totalRegistro);
			remessa.setCancelamentoProtesto(new ArrayList<CancelamentoProtesto>());
			remessa.getCancelamentoProtesto().add(cancelamentoProtesto);
			remessa.setRodape(cancelamentoProtesto.getRemessaCancelamentoProtesto().getRodape());
			remessa.getRodape().setQuantidadeDesistencia(1);
			remessa.getRodape().setSomatorioValorTitulo(valorTotal);
			remessa.setArquivo(cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo());
			file = processadorArquivo.baixarCancelamentoTXT(remessa, usuario);

			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)) {
				loggerCra.sucess(usuario, CraAcao.DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO,
						"Arquivo " + cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()
								+ ", recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
			}
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, CraAcao.DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO, "Erro Download Manual: " + ex.getMessage(), ex);
			throw new InfraException("Não foi possível fazer o download do arquivo de Cancelamento de Protesto! Entre em contato com a CRA !");
		}
		return file;
	}

	/**
	 * Download de arquivos AC TXT de Cartórios
	 * 
	 * @param usuario
	 * @param cancelamentoProtesto
	 * @return
	 */
	public File baixarAutorizacaoTXT(Usuario usuario, AutorizacaoCancelamento autorizacaoCancelamento) {
		File file = null;

		try {
			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)
					&& autorizacaoCancelamento.getDownload() == false) {
				autorizacaoCancelamentoDAO.alterarSituacaoAutorizacaoCancelamento(autorizacaoCancelamento, true);
			}
			autorizacaoCancelamento = autorizacaoCancelamentoDAO.buscarRemessaAutorizacaoCancelamento(autorizacaoCancelamento);

			BigDecimal valorTotal = BigDecimal.ZERO;
			int totalRegistro = 0;
			for (PedidoAutorizacaoCancelamento pedido : autorizacaoCancelamento.getAutorizacoesCancelamentos()) {
				valorTotal = valorTotal.add(pedido.getValorTitulo());
				totalRegistro++;
			}

			RemessaAutorizacaoCancelamento remessa = new RemessaAutorizacaoCancelamento();
			remessa.setCabecalho(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getCabecalho());
			remessa.getCabecalho().setQuantidadeDesistencia(1);
			remessa.getCabecalho().setQuantidadeRegistro(totalRegistro);
			remessa.setAutorizacaoCancelamento(new ArrayList<AutorizacaoCancelamento>());
			remessa.getAutorizacaoCancelamento().add(autorizacaoCancelamento);
			remessa.setRodape(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getRodape());
			remessa.getRodape().setQuantidadeDesistencia(1);
			remessa.getRodape().setSomatorioValorTitulo(valorTotal);
			remessa.setArquivo(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo());
			file = processadorArquivo.baixarAutorizacaoCancelamentoTXT(remessa, usuario);

			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoSistema.CRA)) {
				loggerCra.sucess(usuario, CraAcao.DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO,
						"Arquivo " + autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()
								+ ", recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
			}
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, CraAcao.DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO, "Erro Download Manual: " + ex.getMessage(), ex);
			throw new InfraException("Não foi possível fazer o download do arquivo de Autorização de Cancelamento! Entre em contato com a CRA !");
		}
		return file;
	}

	/**
	 * Download ofício de desistência e cancelamento via URL
	 * 
	 * @param nossoNumero
	 * @param tipoSolicitacao
	 * @param numeroProtocolo
	 * @return
	 */
	public File baixarOficioDesistenciaCancelamento(String nossoNumero, TipoSolicitacaoDesistenciaCancelamento tipoSolicitacao,
			CodigoIrregularidade irregularidade) {
		SolicitacaoDesistenciaCancelamento solicitacao = cancelamentoDAO.buscarSolicitacaoDesistenciaCancelamento(nossoNumero, tipoSolicitacao, irregularidade);

		if (solicitacao == null) {
			return null;
		}
		TituloRemessa tituloRemessa = solicitacao.getTituloRemessa();
		String nomeArquivoZip = tituloRemessa.getNomeDevedor().replace(" ", "_").replace("/", "") + "_"
				+ tituloRemessa.getNumeroTitulo().replace("\\", "").replace("/", "");
		try {
			DecoderString decoderString = new DecoderString();
			decoderString.decode(solicitacao.getDocumentoAnexoAsString(), ConfiguracaoBase.DIRETORIO_TEMP_BASE, nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
			return new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE + ConfiguracaoBase.BARRA + 
					nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);

		} catch (FileNotFoundException e) {
			logger.info(e);
			throw new InfraException("Não foi possível fazer o download do ofício! Por favor, entre em contato com a CRA...");
		} catch (IOException e) {
			logger.info(e);
			throw new InfraException("Não foi possível fazer o download do ofício! Por favor, entre em contato com a CRA...");
		}
	}
}