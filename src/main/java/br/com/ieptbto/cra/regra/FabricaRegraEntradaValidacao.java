package br.com.ieptbto.cra.regra;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.regra.entrada.RegraEntrada;

/**
 * 
 * @author Lefer, Thasso Araújo
 * 
 *         Fábria de regras e validações do arquivo
 *
 */
@Service
public class FabricaRegraEntradaValidacao {

	protected static final Logger logger = Logger.getLogger(RegraEntrada.class);

	@Autowired
	private FabricaRegrasDeEntrada fabricaRegrasDeEntrada;
	@Autowired
	private FabricaRegrasDeValidacao fabricaRegrasValidacao;
	@Autowired
	private FabricaValidacaoTitulo fabricaValidacaoTitulo;

	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		logger.info("Iniciando validações do arquivo " + arquivo.getNomeArquivo() + " enviado pelo usuário " + usuario.getLogin());

		fabricaRegrasDeEntrada.validar(file, arquivo, usuario, erros);
		fabricaRegrasValidacao.validar(arquivo, usuario, erros);
		fabricaValidacaoTitulo.validar(arquivo, usuario, erros);

		logger.info("Fim de validações do arquivo " + arquivo.getNomeArquivo() + " enviado pelo usuário " + usuario.getLogin());
	}

	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		logger.info("Iniciando validações do arquivo " + arquivo.getNomeArquivo() + " enviado pelo usuário " + usuario.getLogin());

		fabricaRegrasValidacao.validar(arquivo, usuario, erros);
		fabricaValidacaoTitulo.validar(arquivo, usuario, erros);

		logger.info("Fim de validações do arquivo " + arquivo.getNomeArquivo() + " enviado pelo usuário " + usuario.getLogin());
	}
}