package br.com.ieptbto.cra.validacao;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.validacao.regra.FabricaRegrasDeEntrada;

/**
 * 
 * @author Lefer
 * 
 *         Fábria de validações do arquivo
 *
 */
@Service
public class FabricaValidacaoArquivo {

	@Autowired
	private FabricaRegrasDeEntrada fabricaRegrasDeEntrada;
	private File arquivoFisico;
	private Arquivo arquivoProcessado;
	private Usuario usuario;
	private List<Exception> erros;

	public void validar(File arquivoFisico, Arquivo arquivo ,Usuario usuario, List<Exception> erros) {
		this.arquivoFisico = arquivoFisico;
		this.arquivoProcessado = arquivo;
		this.usuario = usuario;
		this.erros = erros;
		validarEntradaDoArquivo();
	}

	private void validarEntradaDoArquivo() {
		fabricaRegrasDeEntrada.validar(getArquivoFisico(), getArquivoProcessado(), getUsuario(), getErros());
	}

	public File getArquivoFisico() {
		return arquivoFisico;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public List<Exception> getErros() {
		return erros;
	}

	public void setArquivoFisico(File arquivoFisico) {
		this.arquivoFisico = arquivoFisico;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

	public Arquivo getArquivoProcessado() {
		return arquivoProcessado;
	}

	public void setArquivoProcessado(Arquivo arquivoProcessado) {
		this.arquivoProcessado = arquivoProcessado;
	}

}
