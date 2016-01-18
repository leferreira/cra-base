package br.com.ieptbto.cra.mediator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class BatimentoMediator {
	
	protected static final Logger logger = Logger.getLogger(BatimentoMediator.class);
	
	@Autowired
	private BatimentoDAO batimentoDAO;
	@Autowired
	private RetornoDAO retornoDAO;
	@Autowired
	private RetornoMediator retornoMediator;
	private Usuario usuario;
	private FileUpload fileUpload;
	
	public void processarExtrato(Usuario user, FileUpload fileUpload) {
		this.usuario = user;
		this.fileUpload = fileUpload;
		
		logger.info("Iniciando processamento de arquivo de extrato!");
		converterDepositosExtrato();
	}
	
	private void converterDepositosExtrato() {
		Boolean arquivoRetornoGeradoHoje = retornoMediator.verificarArquivoRetornoGeradoCra();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getFileUpload().getInputStream()));
			String linha = reader.readLine();
			
			/**
			 * Iniciar a leitura após a linha do saldo. (linha nº 7)
			 * */
			int numeroLinha = 1;
			while ((linha = reader.readLine()) != null) {
				
				if (numeroLinha == 7 || numeroLinha > 7) {
					if (StringUtils.isNotBlank(linha.trim())) {
						String dados[] = linha.split(Pattern.quote(";"));

						if (validarLinhaCSV(dados)) {
							Deposito deposito = new Deposito();
							deposito.setSituacaoDeposito(SituacaoDeposito.NAO_IDENTIFICADO);
							deposito.setDataImportacao(new LocalDate());
							deposito.setUsuario(getUsuario());
							
							deposito.setData(DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA ,dados[0]));
							deposito.setLancamento(dados[1]);
							deposito.setNumeroDocumento(dados[2]);
							deposito.setValorCredito(new BigDecimal(dados[3].replace(".", "").replace(",", ".")));
							
							Remessa retorno = batimentoDAO.buscarRetornoCorrespondenteAoDeposito(deposito);
							if (retorno != null) {
								Batimento batimento = new Batimento();
								batimento.setData(retornoMediator.aplicarRegraDataBatimento(arquivoRetornoGeradoHoje));
								
								BatimentoDeposito batimentoDeposito = new BatimentoDeposito();
								batimentoDeposito.setBatimento(batimento);
								batimentoDeposito.setDeposito(deposito);
								
								List<BatimentoDeposito> depositosBatimento = new ArrayList<BatimentoDeposito>();
								depositosBatimento.add(batimentoDeposito);
								
								deposito.setBatimentosDeposito(depositosBatimento);
								deposito.setSituacaoDeposito(SituacaoDeposito.IDENTIFICADO);
							
								batimento.setRemessa(retornoDAO.confirmarBatimento(retorno));
							}
							salvarDeposito(deposito);
						}
					}
				}
				numeroLinha++;
			}
			reader.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new InfraException("Não foi possível abrir o arquivo enviado.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException("Não foi possível converter os dados do arquivo!");
		}
	}
	
	private Deposito salvarDeposito(Deposito deposito) {
		return batimentoDAO.salvarDeposito(deposito);
	}

	private boolean validarLinhaCSV(String[] dados) {
		if (verificarLinhaTotalOuVazia(dados)){
			return false;
		}
		if (verificarLancamentoDeDebitoEmConta(dados)){
			return false;
		}
		if (verificarResgateMercadoAberto(dados)){
			return false;
		}
		return true;
	}

	private boolean verificarLinhaTotalOuVazia(String[] dados) {
		String campoData = dados[0];
		if (campoData != null) {
			if (StringUtils.isBlank(campoData.trim()) || campoData.trim().toUpperCase().contains("TOTAL") || 
					campoData.trim().toUpperCase().contains("DATA")) {
				return true;
			}
		}
		return false;
	}

	private boolean verificarResgateMercadoAberto(String[] dados) {
		String campolancamento = dados[1];
		if (campolancamento != null) {
			if (campolancamento.trim().equals("SALDO ANTERIOR") || campolancamento.trim().equals("RESGATE MERCADO ABERTO")) {
				return true;
			}
		}
		return false;
	}

	private boolean verificarLancamentoDeDebitoEmConta(String[] dados) {
		String campoDebito = dados[4];
		if (campoDebito != null) {
			if (!StringUtils.isBlank(campoDebito.trim()) || !campoDebito.equals(StringUtils.EMPTY)) {
				return true;
			}
		}
		return false;
	}

	public Usuario getUsuario() {
		return usuario;
	}
	
	public FileUpload getFileUpload() {
		return fileUpload;
	}

	public List<Deposito> buscarDepositosExtrato() {
		return batimentoDAO.buscarDepositosExtrato();
	}

	public List<Deposito> consultarDepositos(Deposito deposito, LocalDate dataInicio, LocalDate dataFim) {
		return batimentoDAO.consultarDepositos(deposito, dataInicio, dataFim);
	}

	public void atualizarInformacoesDeposito(Deposito deposito) {
		batimentoDAO.atualizarInformacoesDeposito(deposito);
	}
}