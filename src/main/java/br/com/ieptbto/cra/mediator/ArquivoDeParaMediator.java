package br.com.ieptbto.cra.mediator;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.arquivoDePara.ArquivoBradesco;
import br.com.ieptbto.cra.arquivoDePara.ArquivoCAF;
import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.BancoTipoRegraBasicaInstrumento;
import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivoDeParaBB;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoDeParaMediator {

	@Autowired
	ArquivoDeParaDAO deParaDAO;
	@Autowired
	ProcessadorArquivoDeParaBB processadorArquivoDeParaBB;

	public void processarArquivo(FileUpload uploadedFile) {

		if (uploadedFile.getClientFileName().contains(PadraoArquivoDePara.ARQUIVO_DE_PARA_ATUALIZACAO.getModelo())) {

		} else if (uploadedFile.getClientFileName().contains(PadraoArquivoDePara.CAF.getModelo())) {
			deParaDAO.salvarArquivoCAF(new ArquivoCAF().processar(uploadedFile));
		} else if (uploadedFile.getClientFileName().contains(PadraoArquivoDePara.BANCO_DO_BRASIL.getModelo())) {
			processadorArquivoDeParaBB.iniciarProcessamento(uploadedFile);
		} else if (uploadedFile.getClientFileName().toUpperCase().contains(PadraoArquivoDePara.BRADESCO.getModelo())) {
			deParaDAO.salvarArquivoBradesco(new ArquivoBradesco().processar(uploadedFile));
		} else {
			new InfraException("Não foi possível definir o modelo do arquivo de/para ! Entre em contato com a CRA !");
		}
	}

	public AgenciaBancoDoBrasil buscarAgenciaArquivoBancoDoBrasil(String numeroContrato) {
		return deParaDAO.buscarAgenciaArquivoBancoDoBrasil(numeroContrato);
	}

	public AgenciaBradesco buscarAgenciaArquivoDeParaBradesco(TituloRemessa tituloRemessa) {
		return deParaDAO.buscarAgenciaArquivoDeParaBradesco(tituloRemessa);
	}

	public AgenciaCAF buscarAgenciaArquivoCAF(String agencia, BancoTipoRegraBasicaInstrumento bancoTipo) {
		return deParaDAO.buscarAgenciaArquivoCAF(agencia, bancoTipo);
	}
}
