package br.com.ieptbto.cra.mediator;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.ConversorArquivoCnpVO;
import br.com.ieptbto.cra.dao.CentralNancionalProtestoDAO;
import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoMediator {

	@Autowired
	private CentralNancionalProtestoDAO centralNancionalProtestoDAO;
	
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
}
