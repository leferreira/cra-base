package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;
import br.com.ieptbto.cra.util.DecoderString;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoMediator {

	protected static final Logger logger = Logger.getLogger(ArquivoMediator.class);
	
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private ProcessadorArquivo processadorArquivo;
	@Autowired
	private ArquivoDAO arquivoDAO;
	private List<Exception> erros;
	private Arquivo arquivo;

	public ArquivoMediator salvar(Arquivo arquivo, FileUpload uploadedFile, Usuario usuario) {
		if (verificarPermissaoDeEnvio(usuario, arquivo)) {
			throw new InfraException("O usuário " + usuario.getNome() + " não pode enviar arquivos " + arquivo.getNomeArquivo());
		}
		arquivo.setTipoArquivo(getTipoArquivo(arquivo));
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setStatusArquivo(setStatusArquivo());
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());

		arquivo = processarArquivo(arquivo, uploadedFile);
		setArquivo(arquivoDAO.salvar(arquivo, usuario, getErros()));
		return this;
	}

	private boolean verificarPermissaoDeEnvio(Usuario user, Arquivo arquivo) {
		String nome = arquivo.getNomeArquivo().substring(1, 4);
		if (arquivo.getNomeArquivo().length() == 13) {
			nome = arquivo.getNomeArquivo().substring(2, 5);
		}

		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(user.getInstituicao().getTipoInstituicao().getTipoInstituicao())
		        && user.getInstituicao().getCodigoCompensacao().equals(nome)) {
			return false;
		} else if (TipoInstituicaoCRA.CARTORIO.equals(user.getInstituicao().getTipoInstituicao().getTipoInstituicao())
		        || TipoInstituicaoCRA.CRA.equals(user.getInstituicao().getTipoInstituicao().getTipoInstituicao())) {
			return false;
		}
		return true;
	}

	private StatusArquivo setStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	private TipoArquivo getTipoArquivo(Arquivo arquivo) {
		return tipoArquivoDAO.buscarTipoArquivo(arquivo);
	}

	private Arquivo processarArquivo(Arquivo arquivo, FileUpload uploadedFile) throws InfraException {
		return processadorArquivo.processarArquivo(uploadedFile, arquivo, getErros());
	}
	
	public void criarDocumentosZipadosCampoComplementoRegistro(Remessa remessa, TituloVO tituloVO) {
		File diretorioCRA = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioBaseInstituicao = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO);
		File diretorioInstituicao = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId());
		File diretorioArquivo = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId() 
				+ ConfiguracaoBase.BARRA + remessa.getArquivo().getNomeArquivo());
		File diretorioMunicipio = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId() 
				+ ConfiguracaoBase.BARRA + remessa.getArquivo().getNomeArquivo()
				+ ConfiguracaoBase.BARRA + remessa.getCabecalho().getCodigoMunicipio());
				
		if (!diretorioCRA.exists()) {
			diretorioCRA.mkdirs();
		}
		if (!diretorioBaseInstituicao.exists()) {
			diretorioBaseInstituicao.mkdirs();
		}
		if (!diretorioInstituicao.exists()) {
			diretorioInstituicao.mkdirs();
		}
		if (!diretorioArquivo.exists()) {
			diretorioArquivo.mkdirs();
		}
		if (!diretorioMunicipio.exists()) {
			diretorioMunicipio.mkdirs();
		}
		
		try {
			DecoderString decoderString = new DecoderString();
			String nomeArquivoZip = tituloVO.getNomeDevedor() + "_"
			        + tituloVO.getNumeroTitulo().replaceAll("\\\\", "").replaceAll("\\/", "");
		
			decoderString.decode(tituloVO.getComplementoRegistro(), ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + remessa.getInstituicaoOrigem().getId() 
					+ ConfiguracaoBase.BARRA + remessa.getArquivo().getNomeArquivo()
					+ ConfiguracaoBase.BARRA + remessa.getCabecalho().getCodigoMunicipio() + ConfiguracaoBase.BARRA , nomeArquivoZip + ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
			
		} catch (FileNotFoundException e) {
			logger.info("O arquivo ZIP em anexo não pode ser criado.");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("O arquivo ZIP em anexo não pode ser criado.");
			e.printStackTrace();
		}
	}
	
	public List<Arquivo> buscarArquivos() {
		return arquivoDAO.buscarTodosArquivos();
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public List<Arquivo> buscarArquivosAvancado(Arquivo arquivo, Usuario usuario, ArrayList<TipoArquivoEnum> tipoArquivos,
	        Municipio pracaProtesto, LocalDate dataInicio, LocalDate dataFim, ArrayList<SituacaoArquivo> situacoes) {
		return arquivoDAO.buscarArquivosAvancado(arquivo, usuario, tipoArquivos, pracaProtesto, dataInicio, dataFim, situacoes);
	}

	public Arquivo buscarArquivoPorNome(Instituicao instituicao, String nomeArquivo) {
		return arquivoDAO.buscarArquivoPorNome(instituicao, nomeArquivo);
	}

	public List<Arquivo> buscarArquivosPorNome(Instituicao instituicao, Arquivo arquivo) {
		return arquivoDAO.buscarArquivosPorNome(instituicao, arquivo);
	}
}
