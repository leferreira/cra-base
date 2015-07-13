package br.com.ieptbto.cra.mediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
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
public class ConfirmacaoMediator {

	private static final int NUMERO_SEQUENCIAL_CONFIRMACAO = 1;
	
	@Autowired
	ConfirmacaoDAO confirmacaoDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	
	private Instituicao cra;
	private TipoArquivo tipoArquivo;
	private Arquivo arquivo;
	
	public List<Remessa> buscarConfirmacoesPendentesDeEnvio() {
		return confirmacaoDAO.buscarConfirmacoesPendentesDeEnvio();
	}

	public void gerarConfirmacoes(Usuario usuarioCorrente, List<Remessa> confirmacoesParaEnvio) {
		List<Arquivo> arquivosDeConfirmacao = new ArrayList<Arquivo>();
		setCra(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));
		setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.CONFIRMACAO));
		
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa confirmacao: confirmacoesParaEnvio){
			
			if (arquivo == null || !instituicaoDestino.equals(confirmacao.getInstituicaoDestino())){
				instituicaoDestino = confirmacao.getInstituicaoDestino();
				criarNovoArquivoDeConfirmacao(instituicaoDestino, confirmacao);
				
				List<Remessa> confirmacoes = confirmacaoDAO.buscarConfirmacoesPendentesPorInstituicao(instituicaoDestino);
				arquivo.setRemessas(confirmacoes);
				
				if (!arquivosDeConfirmacao.contains(arquivo) && arquivo != null)
					arquivosDeConfirmacao.add(arquivo);
			} 
		}
		
		confirmacaoDAO.salvarArquivosDeConfirmacaoGerados(usuarioCorrente, arquivosDeConfirmacao);
	} 

	private void criarNovoArquivoDeConfirmacao(Instituicao destino,Remessa confirmacao) {
		this.arquivo = new Arquivo();
		getArquivo().setTipoArquivo(getTipoArquivo());
		getArquivo().setNomeArquivo(gerarNomeArquivoConfirmacao(confirmacao));
		getArquivo().setInstituicaoRecebe(destino);
		getArquivo().setInstituicaoEnvio(getCra());
		getArquivo().setDataEnvio(new LocalDate());
	}

	private String gerarNomeArquivoConfirmacao(Remessa confirmacao) {
		
		return TipoArquivoEnum.CONFIRMACAO.getConstante() 
			+ confirmacao.getCabecalho().getNumeroCodigoPortador()
			+ gerarDataArquivo()
			+ NUMERO_SEQUENCIAL_CONFIRMACAO;
	}

	private String gerarDataArquivo(){
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(new Date()).toString();
	}

	public Instituicao getCra() {
		return cra;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setCra(Instituicao cra) {
		this.cra = cra;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}
}
