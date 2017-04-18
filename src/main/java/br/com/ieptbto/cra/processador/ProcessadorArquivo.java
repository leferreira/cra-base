package br.com.ieptbto.cra.processador;

import java.io.File;
import java.io.IOException;
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

		Usuario usuario = arquivo.getUsuarioEnvio();
		if (getFileUpload() != null) {
			logger.info("Início processamento arquivo via aplicação " + getFileUpload().getClientFileName() + " do usuário "
					+ usuario.getLogin());

			verificaDiretorio(usuario);
			copiarArquivoParaDiretorioDoUsuarioTemporario(arquivo);
			arquivo = fabricaDeArquivo.fabricaAplicacao(getFile(), arquivo, erros);
			fabricaRegraValidacaoArquivo.validar(getFile(), arquivo, usuario, erros);
			copiarArquivoEapagarTemporario(arquivo);

			logger.info(
					"Fim processamento arquivo via aplicação " + getFileUpload().getClientFileName() + " do usuário " + usuario.getLogin());
		} else {
			throw new InfraException("O arquivo " + getFileUpload().getClientFileName() + " enviado não pode ser processado!");
		}
		return arquivo;
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
		Usuario usuario = arquivo.getUsuarioEnvio();

		logger.info("Início processamento arquivo via WS " + arquivo.getNomeArquivo() + " do usuário " + usuario.getLogin());

		arquivo = fabricaDeArquivo.fabricaWS(arquivoRecebido, arquivo, erros);
		fabricaRegraValidacaoArquivo.validar(arquivo, usuario, erros);

		logger.info("Fim processamento arquivo via WS " + arquivo.getNomeArquivo() + " do usuário " + usuario.getLogin());
		return arquivo;
	}
	
	/**
	 * Métódos de download B,C,R TXT Para Instituicao e Convênios
	 * 
	 * @param arquivo
	 * @param remessas
	 * @return
	 */
	public File baixarRemessaConfirmacaoRetornoTXT(Arquivo arquivo, List<Remessa> remessas, Usuario usuario) {
		this.file = null;

		verificaDiretorio(usuario);
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + arquivo.getId()));
		return fabricaDeArquivo.baixarRemessaConfirmacaoRetornoTXT(remessas, getFile());
	}
	
	/**
	 * Métódos de download B,C,R TXT Para Instituicao e Convênios
	 * 
	 * @param arquivo
	 * @param remessas
	 * @return
	 */
	public File baixarRetornoRecebimentoEmpresaTXT(Arquivo arquivo, List<Remessa> remessas, Usuario usuario, Integer sequencialArquivo) {
		this.file = null;

		verificaDiretorio(usuario);
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + arquivo.getId()));
		return fabricaDeArquivo.baixarRetornoRecebimentoEmpresaTXT(remessas, getFile(), usuario.getInstituicao(), arquivo.getDataEnvio(), sequencialArquivo);
	}

	/**
	 * Métódos de download B,C,R TXT Para Cartórios
	 * 
	 * @param remessa
	 * @return
	 */
	public File baixarRemessaConfirmacaoRetornoTXT(Remessa remessa, Usuario usuario) {
		this.file = null;

		verificaDiretorio(usuario);
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
	public File baixarDesistenciaTXT(RemessaDesistenciaProtesto remessa, Usuario usuario) {
		this.file = null;

		verificaDiretorio(usuario);
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
	public File baixarCancelamentoTXT(RemessaCancelamentoProtesto remessa, Usuario usuario) {
		this.file = null;

		verificaDiretorio(usuario);
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
	public File baixarAutorizacaoCancelamentoTXT(RemessaAutorizacaoCancelamento remessa, Usuario usuario) {
		this.file = null;

		verificaDiretorio(usuario);
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + remessa.getId()));
		return fabricaDeArquivo.baixarAutorizacaoCancelamentoTXT(remessa, getFile());
	}

	private void copiarArquivoParaDiretorioDoUsuarioTemporario(Arquivo arquivo) {
		setFile(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + arquivo.getId()));
		try {
			getFileUpload().writeTo(getFile());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(
					"Não foi possível criar arquivo Físico temporário para o arquivo " + getFileUpload().getClientFileName());
		}
	}

	private void copiarArquivoEapagarTemporario(Arquivo arquivo) {
		try {
			if (getFile().renameTo(new File(getPathUsuario() + ConfiguracaoBase.BARRA + arquivo.getId()))) {
				return;
			}
			new InfraException("Não foi possível mover o arquivo temporário para o diretório do usuário.");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			throw new InfraException("Não foi possível mover o arquivo temporário para o diretório do usuário.");
		}
	}

	private void verificaDiretorio(Usuario usuario) {
		File diretorioBase = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioBaseTemp = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE);

		pathInstituicao = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + usuario.getInstituicao().getId();
		pathInstituicaoTemp = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO_TEMP + usuario.getInstituicao().getId();
		File diretorioInstituicaoTemp = new File(pathInstituicaoTemp);
		File diretorioInstituicao = new File(pathInstituicao);

		pathUsuarioTemp = pathInstituicaoTemp + ConfiguracaoBase.BARRA + usuario.getId();
		pathUsuario = pathInstituicao + ConfiguracaoBase.BARRA + usuario.getId();
		File diretorioUsuarioTemp = new File(pathUsuarioTemp);
		File diretorioUsuario = new File(pathUsuario);

		try {
			if (!diretorioBase.exists()) {
				diretorioBase.mkdirs();
			}
			if (!diretorioBaseTemp.exists()) {
				diretorioBaseTemp.mkdirs();
			}
			if (!diretorioInstituicao.exists()) {
				diretorioInstituicao.mkdirs();
			}
			if (!diretorioInstituicaoTemp.exists()) {
				diretorioInstituicaoTemp.mkdirs();
			}
			if (!diretorioUsuario.exists()) {
				diretorioUsuario.mkdirs();
			}
			if (!diretorioUsuarioTemp.exists()) {
				diretorioUsuarioTemp.mkdirs();
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível criar os diretórios do usuário. Favor entrar em contato com a CRA...");
		}
	}

	public FileUpload getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}

	public File getFile() {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new InfraException("Não foi possível criar o arquivo temporário. Favor entrar em contato com a CRA...");
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