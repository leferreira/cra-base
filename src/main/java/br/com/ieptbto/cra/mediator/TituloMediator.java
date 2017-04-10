package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.beans.TituloBean;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.view.ViewTitulo;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloMediator extends BaseMediator {

	@Autowired
	TituloDAO tituloDAO;

	public TituloRemessa buscarTituloPorPK(TituloRemessa titulo) {
		return tituloDAO.buscarPorPK(titulo, TituloRemessa.class);
	}

	public TituloRemessa buscarTituloPorPK(Integer idTitulo) {
		return tituloDAO.buscarPorPK(idTitulo, TituloRemessa.class);
	}

	/**
	 * Busca o título principal para outros devedores
	 * @param confirmacao
	 * @return
	 */
	public Retorno buscarRetornoTituloDevedorPrincipal(Confirmacao confirmacao) {
		return tituloDAO.buscarRetornoTituloDevedorPrincipal(confirmacao);
	}
	
	public List<TituloRemessa> carregarTitulos(Remessa remessa) {
		return tituloDAO.carregarTitulos(remessa);
	}

	public List<ViewTitulo> consultarViewTitulosPorRemessa(Remessa remessa) {
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(remessa.getArquivo());
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
			return tituloDAO.consultarViewTitulosPorIdRemessa(remessa.getId());
		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
			return tituloDAO.consultarViewTitulosConfirmacaoPorIdRemessa(remessa.getId());
		} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			return tituloDAO.consultarViewTitulosRetornoPorIdRemessa(remessa.getId());
		}
		return null;
	}

	public List<TituloRemessa> buscarTitulos(Usuario usuario, LocalDate dataInicio, LocalDate dataFim, TipoInstituicaoCRA tipoInstituicao,
			Instituicao bancoConvenio, Instituicao cartorio, TituloBean titulo) {
		return tituloDAO.buscarTitulos(usuario, dataInicio, dataFim, tipoInstituicao, bancoConvenio, cartorio, titulo);
	}

	@SuppressWarnings("rawtypes")
	public List<Titulo> carregarTitulosGenerico(Arquivo arquivo) {
		return tituloDAO.carregarTitulosGenerico(arquivo);
	}

	public TituloRemessa buscarTituloRemessaPorDadosRetorno(Retorno tituloRetorno) {
		return tituloDAO.buscarTituloRemessaPorDadosRetorno(tituloRetorno);
	}

	public Anexo buscarAnexo(TituloRemessa tituloRemessa) {
		if (tituloRemessa.getRemessa().getInstituicaoOrigem().getTipoCampo51().equals(TipoCampo51.DOCUMENTOS_COMPACTADOS)) {
			Anexo anexo = tituloDAO.buscarAnexo(tituloRemessa);
			if (anexo != null) {
				return anexo;
			}
		} 
		return null;
	}
}