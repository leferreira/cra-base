package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.ConfirmacaoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author thasso
 *
 */
@Service
public class ConfirmacaoMediator extends BaseMediator {

	@Autowired
	ConfirmacaoDAO confirmacaoDAO;
	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;

	public Confirmacao buscarConfirmacaoPorTitulo(TituloRemessa titulo) {
		return tituloDAO.buscarConfirmacaoPorTitulo(titulo);
	}

	public Confirmacao carregarTituloConfirmacao(Confirmacao confirmacao) {
		return tituloDAO.buscarPorPK(confirmacao, Confirmacao.class);
	}
	
	/**
	 * Busca os arquivo de confirmação para serem envaminhados aos bancos
	 * @return
	 */
	public List<Remessa> buscarConfirmacoesPendentesDeEnvio() {
		return confirmacaoDAO.buscarConfirmacoesPendentesDeEnvio();
	}

	public Boolean verificarArquivoConfirmacaoCra() {
		Instituicao cra = instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString());
		return confirmacaoDAO.verificarArquivoConfirmacaoCra(cra);
	}

	/**
	 * @param usuarioCorrente
	 * @param confirmacoesParaEnvio
	 */
	public List<Arquivo> gerarConfirmacoes(Usuario usuarioCorrente, List<Remessa> confirmacoesParaEnvio) {
		List<Arquivo> arquivosDeConfirmacao = new ArrayList<Arquivo>();
		Arquivo arquivo = null;
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa confirmacao : confirmacoesParaEnvio) {

			if (arquivo == null || !instituicaoDestino.equals(confirmacao.getInstituicaoDestino())) {
				instituicaoDestino = confirmacao.getInstituicaoDestino();
				arquivo = criarNovoArquivoDeConfirmacao(instituicaoDestino, confirmacao);

				List<Remessa> confirmacoes = confirmacaoDAO.buscarConfirmacoesPendentesPorInstituicao(instituicaoDestino);
				arquivo.setRemessas(confirmacoes);

				if (!arquivosDeConfirmacao.contains(arquivo) && arquivo != null) {
					arquivosDeConfirmacao.add(arquivo);
				}
			}
		}
		return confirmacaoDAO.salvarArquivosDeConfirmacaoGerados(usuarioCorrente, arquivosDeConfirmacao);
	}

	private Arquivo criarNovoArquivoDeConfirmacao(Instituicao instituicaoDestino, Remessa confirmacao) {
		Instituicao cra = instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString());
		TipoArquivo tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoFebraban.CONFIRMACAO);
		
		Arquivo arquivo = new Arquivo();
		arquivo.setTipoArquivo(tipoArquivo);
		arquivo.setNomeArquivo(TipoArquivoFebraban.generateNomeArquivoFebraban(TipoArquivoFebraban.CONFIRMACAO, instituicaoDestino.getCodigoCompensacao(), ConfiguracaoBase.UM));
		arquivo.setInstituicaoRecebe(instituicaoDestino);
		arquivo.setInstituicaoEnvio(cra);
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		return arquivo;
	}
}