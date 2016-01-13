package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
	
	public List<Remessa> buscarRetornosAguardandoLiberacao(){
		return retornoDao.buscarRetornosAguardandoLiberacao();
	}
	
	public List<Remessa> buscarRetornosConfirmados(){
		return retornoDao.buscarRetornosConfirmados();
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		return retornoDao.buscarValorDeTitulosPagos(retorno);
	}
	
	public BigDecimal buscarValorDeCustasCartorio(Remessa retorno){
		return retornoDao.buscarValorDeCustasCartorio(retorno);
	}
	
	public void salvarBatimentos(List<Remessa> retornos){
		
		for (Remessa retorno : retornos) {
			Batimento batimento = new Batimento();
			batimento.setDataBatimento(new LocalDateTime());
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
	
	public void removerBatimento(Remessa retorno){
		Batimento batimento = batimentoDAO.buscarBatimentoDoRetorno(retorno);
		
		for (BatimentoDeposito depositosBatimento : batimento.getDepositosBatimento()) {
			Deposito deposito = depositosBatimento.getDeposito();
			List<Batimento> batimentosDoDeposito = batimentoDAO.buscarBatimentosDoDeposito(deposito);
			if (batimentosDoDeposito.size() > 1){
				throw new InfraException("O arquivo de retorno possui um depósito vínculado a mais de um batimento! Não é possível removê-lo...");
			} else {
				deposito.setSituacaoDeposito(SituacaoDeposito.NAO_IDENTIFICADO);
				batimentoDAO.atualizarDeposito(deposito);
			}
		}
		retornoDao.removerBatimento(retorno, batimento);
	}
	
	public void gerarRetornos(Usuario usuarioAcao, List<Remessa> retornos){
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		this.tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.RETORNO);
		
		List<Arquivo> arquivosDeRetorno = new ArrayList<Arquivo>();
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa retorno: retornos){
			
			if (getArquivo() == null || !instituicaoDestino.equals(retorno.getInstituicaoDestino())){
				instituicaoDestino = retorno.getInstituicaoDestino();
				criarNovoArquivoDeRetorno(instituicaoDestino, retorno);
				
				List<Remessa> retornosDaInstituicao = retornoDao.buscarRetornosConfirmadosPorInstituicao(instituicaoDestino);
				getArquivo().setRemessas(retornosDaInstituicao);
				
				if (!arquivosDeRetorno.contains(getArquivo()) && getArquivo() != null) {
					arquivosDeRetorno.add(getArquivo());
				}
			} 
		}
		retornoDao.gerarRetornos(usuarioAcao ,arquivosDeRetorno);
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
