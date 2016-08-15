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
	private File file;
	private Arquivo arquivo;
	private Usuario usuario;
	private List<Exception> erros;

	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;
		this.file = file;

		logger.info("Iniciando validações do arquivo " + arquivo.getNomeArquivo() + " enviado pelo usuário " + usuario.getLogin());

		fabricaRegrasDeEntrada.validar(getFile(), getArquivo(), getUsuario(), getErros());
		fabricaRegrasValidacao.validar(getArquivo(), getUsuario(), getErros());

		logger.info("Fim de validações do arquivo " + arquivo.getNomeArquivo() + " enviado pelo usuário " + usuario.getLogin());
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public List<Exception> getErros() {
		return erros;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public File getFile() {
		return file;
	}
}