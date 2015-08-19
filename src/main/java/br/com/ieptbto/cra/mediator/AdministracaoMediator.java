package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.AdministracaoDAO;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;

@Service
public class AdministracaoMediator {

	@Autowired
	AdministracaoDAO adminDAO;
	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	ArquivoDAO arquivoDAO;
	private Arquivo arquivo;
	private Instituicao instituicao;
	private TipoArquivoEnum tipoArquivo;
	
	public AdministracaoMediator removerArquivo(Arquivo arquivo, Instituicao instituicao) {
		this.arquivo = arquivo;
		this.tipoArquivo = arquivo.getTipoArquivo().getTipoArquivo();
		this.instituicao = instituicao;
		
		removerPorTipoArquivo();
		return this;
	}

	private void removerPorTipoArquivo() {
		List<Remessa> remessas = arquivoDAO.buscarRemessasArquivo(getInstituicao(), getArquivo());

		if (!isArquivoEnviadoPelaCra()) {
			if (getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
				adminDAO.removerRemessa(getInstituicao(), getArquivo(), remessas);
			} else if (getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				adminDAO.removerConfirmacao(getInstituicao(), getArquivo(), remessas);
			} else if (getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				adminDAO.removerRetorno(getInstituicao(), getArquivo(), remessas);
			}
		}
	}

	private boolean isArquivoEnviadoPelaCra() {
		if (getArquivo().getInstituicaoEnvio().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			
			return true;
		}
		return false;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public TipoArquivoEnum getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivoEnum tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}
}
