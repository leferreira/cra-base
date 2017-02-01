package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.AutorizacaoCancelamentoDAO;
import br.com.ieptbto.cra.dao.CancelamentoDAO;
import br.com.ieptbto.cra.dao.TituloFiliadoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.processador.ProcessadorDesistenciaCancelamentoConveniada;
import br.com.ieptbto.cra.processador.ProcessadorRemessaConveniada;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ConvenioMediator extends BaseMediator {

	@Autowired
	TituloFiliadoDAO tituloFiliadoDAO;
	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	CancelamentoDAO cancelamentoDAO;
	@Autowired
	AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	@Autowired
	ProcessadorRemessaConveniada processadorRemessaConveniada;
	@Autowired
	ProcessadorDesistenciaCancelamentoConveniada processadorDesistenciaCancelamentoConveniada;

	/**
	 * Buscar títulos do convênio para geração de arquivos de remessa
	 * @return
	 */
	public List<TituloFiliado> buscarTitulosConveniosGerarRemessa() {
		return tituloFiliadoDAO.buscarTitulosConvenios();
	}

	/**
	 * Gerador de Arquivos de Remessas de Convênios
	 * @param usuario
	 * @param listaTitulosConvenios
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<Arquivo> gerarRemessas(Usuario usuario, List<TituloFiliado> listaTitulosConvenios) {
		List<Arquivo> arquivos = processadorRemessaConveniada.processarRemessa(listaTitulosConvenios, usuario);

		for (Arquivo arquivo : arquivos) {
			arquivoDAO.salvar(arquivo, usuario, new ArrayList<Exception>());
		}
		tituloFiliadoDAO.marcarComoEnviadoParaCRA(listaTitulosConvenios);
		return arquivos;
	}

	/**
	 * Gerador de Arquivos de Desistencias e Cancelamentos solicitados pelos convênios
	 * @param user
	 * @param solicitacoes
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<Arquivo> gerarDesistenciasCancelamentosConvenio(Usuario user, List<SolicitacaoDesistenciaCancelamento> solicitacoes) {
		List<Arquivo> arquivos = processadorDesistenciaCancelamentoConveniada.processaDesistenciasCancelamentos(solicitacoes, user);

		for (Arquivo arquivo : arquivos) {

			TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo);
			if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
				arquivoDAO.salvar(arquivo, user, new ArrayList<Exception>());
			} else if (TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
				cancelamentoDAO.salvarCancelamento(arquivo, user, new ArrayList<Exception>());
			} else if (TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
				autorizacaoCancelamentoDAO.salvarAutorizacao(arquivo, user, new ArrayList<Exception>());
			}
		}
		cancelamentoDAO.marcarSolicitacoesDesistenciasCancelamentosEnviados(solicitacoes);
		return arquivos;
	}
}