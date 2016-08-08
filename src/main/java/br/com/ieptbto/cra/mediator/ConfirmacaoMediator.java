package br.com.ieptbto.cra.mediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ConfirmacaoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;

/**
 * @author thasso
 *
 */
@Service
public class ConfirmacaoMediator extends BaseMediator {

	private static final int NUMERO_SEQUENCIAL_CONFIRMACAO = 1;

	@Autowired
	private ConfirmacaoDAO confirmacaoDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;

	private Instituicao cra;
	private TipoArquivo tipoArquivo;

	public List<Remessa> buscarConfirmacoesPendentesDeEnvio() {
		return confirmacaoDAO.buscarConfirmacoesPendentesDeEnvio();
	}

	public Boolean verificarArquivoConfirmacaoGeradoCra() {
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		return confirmacaoDAO.verificarArquivoConfirmacaoGeradoCra(getCra());
	}

	public void gerarConfirmacoes(Usuario usuarioCorrente, List<Remessa> confirmacoesParaEnvio) {
		this.cra = instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString());
		this.tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.CONFIRMACAO);

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
		confirmacaoDAO.salvarArquivosDeConfirmacaoGerados(usuarioCorrente, arquivosDeConfirmacao);
	}

	private Arquivo criarNovoArquivoDeConfirmacao(Instituicao destino, Remessa confirmacao) {
		Arquivo arquivo = new Arquivo();
		arquivo.setTipoArquivo(getTipoArquivo());
		arquivo.setNomeArquivo(gerarNomeArquivoConfirmacao(confirmacao));
		arquivo.setInstituicaoRecebe(destino);
		arquivo.setInstituicaoEnvio(getCra());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		return arquivo;
	}

	private String gerarNomeArquivoConfirmacao(Remessa confirmacao) {
		return TipoArquivoEnum.CONFIRMACAO.getConstante() + confirmacao.getCabecalho().getNumeroCodigoPortador() + gerarDataArquivo()
				+ NUMERO_SEQUENCIAL_CONFIRMACAO;
	}

	private String gerarDataArquivo() {
		return new SimpleDateFormat("ddMM.yy").format(new Date()).toString();
	}

	public Instituicao getCra() {
		return cra;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}
}
