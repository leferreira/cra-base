package br.com.ieptbto.cra.mediator;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.ConversorArquivoCnpVO;
import br.com.ieptbto.cra.dao.CentralNancionalProtestoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoMediator {

	protected static final Logger logger = Logger.getLogger(CentralNacionalProtestoMediator.class);
	
	@Autowired
	private CentralNancionalProtestoDAO centralNancionalProtestoDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	
	public ArquivoCnpVO gerarArquivoNacional() {
		ArquivoCnp arquivoCnp = new ArquivoCnp();
		arquivoCnp.setRemessaCnp(centralNancionalProtestoDAO.buscarRemessasCnpPendentes()); 
		arquivoCnp.setInstituicaoEnvio(getCentralDeRemessasDeArquivos());
		arquivoCnp.setDataEnvio(new LocalDate());
		centralNancionalProtestoDAO.salvarArquivoCnpNacional(arquivoCnp);

		logger.info("ArquivoCNP Nacional sendo gerado!");
		ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
		arquivoCnpVO.setRemessasCnpVO(ConversorArquivoCnpVO.converterParaRemessaCnpVO(arquivoCnp));
		return arquivoCnpVO;
	}

	public ArquivoCnp processarArquivoCartorio(Usuario usuario, ArquivoCnpVO arquivoCnpVO) {
		ArquivoCnp arquivoCnp = new ArquivoCnp();
		arquivoCnp.setDataEnvio(new LocalDate());
		arquivoCnp.setInstituicaoEnvio(usuario.getInstituicao());
		arquivoCnp.setRemessaCnp(ConversorArquivoCnpVO.converterParaRemessaCnp(arquivoCnpVO));
		
		return centralNancionalProtestoDAO.salvarArquivoCartorioCentralNacionalProtesto(usuario, arquivoCnp);
	}

	public boolean isInstituicaoEnviouArquivoCnpHoje(Instituicao instituicao) {
		ArquivoCnp arquivoCnp = centralNancionalProtestoDAO.getArquivoCnpHojeInstituicao(instituicao);
		if (arquivoCnp != null) {
			return true;
		}
		return false;
	}
	
	private Instituicao getCentralDeRemessasDeArquivos() {
		return instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString());
	}
}
