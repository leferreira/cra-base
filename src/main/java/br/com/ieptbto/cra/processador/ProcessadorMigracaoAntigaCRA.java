package br.com.ieptbto.cra.processador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
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
import br.com.ieptbto.cra.validacao.FabricaValidacaoArquivo;

@Service
public class ProcessadorMigracaoAntigaCRA extends Processador {

	private static final Logger logger = Logger.getLogger(ProcessadorMigracaoAntigaCRA.class);
	private static final int NUMERO_SEQUENCIAL = 1;
	
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private FabricaValidacaoArquivo fabricaValidacaoArquivo;
	private HashMap<String, Arquivo> arquivosMigrados;
	private Arquivo arquivo;
	private List<Arquivo> novosArquivos;
	private Usuario usuario;
		
	public void processarArquivoMigracao(Arquivo arquivo, Usuario usuarioEnvio) {
		setArquivo(arquivo);
		setUsuario(usuarioEnvio);
		
		logger.info("Início do processamento do arquivo " + arquivo.getNomeArquivo() + " gerado pela antiga CRA !");
		
		sapararArquivos();
		salvarNovosArquivos();
		
		logger.info("Fim do processamento do arquivo " + arquivo.getNomeArquivo() + " gerado pela antiga CRA !");
	}

	private void sapararArquivos() {
		for (Remessa remessa: getArquivo().getRemessas()){
			setSituacaoOrigemDestinoRemessa(remessa);
			
			if (getArquivosMigrados().containsKey(remessa.getCabecalho().getCodigoMunicipio())) {
				getArquivosMigrados().get(remessa.getCabecalho().getCodigoMunicipio()).getRemessas().add(remessa);
			} else {
				getArquivosMigrados().put(remessa.getCabecalho().getCodigoMunicipio(), criarArquivo(remessa));
			}
		}
	}

	private void salvarNovosArquivos() {
		
		for (Arquivo novoArquivo : getNovosArquivos()){
			logger.info("Salvando arquivo " + novoArquivo.getNomeArquivo() + " de origem "+ novoArquivo.getInstituicaoEnvio().getNomeFantasia() +
					" e com destino para  " + novoArquivo.getInstituicaoRecebe().getNomeFantasia() + " !");
			
			if (!arquivoJaExiste(novoArquivo)){
				arquivoDAO.salvar(novoArquivo, getUsuario());
			}
			logger.info("Arquivo " + getArquivo().getNomeArquivo() + " salvo !");
		}
	}
	
	private void setSituacaoOrigemDestinoRemessa(Remessa remessa) {
		remessa.setDataRecebimento(remessa.getCabecalho().getDataMovimento());
		remessa.setInstituicaoOrigem(instituicaoMediator.getInstituicaoPorCodigoIBGE(remessa.getCabecalho().getCodigoMunicipio()));
		remessa.setInstituicaoDestino(instituicaoMediator.getInstituicaoPorCodigoPortador(remessa.getCabecalho().getNumeroCodigoPortador()));
		remessa.setStatusRemessa(StatusRemessa.ENVIADO);
		remessa.setSituacao(false);
		
		if (getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO))
			remessa.setSituacaoBatimento(false);
	}
	
	private Arquivo criarArquivo(Remessa remessa) {
		Arquivo arquivo = new Arquivo();
		arquivo.setDataEnvio(remessa.getCabecalho().getDataMovimento());
		arquivo.setInstituicaoEnvio(remessa.getInstituicaoOrigem());
		arquivo.setInstituicaoRecebe(instituicaoMediator.buscarCRA());
		arquivo.setNomeArquivo(gerarNomeArquivo(remessa));
		arquivo.setTipoArquivo(getArquivo().getTipoArquivo());
		arquivo.setUsuarioEnvio(getUsuario());
		arquivo.setStatusArquivo(gerarStatusArquivo());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.getRemessas().add(remessa);
		getNovosArquivos().add(arquivo);
		return arquivo;
	}

	private String gerarNomeArquivo(Remessa remessa) {
		
		return remessa.getArquivo().getTipoArquivo().getTipoArquivo().getConstante()
			+ remessa.getInstituicaoDestino().getCodigoCompensacao()
			+ gerarDataArquivo(remessa.getCabecalho().getDataMovimento())
			+ NUMERO_SEQUENCIAL;
	}
	
	private String gerarDataArquivo(LocalDate dataArquivo){
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(dataArquivo.toDate()).toString();
	}
	
	private StatusArquivo gerarStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}
	
	private boolean arquivoJaExiste(Arquivo novoArquivo) {
		if (arquivoDAO.buscarArquivosPorNomeArquivoInstituicaoEnvio(novoArquivo.getInstituicaoEnvio(), novoArquivo.getNomeArquivo()) != null) {
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
