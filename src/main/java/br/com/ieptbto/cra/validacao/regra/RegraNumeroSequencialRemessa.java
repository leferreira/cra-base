package br.com.ieptbto.cra.validacao.regra;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RegraNumeroSequencialRemessa extends RegrasDeEntrada {

	private Usuario usuario;
	private File arquivo;
	private Arquivo arquivoProcessado;

	@Override
	protected void validar(File arquivo, Arquivo arquivoProcessado, Usuario usuario, List<Exception> erros) {
		setArquivo(arquivo);
		setUsuario(usuario);
		setErros(erros);
		setArquivoProcessado(arquivoProcessado);

		executar();
	}

	@Override
	protected void executar() {
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public File getArquivo() {
		return arquivo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setArquivo(File arquivo) {
		this.arquivo = arquivo;
	}

	public Arquivo getArquivoProcessado() {
		return arquivoProcessado;
	}

	public void setArquivoProcessado(Arquivo arquivoProcessado) {
		this.arquivoProcessado = arquivoProcessado;
	}
}
