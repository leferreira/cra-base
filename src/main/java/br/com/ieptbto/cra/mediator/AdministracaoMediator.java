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
	private List<Remessa> remessas;
	
	public AdministracaoMediator removerArquivo(Arquivo arquivo, Instituicao instituicao) {
		this.arquivo = arquivo;
		this.tipoArquivo = arquivo.getTipoArquivo().getTipoArquivo();
		this.instituicao = instituicao;
		
		removerPorTipoArquivo();
		return this;
	}

	private void removerPorTipoArquivo() {
		this.remessas = arquivoDAO.getRemessasArquivo(getArquivo(), getInstituicao());
		
		getArquivo().setRemessas(getRemessas());
		if (!isArquivoEnviadoPelaCra()) {
			if (getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
				adminDAO.removerRemessa(getInstituicao(), getArquivo());
			} else if (getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				adminDAO.removerConfirmacao(getInstituicao(), getArquivo());
			} else if (getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				adminDAO.removerRetorno(getInstituicao(), getArquivo());
			}
		}
	}

	private boolean isArquivoEnviadoPelaCra() {
		if (getArquivo().getInstituicaoEnvio().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			if (getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				adminDAO.removerConfimacaoPelaCra(getArquivo());
			} else if (getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				adminDAO.removerRetornoPelaCra(getArquivo());
			}
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

	public List<Remessa> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<Remessa> remessas) {
		this.remessas = remessas;
	}
}
