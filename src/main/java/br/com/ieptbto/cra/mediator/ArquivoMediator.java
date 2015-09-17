package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

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
	private InstituicaoMediator instituicaoMediator;
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
