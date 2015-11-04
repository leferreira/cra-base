package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
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
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	RetornoDAO retornoDao;
	
	private Instituicao cra;
	private TipoArquivo tipoArquivo;
	private Arquivo arquivo;
	
	public List<Remessa> buscarRetornosParaBatimento(){
		return retornoDao.buscarRetornosParaBatimento();
	}
	
	public List<Remessa> buscarRetornosConfirmados(){
		return retornoDao.buscarRetornosConfirmados();
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		if (!retorno.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)){
			throw new InfraException("O arquivo não é um arquivo de RETORNO válido.");
		} 
		return retornoDao.buscarValorDeTitulosPagos(retorno);
	}
	
	public BigDecimal buscarValorDeCustasCartorio(Remessa retorno){
		if (!retorno.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)){
			throw new InfraException("O arquivo não é um arquivo de RETORNO válido.");
		} 
		return retornoDao.buscarValorDeCustasCartorio(retorno);
	}
	
	public void confirmarBatimentos(List<Remessa> retornos){
		retornoDao.confirmarBatimento(retornos);
	}
	
	public void removerConfirmado(Remessa retorno){
		retornoDao.removerConfirmado(retorno);
	}
	
	public void gerarRetornos(Usuario usuarioAcao, List<Remessa> retornos){
		List<Arquivo> arquivosDeRetorno = new ArrayList<Arquivo>();
		
		setCra(instituicaoDAO.buscarInstituicaoInicial("CRA"));
		setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.RETORNO));
		
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa retorno: retornos){
			
			if (getArquivo() == null || !instituicaoDestino.equals(retorno.getInstituicaoDestino())){
				instituicaoDestino = retorno.getInstituicaoDestino();
				criarNovoArquivoDeRetorno(instituicaoDestino, retorno);
				
				List<Remessa> retornosDaInstituicao = retornoDao.buscarRetornosConfirmadosPorInstituicao(instituicaoDestino);
				getArquivo().setRemessas(retornosDaInstituicao);
				
				if (!arquivosDeRetorno.contains(getArquivo()) && getArquivo() != null)
					arquivosDeRetorno.add(getArquivo());
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

	public void setCra(Instituicao cra) {
		this.cra = cra;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}
}
