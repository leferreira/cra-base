package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.AutorizacaoCancelamentoDAO;
import br.com.ieptbto.cra.dao.CancelamentoDAO;
import br.com.ieptbto.cra.dao.SolicitacaoDesistenciaCancelamentoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorDesistenciaCancelamentoConvenio;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.DecoderString;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class SolicitacaoDesistenciaCancelamentoMediator extends BaseMediator {

	@Autowired
	ProcessadorDesistenciaCancelamentoConvenio processadorDesistenciaCancelamentoConvenio;
	@Autowired
	SolicitacaoDesistenciaCancelamentoDAO solicitacaoDAO;
	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	CancelamentoDAO cancelamentoDAO;
	@Autowired
	AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	
	private String pathInstituicaoTemp;
	private String pathUsuarioTemp;
	
	public List<SolicitacaoDesistenciaCancelamento> buscarSolicitacoesDesistenciasCancelamentos() {
		return solicitacaoDAO.buscarCancelamentosSolicitados();
	}

	public List<SolicitacaoDesistenciaCancelamento> buscarSolicitacoesDesistenciasCancelamentoPorTitulo(TituloRemessa titulo) {
		if (titulo.getRemessa().getInstituicaoOrigem().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CONVENIO)) {
			return solicitacaoDAO.buscarSolicitacoesDesistenciasCancelamentoPorTitulo(titulo);
		}
		return null;
	}
	
	/**
	 * Salvar uma solicitação de desistencia e cancelamento com anexos
	 * 
	 * @param solicitacaoDesistenciaCancelamento
	 * @param uploadedFile
	 * @return
	 */
	public SolicitacaoDesistenciaCancelamento salvarSolicitacaoDesistenciaCancelamento(
			SolicitacaoDesistenciaCancelamento solicitacaoDesistenciaCancelamento, FileUpload uploadedFile) {
		Usuario usuario = solicitacaoDesistenciaCancelamento.getUsuario();

		SolicitacaoDesistenciaCancelamento solicitacaoEnviada = solicitacaoDAO.verificarSolicitadoAnteriormente(solicitacaoDesistenciaCancelamento);
		if (solicitacaoEnviada != null) {
			throw new InfraException("Esta solicitação já foi enviada anteriormente para este título em "
					+ DataUtil.localDateToString(new LocalDate(solicitacaoEnviada.getDataSolicitacao()))
					+ "! Aguarde o processamento pelo cartório...");
		}
		if (uploadedFile != null) {
			this.pathInstituicaoTemp = null;
			this.pathUsuarioTemp = null;
			File fileTmp = verificarDiretorioECopiarArquivo(usuario, uploadedFile);

			byte[] conteudoArquivo = DecoderString.loadFile(fileTmp);
			solicitacaoDesistenciaCancelamento.setDocumentoAnexo(Base64.encodeBase64(conteudoArquivo));
		}
		return solicitacaoDAO.salvarSolicitacaoDesistenciaCancelamento(solicitacaoDesistenciaCancelamento);
	}

	private File verificarDiretorioECopiarArquivo(Usuario usuario, FileUpload uploadedFile) {
		pathInstituicaoTemp = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO_TEMP + usuario.getInstituicao().getId();
		pathUsuarioTemp = pathInstituicaoTemp + ConfiguracaoBase.BARRA + usuario.getId();
		File diretorioInstituicaoTemp = new File(pathInstituicaoTemp);
		File diretorioUsuarioTemp = new File(pathUsuarioTemp);

		if (diretorioInstituicaoTemp.exists()) {
			diretorioInstituicaoTemp.delete();
		}
		diretorioInstituicaoTemp.mkdirs();
		if (diretorioUsuarioTemp.exists()) {
			diretorioUsuarioTemp.delete();
		}
		diretorioUsuarioTemp.mkdirs();

		File fileTmp = new File(pathUsuarioTemp + ConfiguracaoBase.BARRA + usuario.getId());
		try {
			uploadedFile.writeTo(fileTmp);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível criar arquivo temporário do anexo! Por favor entre em contato com o IEPTB-TO.");
		}
		return fileTmp;
	}
	
	/**
	 * @param user
	 * @param solicitacoes
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<Arquivo> gerarDesistenciasCancelamentosConvenio(Usuario user, List<SolicitacaoDesistenciaCancelamento> solicitacoes) {
		List<Arquivo> arquivos = processadorDesistenciaCancelamentoConvenio.processaDesistenciasCancelamentos(solicitacoes, user);

		for (Arquivo arquivo : arquivos) {
			TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(arquivo);
			if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
				arquivoDAO.salvar(arquivo, user, new ArrayList<Exception>());
			} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
				cancelamentoDAO.salvarCancelamento(arquivo, user, new ArrayList<Exception>());
			} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
				autorizacaoCancelamentoDAO.salvarAutorizacao(arquivo, user, new ArrayList<Exception>());
			}
		}
		solicitacaoDAO.alterarSolicitacoesParaEnviadas(solicitacoes);
		return arquivos;
	}
}
