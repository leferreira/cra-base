package br.com.ieptbto.cra.mediator;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.conversor.ConversorArquivoCnpVO;
import br.com.ieptbto.cra.dao.CentralNancionalProtestoDAO;
import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.RemessaCnp;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoMediator {

	protected static final Logger logger = Logger.getLogger(CentralNacionalProtestoMediator.class);

	@Autowired
	CentralNancionalProtestoDAO centralNancionalProtestoDAO;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ArquivoCnpVO gerarArquivoNacional() {
		ArquivoCnp arquivoCnp = new ArquivoCnp();
		arquivoCnp.setRemessaCnp(centralNancionalProtestoDAO.buscarRemessasCnpPendentes());
		centralNancionalProtestoDAO.salvarArquivoCnpNacional(arquivoCnp);

		ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
		arquivoCnpVO.setRemessasCnpVO(ConversorArquivoCnpVO.converterParaRemessaCnpVO(arquivoCnp.getRemessaCnp()));
		return arquivoCnpVO;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
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

	public boolean isArquivoJaDisponibilizadoConsultaPorData(LocalDate dataLiberacao) {
		RemessaCnp remessaCnp = centralNancionalProtestoDAO.isArquivoJaDisponibilizadoConsultaPorData(dataLiberacao);
		if (remessaCnp != null) {
			return true;
		}
		return false;
	}

	public ArquivoCnpVO buscarArquivoNacionalPorData(LocalDate dataLiberacao) {
		ArquivoCnp arquivoCnp = new ArquivoCnp();
		arquivoCnp.setRemessaCnp(centralNancionalProtestoDAO.buscarRemessasCnpPorData(dataLiberacao));
		centralNancionalProtestoDAO.salvarArquivoCnpNacional(arquivoCnp);

		ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
		arquivoCnpVO.setRemessasCnpVO(ConversorArquivoCnpVO.converterParaRemessaCnpVO(arquivoCnp.getRemessaCnp()));
		return arquivoCnpVO;
	}
}
