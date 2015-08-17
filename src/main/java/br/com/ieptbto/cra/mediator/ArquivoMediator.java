package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;
import br.com.ieptbto.cra.processador.ProcessadorMigracaoCRA;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoMediator {

	@Autowired
	InstituicaoMediator instituicaoMediator;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private ArquivoDAO arquivoDAO;
	private List<Exception> erros;
	private Arquivo arquivo;

	@Autowired
	private ProcessadorArquivo processadorArquivo;
	@Autowired
	private ProcessadorMigracaoCRA processadorArquivoMigracao;

	public ArquivoMediator salvar(Arquivo arquivo, FileUpload uploadedFile, Usuario usuario) {
		arquivo.setTipoArquivo(getTipoArquivo(arquivo));
		arquivo.setStatusArquivo(setStatusArquivo());
		if (verificarPermissaoDeEnvio(usuario, arquivo)) {
			throw new InfraException("O usuário " + usuario.getNome() + " não pode enviar arquivos " + arquivo.getNomeArquivo());
		}

		processarArquivo(arquivo, uploadedFile);
		
		/** 
		 * @TODO Processador para receber os arquivos de Confirmacao e Retorno, gerados pela CRA ANTIGA.
		 * Arquivos com mais de uma praça de protesto. Para fins de migração do sistema apenas!
		 */
		if (isArquivoMigracao(arquivo)) {
			setArquivo(processarArquivoMigracao(arquivo, usuario).getArquivo());
		} else {
			arquivo.setInstituicaoEnvio(setInstituicaoEnvio(arquivo));
			setArquivo(arquivoDAO.salvar(arquivo, usuario));
		}
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
	
	private Instituicao setInstituicaoEnvio(Arquivo arquivo) {

		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(arquivo.getNomeArquivo().substring(1, 4));			
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)
				|| arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(arquivo.getNomeArquivo().substring(2, 5));
		} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO) 
				|| arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)
				|| arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
			return instituicaoMediator.getCartorioPorCodigoIBGE(arquivo.getRemessas().get(0).getCabecalho().getCodigoMunicipio());
		} else {
			throw new InfraException("Tipo Do arquivo [" + arquivo.getTipoArquivo().getTipoArquivo().getLabel() + "] inválido");
		}
	}

	private ProcessadorMigracaoCRA processarArquivoMigracao(Arquivo arquivo, Usuario usuario) {
		this.processadorArquivoMigracao = new ProcessadorMigracaoCRA();
		return processadorArquivoMigracao.processarArquivoMigracao(arquivo, usuario);
	}

	private boolean isArquivoMigracao(Arquivo arquivo) {
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO) ||
				arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			
			if (verificarSeTemMaisDeUmaPraca(arquivo))
				return true;
		}
		return false;
	}
	
	private boolean verificarSeTemMaisDeUmaPraca(Arquivo arquivo) {
		List<String> pracasProtesto = new ArrayList<String>();
		
		for (Remessa remessa : arquivo.getRemessas()) {
			if (!pracasProtesto.contains(remessa.getCabecalho().getCodigoMunicipio())) {
				pracasProtesto.add(remessa.getCabecalho().getCodigoMunicipio());
				if (pracasProtesto.size() > 1) {
					return true;
				}
			}
		}
		return false;
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

	private void processarArquivo(Arquivo arquivo, FileUpload uploadedFile) throws InfraException {
		processadorArquivo.processarArquivo(uploadedFile, arquivo, getErros());
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
	
	public List<Arquivo> buscarArquivosAvancado(Arquivo arquivo, Instituicao instituicao, ArrayList<TipoArquivoEnum> tipoArquivos,
	        Municipio pracaProtesto, LocalDate dataInicio, LocalDate dataFim, ArrayList<SituacaoArquivo> situacoes) {
		return arquivoDAO.buscarArquivosAvancado(arquivo, instituicao, tipoArquivos, pracaProtesto, dataInicio, dataFim, situacoes);
	}
}
