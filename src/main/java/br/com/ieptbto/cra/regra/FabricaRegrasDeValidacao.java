package br.com.ieptbto.cra.regra;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.regra.validacao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class FabricaRegrasDeValidacao {

	@Autowired
	private ValidarCidadeCodigoIBGE regraVerificarCidadeCodigoIBGE;
	@Autowired
	private ValidarSequencialCabecalho regraVerificarSequencialCabecalho;
	@Autowired
	private ValidarAgenciaCentralizadora regraVerificarAgenciaCentralizadora;
	@Autowired
	private ValidarCabecalho regraVerificarDadosCabecalho;
	@Autowired
	private ValidarRodape regraVerificarDadosRodape;

	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		regraVerificarDadosCabecalho.validar(arquivo, usuario, erros);
		regraVerificarCidadeCodigoIBGE.validar(arquivo, usuario, erros);
		regraVerificarSequencialCabecalho.validar(arquivo, usuario, erros);
		regraVerificarAgenciaCentralizadora.validar(arquivo, usuario, erros);
		regraVerificarDadosRodape.validar(arquivo, usuario, erros);
	}
}
