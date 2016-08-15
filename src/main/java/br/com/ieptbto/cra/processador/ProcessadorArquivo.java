package br.com.ieptbto.cra.processador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.fabrica.FabricaDeArquivo;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.regra.FabricaRegraEntradaValidacao;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ProcessadorArquivo extends Processador {

	@Autowired
	private FabricaDeArquivo fabricaDeArquivo;
	@Autowired
	private FabricaRegraEntradaValidacao fabricaRegraValidacaoArquivo;

	private FileUpload fileUpload;
	private File file;
	private Usuario usuario;
	private Arquivo arquivo;
	private List<Exception> erros;
	private String pathInstituicao;
	private String pathUsuario;
	private String pathInstituicaoTemp;
	private String pathUsuarioTemp;

	/**
	 * Métódo para processar arquivos via aplicação
	 * 
	 * @param uploadedFile
	 * @param arquivo
	 * @param erros
	 * @return
	 */
	public Arquivo processarArquivo(FileUpload uploadedFile, Arquivo arquivo, List<Exception> erros) {
		this.fileUpload = uploadedFile;
		this.usuario = arquivo.getUsuarioEnvio();
		this.arquivo = arquivo;
		this.erros = erros;

		if (getFileUpload() != null) {
			logger.info(
					"Início processamento arquivo via aplicação " + getFileUpload().getClientFileName() + " do usuário " + getUsuario().getLogin());

			verificaDiretorio();
			copiarArquivoParaDiretorioDoUsuarioTemporario(getFileUpload().getClientFileName());
			setArquivo(fabricaDeArquivo.fabricaAplicacao(getFile(), getArquivo(), getErros()));
			validarArquivo();
			copiarArquivoEapagarTemporario();

			logger.info(
					"Início processamento arquivo via aplicação " + getFileUpload().getClientFileName() + " do usuário " + getUsuario().getLogin());
			return getArquivo();
		} else {
			throw new InfraException("O arquivo " + getFileUpload().getClientFileName() + " enviado não pode ser processado!");
		}
	}

	/**
	 * Métódo para processar arquivos via ws
	 * 
	 * @param arquivoRecebido
	 * @param arquivo
	 * @param erros
	 * @return
	 */
	public Arquivo processarArquivoWS(List<RemessaVO> arquivoRecebido, Arquivo arquivo, List<Exception> erros) {
		this.usuario = arquivo.getUsuarioEnvio();
		this.arquivo = arquivo;
		this.erros = erros;

		logger.info("Início processamento arquivo via WS " + getFileUpload().getClientFileName() + " do usuário " + getUsuario().getLogin());

		verificaDiretorio();
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + arquivo.getNomeArquivo()));
		setArquivo(fabricaDeArquivo.fabricaWS(arquivoRecebido, arquivo, erros));
		validarArquivo();

		logger.info("Início processamento arquivo via WS " + getFileUpload().getClientFileName() + " do usuário " + getUsuario().getLogin());
		return getArquivo();
	}

	private void validarArquivo() {
		fabricaRegraValidacaoArquivo.validar(getFile(), getArquivo(), getUsuario(), getErros());
	}

	/**
	 * Métódos de download B,C,R TXT Para Instituicao e Convênios
	 * 
	 * @param arquivo
	 * @param remessas
	 * @return
	 */
	public File baixarRemessaConfirmacaoRetornoTXT(Arquivo arquivo, List<Remessa> remessas) {
		this.file = null;
		this.arquivo = arquivo;
		this.usuario = arquivo.getUsuarioEnvio();

		logger.info("Início do criação de Arquivo TXT " + getArquivo().getNomeArquivo() + " do usuário " + getUsuario().getLogin());

		verificaDiretorio();
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + arquivo.getId()));
		return fabricaDeArquivo.baixarRemessaConfirmacaoRetornoTXT(remessas, getFile());
	}

	/**
	 * Métódos de download B,C,R TXT Para Cartórios
	 * 
	 * @param remessa
	 * @return
	 */
	public File baixarRemessaConfirmacaoRetornoTXT(Remessa remessa) {
		this.file = null;
		this.arquivo = remessa.getArquivo();
		this.usuario = remessa.getArquivo().getUsuarioEnvio();

		logger.info("Início do criação de Arquivo TXT " + getArquivo().getNomeArquivo() + " do usuário " + getUsuario().getLogin());

		verificaDiretorio();
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + remessa.getId()));
		return fabricaDeArquivo.baixarRemessaConfirmacaoRetornoTXT(remessa, getFile());
	}

	/**
	 * Métódos de download TXT
	 * 
	 * @param arquivo
	 * @param remessas
	 * @return
	 */
	public File baixarDesistenciaTXT(RemessaDesistenciaProtesto remessa) {
		this.file = null;
		this.arquivo = remessa.getArquivo();
		this.usuario = remessa.getArquivo().getUsuarioEnvio();

		logger.info("Início do criação de Arquivo TXT " + getArquivo().getNomeArquivo() + " do usuário " + getUsuario().getLogin());

		verificaDiretorio();
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + remessa.getId()));
		return fabricaDeArquivo.baixarDesistenciaTXT(remessa, getFile());
	}

	/**
	 * Métódos de download TXT
	 * 
	 * @param arquivo
	 * @param remessas
	 * @return
	 */
	public File baixarCancelamentoTXT(RemessaCancelamentoProtesto remessa) {
		this.file = null;
		this.arquivo = remessa.getArquivo();
		this.usuario = remessa.getArquivo().getUsuarioEnvio();

		logger.info("Início do criação de Arquivo TXT " + getArquivo().getNomeArquivo() + " do usuário " + getUsuario().getLogin());

		verificaDiretorio();
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + remessa.getId()));
		return fabricaDeArquivo.baixarCancelamentoTXT(remessa, getFile());
	}

	/**
	 * Métódos de download TXT
	 * 
	 * @param arquivo
	 * @param remessas
	 * @return
	 */
	public File baixarAutorizacaoCancelamentoTXT(RemessaAutorizacaoCancelamento remessa) {
		this.file = null;
		this.arquivo = remessa.getArquivo();
		this.usuario = remessa.getArquivo().getUsuarioEnvio();

		logger.info("Início do criação de Arquivo TXT " + getArquivo().getNomeArquivo() + " do usuário " + getUsuario().getLogin());

		verificaDiretorio();
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + remessa.getId()));
		return fabricaDeArquivo.baixarAutorizacaoCancelamentoTXT(remessa, getFile());
	}

	private void copiarArquivoParaDiretorioDoUsuarioTemporario(String nomeArquivo) {
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + nomeArquivo));
		try {
			getFileUpload().writeTo(getFile());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + getFileUpload().getClientFileName());
		}
	}

	private void copiarArquivoEapagarTemporario() {
		try {
			if (getFile().renameTo(new File(getPathUsuario() + ConfiguracaoBase.BARRA + getArquivo().getId()))) {
				logger.info("Arquivo " + getFileUpload().getClientFileName() + " movido para pasta do usuário.");
				return;
			}
			new InfraException("Não foi possível mover o arquivo temporário para o diretório do usuário.");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			getErros().add(ex);
			new InfraException("Não foi possível mover o arquivo temporário para o diretório do usuário.");
		}
	}

	private void verificaDiretorio() {
		pathInstituicao = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + getUsuario().getInstituicao().getId();
		pathInstituicaoTemp = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO_TEMP + getUsuario().getInstituicao().getId();
		pathUsuario = pathInstituicao + ConfiguracaoBase.BARRA + usuario.getId();
		pathUsuarioTemp = pathInstituicaoTemp + ConfiguracaoBase.BARRA + usuario.getId();
		File diretorioTemp = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE);
		File diretorioArquivo = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioInstituicao = new File(pathInstituicao);
		File diretorioUsuario = new File(pathUsuario);
		File diretorioUsuarioTemp = new File(pathUsuarioTemp);

		if (!diretorioTemp.exists()) {
			diretorioTemp.mkdirs();
		}
		if (!diretorioArquivo.exists()) {
			diretorioArquivo.mkdirs();
		}
		if (!diretorioInstituicao.exists()) {
			diretorioInstituicao.mkdirs();
		}
		if (!diretorioUsuario.exists()) {
			diretorioUsuario.mkdirs();
		}
		if (!diretorioUsuarioTemp.exists()) {
			diretorioUsuarioTemp.mkdirs();
		}
	}

	public FileUpload getFileUpload() {
		return fileUpload;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setFileUpload(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}

	public File getFile() {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e.getCause());
				throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + file.getName());
			}
		}
		return file;
	}

	public void setFile(File file) {
		if (this.file != null && this.file.exists()) {
			this.file.delete();
		}
		this.file = file;
	}

	public Arquivo getArquivo() {
		if (this.arquivo == null) {
			arquivo = new Arquivo();
		}
		if (arquivo.getRemessas() == null) {
			arquivo.setRemessas(new ArrayList<Remessa>());
		}
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
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

	public String getPathInstituicaoTemp() {
		return pathInstituicaoTemp;
	}

	public String getPathUsuarioTemp() {
		return pathUsuarioTemp;
	}

	public String getPathInstituicao() {
		return pathInstituicao;
	}

	public String getPathUsuario() {
		return pathUsuario;
	}
}