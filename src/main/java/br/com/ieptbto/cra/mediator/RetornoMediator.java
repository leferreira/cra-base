package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RetornoMediator {

	private static final int NUMERO_SEQUENCIAL_RETORNO = 1;
	
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private RetornoDAO retornoDao;
	@Autowired
	private BatimentoDAO batimentoDAO;
	private Instituicao cra;
	private TipoArquivo tipoArquivo;
	private Arquivo arquivo;
	
	public List<Remessa> buscarRetornosParaBatimento(){
		return retornoDao.buscarRetornosParaBatimento();
	}
	
	public List<Remessa> buscarRetornosAguardandoLiberacao(Instituicao instiuicao, LocalDate dataBatimento, boolean dataComoDataLimite){
		return retornoDao.buscarRetornosAguardandoLiberacao(instiuicao ,dataBatimento, dataComoDataLimite);
	}
	
	public List<Remessa> buscarRetornosConfirmados(){
		return retornoDao.buscarRetornosConfirmados();
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		return retornoDao.buscarValorDeTitulosPagos(retorno);
	}
	
	public BigDecimal buscarSomaValorTitulosPagosRemessas(Instituicao instituicao, LocalDate dataBatimento, boolean dataComoDataLimite) {
		return retornoDao.buscarSomaValorTitulosPagosRemessas(instituicao, dataBatimento, dataComoDataLimite);
	}
	
	public BigDecimal buscarValorDeCustasCartorio(Remessa retorno){
		return retornoDao.buscarValorDeCustasCartorio(retorno);
	}
	
	public Boolean verificarArquivoRetornoGeradoCra(){
		return retornoDao.verificarArquivoRetornoGeradoCra(getCra());
	}
	
	public void salvarBatimentos(List<Remessa> retornos){
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");

		Boolean arquivoRetornoGeradoHoje = verificarArquivoRetornoGeradoCra();
		for (Remessa retorno : retornos) {
			Batimento batimento = new Batimento();
			batimento.setData(aplicarRegraDataBatimento(arquivoRetornoGeradoHoje));
			batimento.setRemessa(retorno);
			batimento.setDepositosBatimento(new ArrayList<BatimentoDeposito>());
			
			for (Deposito depositosIdentificado : retorno.getListaDepositos()) {
				BatimentoDeposito depositosBatimento = new BatimentoDeposito();
				depositosBatimento.setBatimento(batimento);
				depositosBatimento.setDeposito(depositosIdentificado);
				
				batimento.getDepositosBatimento().add(depositosBatimento);
			}
			retornoDao.salvarBatimento(batimento);
		}
	}
	
	public LocalDate aplicarRegraDataBatimento(Boolean arquivoRetornoGeradoHoje) {
		
		if (arquivoRetornoGeradoHoje.equals(false)) {
			return new LocalDate();
		} else if (arquivoRetornoGeradoHoje.equals(true)) {
			Integer contadorDeDias = 1;
			while (true) {
				LocalDate proximoDiaUtil = new LocalDate().plusDays(contadorDeDias);
				Date date = proximoDiaUtil.toDate();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				
				if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
		        	return proximoDiaUtil;
		        }
				contadorDeDias++;
			}
		}
		return null;
	}

	public void removerBatimento(Remessa retorno){
		Batimento batimento = batimentoDAO.buscarBatimentoDoRetorno(retorno);
		
		for (BatimentoDeposito depositosBatimento : batimento.getDepositosBatimento()) {
			Deposito deposito = depositosBatimento.getDeposito();
			List<Batimento> batimentosDoDeposito = batimentoDAO.buscarBatimentosDoDeposito(deposito);
			if (batimentosDoDeposito.size() > 1){
				throw new InfraException("O arquivo de retorno possui um depósito vínculado a mais de um batimento! Não é possível removê-lo...");
			} else if (!batimentosDoDeposito.isEmpty()) {
				deposito.setSituacaoDeposito(SituacaoDeposito.NAO_IDENTIFICADO);
				batimentoDAO.atualizarDeposito(deposito);
			}
		}
		retornoDao.removerBatimento(retorno, batimento);
	}
	
	public void liberarRetornoBatimentoInstituicao(List<Remessa> retornoLiberados) {
		retornoDao.liberarRetornoBatimento(retornoLiberados);
	}
	
	public void gerarRetornos(Usuario usuarioAcao, List<Remessa> retornos){
		HashMap<String, Arquivo> arquivosRetorno = new HashMap<String, Arquivo>();
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		this.tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.RETORNO);
		
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa retorno: retornos){
			
			if (arquivo == null || !instituicaoDestino.equals(retorno.getInstituicaoDestino())){
				instituicaoDestino = retorno.getInstituicaoDestino();
				criarNovoArquivoDeRetorno(instituicaoDestino, retorno);
				
				if (!arquivosRetorno.containsKey(instituicaoDestino.getCodigoCompensacao()) && arquivo != null) {
					List<Remessa> retornosDaInstituicao = retornoDao.buscarRetornosConfirmadosPorInstituicao(instituicaoDestino);
					arquivo.setRemessas(retornosDaInstituicao);
					arquivosRetorno.put(instituicaoDestino.getCodigoCompensacao(), arquivo);
				}
			} 
		}
		List<Arquivo> retornosArquivos = new ArrayList<Arquivo>(arquivosRetorno.values());
		retornoDao.gerarRetornos(usuarioAcao ,retornosArquivos);
	}
	
	private void criarNovoArquivoDeRetorno(Instituicao destino, Remessa retorno) {
		this.arquivo = new Arquivo();
		getArquivo().setTipoArquivo(getTipoArquivo());
		getArquivo().setNomeArquivo(gerarNomeArquivoRetorno(retorno));
		getArquivo().setInstituicaoRecebe(destino);
		getArquivo().setInstituicaoEnvio(getCra());
		getArquivo().setDataEnvio(new LocalDate());
		getArquivo().setHoraEnvio(new LocalTime());
	}
	
	private String gerarNomeArquivoRetorno(Remessa retorno) {
		
		return TipoArquivoEnum.RETORNO.getConstante() 
			+ retorno.getCabecalho().getNumeroCodigoPortador()
			+ gerarDataArquivo()
			+ NUMERO_SEQUENCIAL_RETORNO;
	}

	private String gerarDataArquivo(){
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(new Date()).toString();
	}

	public Instituicao getCra() {
		return cra;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}
}
