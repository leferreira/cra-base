package br.com.ieptbto.cra.regra;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.regra.entrada.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaRegrasDeEntrada {

	@Autowired
	private RegraInstituicaoEnvio regraValidarInstituicaoEnvio;
	@Autowired
	private RegraNomeArquivo regraNomeArquivo;
	@Autowired
	private RegraUsuarioEnvio regraValidarUsuarioEnvio;
	@Autowired
	private RegraDuplicidade regraValidarDuplicidade;
	@Autowired
	private RegraHoraEnvio regraHoraEnvio;
	@Autowired
	private RegraNumeroSequencialRemessa regraNumeroSequencialRemessaCabecalho;
	@Autowired
	private RegraTipoArquivoTXT regraTipoArquivoTXT;

	@SuppressWarnings("unused")
	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {

		BufferedReader reader;
		try {
			String conteudo;
			reader = new BufferedReader(new FileReader(file));
			if ((conteudo = reader.readLine()) == null) {

			}
		} catch (FileNotFoundException e) {
		} catch (IOException ex) {
		}

		regraValidarDuplicidade.validar(file, arquivo, usuario, erros);
		regraHoraEnvio.validar(file, arquivo, usuario, erros);
		regraValidarInstituicaoEnvio.validar(file, arquivo, usuario, erros);
		regraNomeArquivo.validar(file, arquivo, usuario, erros);
		regraNumeroSequencialRemessaCabecalho.validar(file, arquivo, usuario, erros);
		regraValidarUsuarioEnvio.validar(file, arquivo, usuario, erros);
		regraTipoArquivoTXT.validar(file, arquivo, usuario, erros);
	}
}