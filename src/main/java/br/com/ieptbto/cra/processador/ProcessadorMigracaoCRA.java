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
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.MigracaoDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ProcessadorMigracaoCRA extends Processador {

	private static final Logger logger = Logger.getLogger(ProcessadorMigracaoCRA.class);
	
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	MigracaoDAO migracaoDAO;
	@Autowired
	RemessaDAO remessaDAO;
	
	private HashMap<chaveArquivo, Arquivo> arquivosMigrados;
	private Arquivo arquivo;
	private List<Arquivo> novosArquivos;
	private Usuario usuario;
		
	public ProcessadorMigracaoCRA processarArquivoMigracao(Arquivo arquivo, Usuario usuarioEnvio) {
		this.novosArquivos = new ArrayList<Arquivo>();
		setArquivo(arquivo);
		setUsuario(usuarioEnvio);
		
		logger.info("Início do processamento do arquivo " + arquivo.getNomeArquivo() + " gerado pela antiga CRA !");
		
		separarArquivos();
		salvarNovosArquivos();
		
		logger.info("Fim do processamento do arquivo " + arquivo.getNomeArquivo() + " gerado pela antiga CRA !");
		return this;
	}

	private void salvarNovosArquivos() {
		for (Arquivo novoArquivo : getNovosArquivos()){
			if (arquivoDAO.buscarArquivosPorNomeArquivoInstituicaoEnvio(novoArquivo.getInstituicaoEnvio(), novoArquivo.getNomeArquivo()) == null) {
				migracaoDAO.salvar(novoArquivo, getUsuario());
			}
			logger.info("Arquivo " + getArquivo().getNomeArquivo() + " salvo !");
		}
	}
	
	private void separarArquivos() {
		int sequencialArquivo = 1;
		for (Remessa remessa: getArquivo().getRemessas()){
			setSituacaoOrigemDestinoRemessa(remessa);
			Remessa remessaBuscada = remessaDAO.isRemessaEnviada(gerarNomeArquivo(remessa, 1), remessa.getInstituicaoOrigem());
			if (remessaBuscada != null) {
				if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
					if (!remessaDAO.verificarDuplicidadeRetorno(remessa)) {
						adicionarRemessaNoArquivo(remessa, remessaBuscada, sequencialArquivo);
					}
				}
				if (remessa.getCabecalho().getNumeroSequencialRemessa() != remessaBuscada.getCabecalho().getNumeroSequencialRemessa()) {
					sequencialArquivo = remessaDAO.verificarSequencialArquivo(remessa);
				}
			} else {
				adicionarRemessaNoArquivo(remessa, remessaBuscada, sequencialArquivo);
			}
		}
	}
	
	private void adicionarRemessaNoArquivo(Remessa remessa, Remessa remessaBuscada, int sequencialArquivo) {
		if (remessa != remessaBuscada) {
			if (getArquivosMigrados().containsKey(remessa.getCabecalho().getCodigoMunicipio())) {
				getArquivosMigrados().get(remessa.getCabecalho().getCodigoMunicipio() + remessa.getCabecalho().getDataMovimento()).getRemessas().add(remessa);
			} else {
				getArquivosMigrados().put(new chaveArquivo(remessa.getCabecalho().getCodigoMunicipio(), 
						DataUtil.localDateToString(remessa.getCabecalho().getDataMovimento())), criarArquivo(remessa, sequencialArquivo));
			}
		}
	}

	private void setSituacaoOrigemDestinoRemessa(Remessa remessa) {
		remessa.setDataRecebimento(remessa.getCabecalho().getDataMovimento());
		remessa.setInstituicaoOrigem(instituicaoDAO.getInstituicao(remessa.getCabecalho().getCodigoMunicipio()));
		remessa.setInstituicaoDestino(instituicaoDAO.getInstituicaoPorCodigo(remessa.getCabecalho().getNumeroCodigoPortador()));
		remessa.setStatusRemessa(StatusRemessa.ENVIADO);
		remessa.setSituacao(false);
		
		if (getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO))
			remessa.setSituacaoBatimento(false);
	}
	
	private Arquivo criarArquivo(Remessa remessa, int sequencialArquivo) {
		Arquivo arquivo = new Arquivo();
		arquivo.setDataEnvio(remessa.getCabecalho().getDataMovimento());
		arquivo.setInstituicaoEnvio(remessa.getInstituicaoOrigem());
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));
		arquivo.setNomeArquivo(gerarNomeArquivo(remessa, 1));
		arquivo.setTipoArquivo(getArquivo().getTipoArquivo());
		arquivo.setUsuarioEnvio(getUsuario());
		arquivo.setStatusArquivo(gerarStatusArquivo());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.getRemessas().add(remessa);
		getNovosArquivos().add(arquivo);
		return arquivo;
	}
	
	private String gerarNomeArquivo(Remessa remessa, int sequencialArquivo) {
		return remessa.getArquivo().getTipoArquivo().getTipoArquivo().getConstante()
			+ remessa.getInstituicaoDestino().getCodigoCompensacao()
			+ gerarDataArquivo(remessa.getCabecalho().getDataMovimento())
			+ Integer.toString(sequencialArquivo);
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
	
	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public HashMap<chaveArquivo, Arquivo> getArquivosMigrados() {
		if (arquivosMigrados == null) {
			arquivosMigrados = new HashMap<chaveArquivo, Arquivo>();
		}
		return arquivosMigrados;
	}
	
	public static void main(String[] args) {
		String nome = "R2371506.151";
		System.out.println(nome.substring(0, 3));
	}

	public void setArquivosMigrados(HashMap<chaveArquivo, Arquivo> arquivosMigrados) {
		this.arquivosMigrados = arquivosMigrados;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Arquivo> getNovosArquivos() {
		return novosArquivos;
	}

	public void setNovosArquivos(List<Arquivo> novosArquivos) {
		this.novosArquivos = novosArquivos;
	}

	public class chaveArquivo {
		
		private String dataMovimento;
		private String codigoMunicipio;
		
		public chaveArquivo(String dataMovimento, String codigoMunicipio) {
			this.dataMovimento = dataMovimento;
			this.codigoMunicipio = codigoMunicipio;
		}
		
		@Override
		public String toString() {
			return codigoMunicipio + dataMovimento;
		}
	}
}

