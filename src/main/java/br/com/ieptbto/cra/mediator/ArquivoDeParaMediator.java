package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;
import br.com.ieptbto.cra.enumeration.regra.RegraBasicaInstrumentoBanco;
import br.com.ieptbto.cra.processador.ProcessadorArquivoDeParaBB;
import br.com.ieptbto.cra.slip.ArquivoBradesco;
import br.com.ieptbto.cra.slip.ArquivoCAF;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	/**
	 * Processador de arquivo De/Para de Agências
	 * 
	 * @param uploadedFile
	 * @param padraoArquivo
	 * @param limparBase
	 */
	public void processarArquivo(FileUpload uploadedFile, PadraoArquivoDePara padraoArquivo, boolean limparBase) {
		if (limparBase) {
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

	/**
	 * Buscar agência do Banco do Brasil de acordo com o núemro do contrato
	 * 
	 * @param numeroContrato
	 * @return
	 */
	public AgenciaBancoDoBrasil buscarAgenciaBancoDoBrasilPorContrato(String numeroContrato) {
		return deParaDAO.buscarAgenciaBancoDoBrasilPorContrato(numeroContrato);
	}

	/**
	 * Buscar agência do Banco Bradesco de acordo com os dados de título
	 * 
	 * @param tituloRemessa
	 * @return
	 */
	public AgenciaBradesco buscarAgenciaBradescoPorTitulo(TituloRemessa tituloRemessa) {
		return deParaDAO.buscarAgenciaBradescoPorTitulo(tituloRemessa);
	}

	/**
	 * Buscar agência CAF por código da agência e tipoRegra
	 * 
	 * @param agencia
	 * @param bancoTipo
	 * @return
	 */
	public AgenciaCAF buscarAgenciaCAFPorCodigoRegra(String agencia, RegraBasicaInstrumentoBanco bancoTipo) {
		return deParaDAO.buscarAgenciaCAFPorCodigoRegra(agencia, bancoTipo);
	}
}