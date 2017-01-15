package br.com.ieptbto.cra.mediator;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.BancoTipoRegraBasicaInstrumento;
import br.com.ieptbto.cra.enumeration.BooleanSimNao;
import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;
import br.com.ieptbto.cra.processador.ProcessadorArquivoDeParaBB;
import br.com.ieptbto.cra.slip.ArquivoBradesco;
import br.com.ieptbto.cra.slip.ArquivoCAF;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoDeParaMediator extends BaseMediator {

	@Autowired
	ArquivoDeParaDAO deParaDAO;
	@Autowired
	ProcessadorArquivoDeParaBB processadorArquivoDeParaBB;

	public void processarArquivo(FileUpload uploadedFile, PadraoArquivoDePara padraoArquivo, BooleanSimNao limparBase) {
		if (limparBase.getBool()) {
			limparBasesAgencias(padraoArquivo);
		}

		if (padraoArquivo.equals(PadraoArquivoDePara.ARQUIVO_DE_PARA_ATUALIZACAO)) {

		} else if (padraoArquivo.equals(PadraoArquivoDePara.CAF)) {
			deParaDAO.salvarArquivoCAF(new ArquivoCAF().processar(uploadedFile));
		} else if (padraoArquivo.equals(PadraoArquivoDePara.BANCO_DO_BRASIL)) {
			processadorArquivoDeParaBB.iniciarProcessamento(uploadedFile);
		} else if (padraoArquivo.equals(PadraoArquivoDePara.BRADESCO)) {
			deParaDAO.salvarArquivoBradesco(new ArquivoBradesco().processar(uploadedFile));
		}
	}

	private void limparBasesAgencias(PadraoArquivoDePara padraoArquivo) {
		if (padraoArquivo.equals(PadraoArquivoDePara.ARQUIVO_DE_PARA_ATUALIZACAO)) {

		} else if (padraoArquivo.equals(PadraoArquivoDePara.CAF)) {
			deParaDAO.limparAgenciasCaf();
		} else if (padraoArquivo.equals(PadraoArquivoDePara.BANCO_DO_BRASIL)) {
			deParaDAO.limparAgenciasBancoDoBrasil();
		} else if (padraoArquivo.equals(PadraoArquivoDePara.BRADESCO)) {
			deParaDAO.limparAgenciasBradesco();
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
