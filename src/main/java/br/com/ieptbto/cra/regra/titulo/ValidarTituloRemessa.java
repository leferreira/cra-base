package br.com.ieptbto.cra.regra.titulo;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;

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
		// TODO Auto-generated method stub

	}
}
