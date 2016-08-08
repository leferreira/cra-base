package br.com.ieptbto.cra.regra.entrada;

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
public class RegraNumeroSequencialRemessa extends RegraEntrada {

	@Override
	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.file = file;
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;

		executar();
	}

	@Override
	protected void executar() {
	}
}