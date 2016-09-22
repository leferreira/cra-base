package br.com.ieptbto.cra.regra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.regra.entrada.RegraDuplicidade;
import br.com.ieptbto.cra.regra.entrada.RegraHoraEnvio;
import br.com.ieptbto.cra.regra.entrada.RegraInstituicaoEnvio;
import br.com.ieptbto.cra.regra.entrada.RegraNomeArquivo;
import br.com.ieptbto.cra.regra.entrada.RegraNumeroSequencialRemessa;
import br.com.ieptbto.cra.regra.entrada.RegraTipoArquivoTXT;
import br.com.ieptbto.cra.regra.entrada.RegraUsuarioEnvio;

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