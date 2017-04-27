package br.com.ieptbto.cra.regra.titulo;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ValidarTituloRemessa extends RegraTitulo {

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	@Override
	protected void executar() {
	}
}
