package br.com.ieptbto.cra.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.CabecalhoDAO;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class CabecalhoMediator {

	@Autowired
	private CabecalhoDAO cabecalhoDAO;

	/**
	 * Verificar se o sequencial do cabecalho é único para aquele banco
	 * 
	 * @param cabecalhoRemessa
	 * @return
	 */
	public boolean isSequencialValido(CabecalhoRemessa cabecalhoRemessa) {
		if (TipoArquivoEnum.REMESSA.equals(cabecalhoRemessa.getRemessa().getArquivo().getTipoArquivo().getTipoArquivo())) {
			return cabecalhoDAO.isSequencialUnicoPorBanco(cabecalhoRemessa);
		}
		return true;
	}

	public CabecalhoRemessa buscarUltimoCabecalhoRemessaPorMunicipio(String codigoPortador, String codigoMunicipio) {
		return cabecalhoDAO.buscarUltimoCabecalhoRemessaPorMunicipio(codigoPortador, codigoMunicipio);
	}

	public CabecalhoRemessa buscarUltimoCabecalhoRetornoPorMunicipio(CabecalhoRemessa cabecalhoRemessa) {
		return cabecalhoDAO.buscarUltimoCabecalhoRetornoPorMunicipio(cabecalhoRemessa);
	}

	public CabecalhoRemessa buscarUltimoCabecalhoRemessa(CabecalhoRemessa cabecalhoRemessa) {
		return cabecalhoDAO.buscarUltimoCabecalhoRemessa(cabecalhoRemessa);
	}
}
