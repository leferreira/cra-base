package br.com.ieptbto.cra.processador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;

@Service
public class ProcessadorMigracaoAntigaCRA extends Processador {

	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private ArquivoDAO arquivoDAO;
	private HashMap<String, Arquivo> arquivosMigrados;
	private Arquivo arquivo;
	private List<Arquivo> novosArquivos;
	private Usuario usuario;
		
	public void processarArquivoMigracao(Arquivo arquivo, Usuario usuarioEnvio) {
		setArquivo(arquivo);
		setUsuario(usuarioEnvio);
		
		gerarArquivosCartorios();
		salvarNovosArquivos();
	}

	private void salvarNovosArquivos() {
		for (Arquivo novoArquivo : getNovosArquivos()){
			arquivoDAO.salvar(novoArquivo, getUsuario());
		}
	}

	private void gerarArquivosCartorios() {
		for (Remessa remessa: getArquivo().getRemessas()){
			setSituacaoOrigemDestinoRemessa(remessa);

			if (getArquivosMigrados().containsKey(remessa.getCabecalho().getCodigoMunicipio())) {
				getArquivosMigrados().get(remessa.getCabecalho().getCodigoMunicipio()).getRemessas().add(remessa);
			} else {
				getArquivosMigrados().put(remessa.getCabecalho().getCodigoMunicipio(), criarArquivo(remessa));
			}
		}
	}
	
	private void setSituacaoOrigemDestinoRemessa(Remessa remessa) {
		remessa.setInstituicaoOrigem(instituicaoMediator.getInstituicaoPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio()));
		remessa.setInstituicaoDestino(instituicaoMediator.getInstituicaoPorCodigoPortador(remessa.getCabecalho().getNumeroCodigoPortador()));
		remessa.setStatusRemessa(StatusRemessa.ENVIADO);
		remessa.setSituacao(false);
		
		if (getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO))
			remessa.setSituacaoBatimento(false);
	}
	
	private Arquivo criarArquivo(Remessa remessa) {
		Arquivo arquivo = new Arquivo();
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setInstituicaoEnvio(remessa.getInstituicaoOrigem());
		arquivo.setInstituicaoRecebe(instituicaoMediator.buscarCRA());
		arquivo.setNomeArquivo(getArquivo().getNomeArquivo());
		arquivo.setTipoArquivo(getArquivo().getTipoArquivo());
		arquivo.setUsuarioEnvio(getUsuario());
		arquivo.setStatusArquivo(gerarStatusArquivo());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.getRemessas().add(remessa);
		getNovosArquivos().add(arquivo);
		return arquivo;
	}

	private StatusArquivo gerarStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public HashMap<String, Arquivo> getArquivosMigrados() {
		if (arquivosMigrados == null) {
			arquivosMigrados = new HashMap<String, Arquivo>();
		}
		return arquivosMigrados;
	}

	public void setArquivosMigrados(HashMap<String, Arquivo> arquivosMigrados) {
		this.arquivosMigrados = arquivosMigrados;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Arquivo> getNovosArquivos() {
		if (novosArquivos == null) {
			novosArquivos = new ArrayList<Arquivo>();
		}
		return novosArquivos;
	}

	public void setNovosArquivos(List<Arquivo> novosArquivos) {
		this.novosArquivos = novosArquivos;
	}
}
