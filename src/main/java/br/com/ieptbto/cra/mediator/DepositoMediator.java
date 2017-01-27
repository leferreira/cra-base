package br.com.ieptbto.cra.mediator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.DepositoDAO;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoDeposito;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

@Service
public class DepositoMediator extends BaseMediator {

	@Autowired
	DepositoDAO depositoDAO;
	private Usuario usuario;
	private FileUpload fileUpload;
	private List<Deposito> depositos;
	private int numeroLinha;
	
	/**
	 * @param deposito
	 * @return
	 */
	public List<Remessa> buscarArquivosRetornosVinculadosPorDeposito(Deposito deposito) {
		return batimentoDAO.buscarArquivosRetornosVinculadosPorDeposito(deposito);
	}

	/**
	 * Método para atualização de Depósitos
	 * @param deposito
	 * @return
	 */
	public Deposito atualizarDeposito(Deposito deposito) {
		return batimentoDAO.atualizarDeposito(deposito);
	}
	
	/**
	 * Buscar depósitos não identificados
	 * @return
	 */
	public List<Deposito> buscarDepositosNaoIdentificados() {
		return batimentoDAO.buscarDepositosNaoIdentificados();
	}

	/**
	 * @param deposito
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public List<Deposito> consultarDepositos(Deposito deposito, LocalDate dataInicio, LocalDate dataFim) {
		return batimentoDAO.consultarDepositos(deposito, dataInicio, dataFim);
	}
	
	/**
	 * Processar arquivo de extrados baixado do site do Bradesco em formato CSV
	 * @param user
	 * @param fileUpload
	 */
	public void processarExtrato(Usuario user, FileUpload fileUpload) {
		this.usuario = user;
		this.fileUpload = fileUpload;

		converterDepositosExtrato();
		vincularDepositosRetornosNaoConfirmados();
	}

	private void converterDepositosExtrato() {
		this.depositos = new ArrayList<>();
		this.numeroLinha = 1;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.fileUpload.getInputStream()));

			String linha = reader.readLine();
			while ((linha = reader.readLine()) != null) {
				if (numeroLinha == 7 || numeroLinha > 7) {
					if (StringUtils.isNotBlank(linha.trim())) {
						String dados[] = linha.split(Pattern.quote(";"));
						if (validarLinhaCSV(dados)) {
							Deposito deposito = new Deposito();
							deposito.setSituacaoDeposito(SituacaoDeposito.NAO_IDENTIFICADO);
							deposito.setDataImportacao(new LocalDate());
							deposito.setUsuario(usuario);
							deposito.setTipoDeposito(verificaTipoDeposito(dados));
							deposito.setData(DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA, dados[0]));
							deposito.setLancamento(RemoverAcentosUtil.removeAcentos(dados[1]));
							deposito.setNumeroDocumento(dados[2]);
							deposito.setValorCredito(new BigDecimal(dados[3].trim().replace(".", "").replace(",", ".")));
							
							this.depositos.add(deposito);
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
			throw new InfraException("Não foi possível converter os dados da linha [ Nº " + numeroLinha + " ]. Verifique as informações do depósito...");
		}
	}
	
	private void vincularDepositosRetornosNaoConfirmados() {
		Boolean arquivoRetornoGeradoHoje = retornoMediator.verificarArquivoRetornoGeradoCra();
		
		for (Deposito deposito : depositos) {
			Remessa retorno = batimentoDAO.buscarRetornoCorrespondenteAoDeposito(deposito);
			if (retorno != null) {
				Batimento batimento = new Batimento();
				batimento.setData(retornoMediator.aplicarRegraDataBatimento(arquivoRetornoGeradoHoje));
				batimento.setDataBatimento(new LocalDateTime());
				batimento.setRemessa(retorno);
				
				BatimentoDeposito batimentoDeposito = new BatimentoDeposito();
				batimentoDeposito.setBatimento(batimento);
				batimentoDeposito.setDeposito(deposito);
				
				List<BatimentoDeposito> depositosBatimento = new ArrayList<BatimentoDeposito>();
				depositosBatimento.add(batimentoDeposito);
				deposito.setBatimentosDeposito(depositosBatimento);
				deposito.setSituacaoDeposito(SituacaoDeposito.IDENTIFICADO);
			}
			batimentoDAO.salvarDeposito(deposito);
		}
	}

	private TipoDeposito verificaTipoDeposito(String dados[]) {
		if (dados[2] != null) {
			String numeroDocumento = RemoverAcentosUtil.removeAcentos(dados[2]);
			if (numeroDocumento.toUpperCase().trim().equals(CONSTANTE_TIPO_DEPOSITO_CARTORIO))
				return TipoDeposito.DEPOSITO_CARTORIO_PARA_BANCO;
		}
		return TipoDeposito.NAO_INFORMADO;
	}

	private boolean validarLinhaCSV(String[] dados) {
		if (verificarLinhaTotalOuVazia(dados)) {
			return false;
		}
		if (verificarLancamentoDeDebitoEmConta(dados)) {
			return false;
		}
		if (verificarResgateMercadoAberto(dados)) {
			return false;
		}
		return true;
	}

	private boolean verificarLinhaTotalOuVazia(String[] dados) {
		String campoData = dados[0];
		if (campoData != null) {
			if (StringUtils.isBlank(campoData.trim()) || campoData.trim().toUpperCase().contains("TOTAL") || campoData.trim().toUpperCase().contains("DATA")) {
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
			if (!StringUtils.isBlank(campoDebito.trim()) || !campoDebito.trim().equals(StringUtils.EMPTY)) {
				return true;
			}
		}
		return false;
	}
}
