package br.com.ieptbto.cra.validacao.regra;

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
	private Arquivo arquivo;

	@Override
	protected void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		setArquivo(arquivo);
		setUsuario(usuario);
		setErros(erros);

		executar();
	}

	@Override
	protected void executar() {
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}
}
