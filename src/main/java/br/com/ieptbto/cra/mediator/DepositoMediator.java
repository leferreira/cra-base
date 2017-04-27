package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.dao.DepositoDAO;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoDeposito;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DepositoMediator extends BaseMediator {

	@Autowired
	private BatimentoMediator batimentoMediator;
	@Autowired
	private RemessaMediator remessaMediator;
	@Autowired
	private DepositoDAO depositoDAO;
	@Autowired
	private BatimentoDAO batimentoDAO;
	
	private Usuario usuario;
	private FileUpload fileUpload;
	private List<Deposito> depositos;
	private List<Deposito> depositosConflitados;
	private List<Remessa> retornosConflitados;
	
	/**
	 * Método para atualização de Depósitos
	 * @param deposito
	 * @return
	 */
	public Deposito atualizarDeposito(Deposito deposito) {
		return depositoDAO.atualizarDeposito(deposito);
	}
	
	/**
	 * Buscar depósitos não identificados
	 * @return
	 */
	public List<Deposito> buscarDepositosNaoIdentificados() {
		return depositoDAO.buscarDepositosNaoIdentificados();
	}

	/**
	 * @param deposito
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public List<Deposito> consultarDepositos(Deposito deposito, LocalDate dataInicio, LocalDate dataFim) {
		return depositoDAO.consultarDepositos(deposito, dataInicio, dataFim);
	}

	/**
	 * Processar arquivo de extrados baixado do site do Bradesco em formato CSV
	 * @param user
	 * @param fileUpload
	 */
	public DepositoMediator processarDepositosExtrato(Usuario user, FileUpload fileUpload) {
		this.usuario = user;
		this.fileUpload = fileUpload;
		this.depositosConflitados = new ArrayList<>();
		this.retornosConflitados = new ArrayList<>();
		
		List<Object> retornosPendetesDepositos = batimentoDAO.buscarRetornoCorrespondenteAoDeposito();
		this.depositos = converterDepositosExtrato();

		for (Deposito deposito : depositos) {
			deposito = depositoDAO.salvarDeposito(deposito);
			List<Remessa> retornos = verificarExistenciaArquivoComMesmoValor(retornosPendetesDepositos, deposito); 
			if (retornos.size() == 1) {
				List<Deposito> depositosProcessados = new ArrayList<>();
				depositosProcessados.addAll(depositos);
				if (deposito.containsDepositosMesmoValor(depositosProcessados)) {
					depositosConflitados.add(deposito);
					if (!retornosConflitados.containsAll(retornos)) {
						retornosConflitados.addAll(retornos);
					}
				} else {
					for (Remessa remessa : retornos) {
						remessa.setListaDepositos(new ArrayList<Deposito>());
						remessa.getListaDepositos().add(deposito);
						batimentoMediator.salvarBatimento(remessa);
					}
				}
			} else if (retornos.size() > 1) {
				depositosConflitados.add(deposito);
				retornosConflitados.addAll(retornos);
			}
			retornos = null;
		}
		return this;
	}
	
	private List<Remessa> verificarExistenciaArquivoComMesmoValor(List<Object> retornosPendetesDepositos, Deposito deposito) {
		List<Remessa> arquivosRetorno = new ArrayList<Remessa>();
		Iterator<Object> iterator = retornosPendetesDepositos.iterator();
		while (iterator.hasNext()) {
			Object[] object = (Object[]) iterator.next();
			Integer id = Integer.class.cast(object[0]);
			BigDecimal valor = BigDecimal.class.cast(object[1]);
			if (valor.equals(deposito.getValorCredito())) {
				arquivosRetorno.add(remessaMediator.buscarRemessaPorPK(id));
			}
		}
		return arquivosRetorno;
	}


	private List<Deposito> converterDepositosExtrato() {
		Integer numeroLinha = 1;
		List<Deposito> depositos = new ArrayList<>();

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
							deposito.setIndexExtrato(numeroLinha);
							depositos.add(deposito);
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
		return depositos;
	}
	
	private TipoDeposito verificaTipoDeposito(String dados[]) {
		if (dados[2] != null) {
			String numeroDocumento = RemoverAcentosUtil.removeAcentos(dados[2]);
			if (numeroDocumento.toUpperCase().trim().equals(ConfiguracaoBase.DEPOSITO_CARTORIO))
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
	
	public List<Deposito> getDepositos() {
		return depositos;
	}
	
	public List<Deposito> getDepositosConflitados() {
		return depositosConflitados;
	}

	public List<Remessa> getRetornosConflitados() {
		return retornosConflitados;
	}
	
	/**
	 * Processar depósitos de valores em conflito com mais de 
	 * um arquivo de retorno
	 * @param user
	 * @param fileUpload
	 */
	public List<Deposito> processarDepositosConflito(List<Deposito> depositos) {
		for (Deposito deposito : depositos) {
			
			if (deposito.getRemessas() != null) {
				for (Remessa retorno : deposito.getRemessas()){
					retorno.setListaDepositos(new ArrayList<Deposito>());
					retorno.getListaDepositos().add(deposito);
					batimentoMediator.salvarBatimento(retorno);
				}
			}
		}
		return depositos;
	}
}