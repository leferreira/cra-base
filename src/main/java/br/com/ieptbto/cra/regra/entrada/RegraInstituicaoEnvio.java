package br.com.ieptbto.cra.regra.entrada;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.enumeration.ErroValidacao;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraInstituicaoEnvio extends RegraEntrada {

	@Autowired
	TipoInstituicaoMediator tipoInstituicaoMediator;

	private Instituicao instituicao;
	private TipoArquivoFebraban tipoArquivo;

	@Override
	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.file = file;
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;
		this.instituicao = usuario.getInstituicao();
		this.tipoArquivo = arquivo.getTipoArquivo().getTipoArquivo();

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
		return true;
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	public TipoArquivoFebraban getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivoFebraban tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}
}