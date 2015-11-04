package br.com.ieptbto.cra.validacao.regra;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.enumeration.ErroValidacao;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraValidarInstituicaoEnvio extends RegrasDeEntrada {

	@Autowired
	private TipoInstituicaoMediator tipoInstituicaoMediator;
	private Instituicao instituicao;
	private TipoArquivoEnum tipoArquivo;
	
	@Override
	protected void validar(File arquivo, Arquivo arquivoProcessado, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.instituicao = usuario.getInstituicao();
		this.tipoArquivo = arquivoProcessado.getTipoArquivo().getTipoArquivo();
		setErros(erros);

		executar();
	}

	@Override
	protected void executar() {
		verificarInstituicaoDeEnvio();
		if (!verificarPermissaoDeEnvioDaInstituicao()) {
			throw new InfraException(ErroValidacao.USUARIO_SEM_PERMISSAO_DE_ENVIO_DE_ARQUIVO.getMensagemErro());
		}
	}

	/**
	 * Verifica se a instituição não está bloqueada para envio do arquivo
	 */
	private void verificarInstituicaoDeEnvio() {
		try {
			if (!usuario.getInstituicao().isSituacao()) {
				logger.error(ErroValidacao.INSTITUICAO_BLOQUEADA.getMensagemErro());
				throw new InfraException(ErroValidacao.INSTITUICAO_BLOQUEADA.getMensagemErro());
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new InfraException(ErroValidacao.INSTITUICAO_BLOQUEADA.getMensagemErro(), ex.getCause());
		}
	}

	/**
	 * Verifica se a instituição tem permissão para envio do tipo de arquivo (B,
	 * C, R ou DP)
	 */
	private boolean verificarPermissaoDeEnvioDaInstituicao() {
		List<PermissaoEnvio> permissoes = tipoInstituicaoMediator.permissoesPorTipoInstituicao(getInstituicao().getTipoInstituicao());
		for (PermissaoEnvio permissao : permissoes) {
			if (tipoArquivo.equals(permissao.getTipoArquivo().getTipoArquivo())) {
				return true;
			}
		}
		return false;
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	public TipoArquivoEnum getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivoEnum tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

}
