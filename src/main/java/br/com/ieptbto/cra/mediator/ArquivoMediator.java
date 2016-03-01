package br.com.ieptbto.cra.mediator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoMediator {

	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private ProcessadorArquivo processadorArquivo;
	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	private List<Exception> erros;
	private Arquivo arquivo;

	/**
	 * 
	 * Salvar documento pela aplicação.
	 * 
	 * @param arquivo
	 * @param uploadedFile
	 * @param usuario
	 * @return
	 */
	public ArquivoMediator salvar(Arquivo arquivo, FileUpload uploadedFile, Usuario usuario) {
		arquivo.setTipoArquivo(getTipoArquivo(arquivo));
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setStatusArquivo(setStatusArquivo());
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(getInstituicaoEnvioArquivo(usuario, uploadedFile));

		arquivo = processarArquivo(arquivo, uploadedFile);
		setArquivo(arquivoDAO.salvar(arquivo, usuario, getErros()));
		return this;
	}

	private Instituicao getInstituicaoEnvioArquivo(Usuario usuario, FileUpload uploadedFile) {
		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			return usuario.getInstituicao();
		}
		
		String nomeArquivo = uploadedFile.getClientFileName();
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
			return instituicaoDAO.getInstituicaoPorCodigo(nomeArquivo.substring(1, 4));
		} if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)
				|| TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			return identificarMunicipioEnvioPeloCabecalho(uploadedFile);
		} if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)
				|| TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)
				|| TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
			return instituicaoDAO.getInstituicaoPorCodigo(nomeArquivo.substring(2, 5));
		} else {
			throw new InfraException("Não é possível identificar o nome do arquivo ou não segue os padrões FEBRABAN.");
		}
	}

	private Instituicao identificarMunicipioEnvioPeloCabecalho(FileUpload uploadedFile) {
		String codigoMunicipio = StringUtils.EMPTY;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedFile.getInputStream()));
			codigoMunicipio = reader.readLine().substring(92, 99);
			reader.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new InfraException("Não foi possível ler o cabeçalho do arquivo!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new InfraException("Não foi possível ler o cabeçalho do arquivo!");
		}
		
		if (StringUtils.isEmpty(codigoMunicipio) || StringUtils.isBlank(codigoMunicipio) || codigoMunicipio.trim().length() != 7) {
			throw new InfraException("Código do município do cabeçalho inválido.");
		}
		Instituicao instituicao = instituicaoDAO.getCartorioPeloCodigoMunicipio(codigoMunicipio);
		if (instituicao == null) {
			throw new InfraException("Não foi possível identificar o cartório com o código do município [ "+ codigoMunicipio +" ].");
		}
		return instituicao;
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
}
