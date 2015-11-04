package br.com.ieptbto.cra.validacao;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.validacao.regra.RegrasDeEntrada;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RegraValidarHoraEnvio extends RegrasDeEntrada {

	@Override
	public void validar(File arquivo, Arquivo arquivoProcessado,
			Usuario usuario, List<Exception> erros) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executar() {
		// TODO Auto-generated method stub
		
	}
}