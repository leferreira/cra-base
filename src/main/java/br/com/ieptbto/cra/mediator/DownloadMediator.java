package br.com.ieptbto.cra.mediator;

import java.io.File;
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
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;

@Service
public class DownloadMediator extends BaseMediator {

	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private RemessaDAO remessaDAO;
	@Autowired
	private DesistenciaDAO desistenciaDAO;
	@Autowired
	private CancelamentoDAO cancelamentoDAO;
	@Autowired
	private AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	@Autowired
	private ProcessadorArquivo processadorArquivo;

	/**
	 * Download de arquivos TXT de Instituições e Convênios
	 * 
	 * @param instituicao
	 * @param arquivo
	 * @return
	 */
	public File baixarArquivoTXT(Instituicao instituicao, Arquivo arquivo) {
		List<Remessa> remessas = null;
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)
				&& !arquivo.getStatusArquivo().getSituacaoArquivo().equals(SituacaoArquivo.ENVIADO)) {
			StatusArquivo status = new StatusArquivo();
			status.setData(new LocalDateTime());
			status.setSituacaoArquivo(SituacaoArquivo.RECEBIDO);
			arquivo.setStatusArquivo(status);
			arquivoDAO.alterarStatusArquivo(arquivo);
		}

		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			remessas = arquivoDAO.baixarArquivoInstituicaoRemessa(arquivo);
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			remessas = arquivoDAO.baixarArquivoInstituicaoConfirmacao(arquivo);
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			remessas = arquivoDAO.baixarArquivoInstituicaoRetorno(arquivo);
		}
		return processadorArquivo.processarArquivoTXT(arquivo, remessas);
	}

	/**
	 * Download de arquivos B,C,R TXT de Cartórios
	 * 
	 * @param usuario
	 * @param remessa
	 * @return
	 */
	public File baixarRemessaTXT(Usuario usuario, Remessa remessa) {
		TipoInstituicaoCRA tipoInstituicaoUsuario = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (!tipoInstituicaoUsuario.equals(TipoInstituicaoCRA.CRA) && !remessa.getStatusRemessa().equals(StatusRemessa.ENVIADO)) {
			remessa = remessaDAO.buscarPorPK(remessa, Remessa.class);
			remessa.setStatusRemessa(StatusRemessa.RECEBIDO);
			remessaDAO.alterarSituacaoRemessa(remessa);
			loggerCra.sucess(usuario, CraAcao.DOWNLOAD_ARQUIVO_REMESSA, "Arquivo de Remessa " + remessa.getArquivo().getNomeArquivo()
					+ " recebido com sucesso " + "por " + usuario.getInstituicao() + ", via aplicação.");
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
			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)
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
			file = processadorArquivo.processarRemessaDesistenciaProtestoTXT(remessa, usuario);

			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				loggerCra.sucess(usuario, CraAcao.DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO,
						"Arquivo " + desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo() + ", recebido com sucesso por "
								+ usuario.getInstituicao().getNomeFantasia() + ".");
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
			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)
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
			file = processadorArquivo.processarRemessaCancelamentoProtestoTXT(remessa, usuario);

			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
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
			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)
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
			file = processadorArquivo.processarRemessaAutorizacaoCancelamentoTXT(remessa, usuario);

			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
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
}
