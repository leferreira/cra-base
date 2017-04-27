package br.com.ieptbto.cra.conversor.cnp;

import br.com.ieptbto.cra.entidade.LoteCnp;
import br.com.ieptbto.cra.entidade.RegistroCnp;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.entidade.vo.RemessaCnpVO;
import br.com.ieptbto.cra.entidade.vo.TituloCnpVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
public class ConversorCnpVO {

	public List<RegistroCnp> converterArquivoCnpVOParaRegistrosCnp(ArquivoCnpVO arquivoCnpVO) {
		List<RegistroCnp> registrosCnp = new ArrayList<RegistroCnp>();

		for (RemessaCnpVO remessaCnp : arquivoCnpVO.getRemessasCnpVO()) {
			for (TituloCnpVO tituloCnpVO : remessaCnp.getTitulosCnpVO()) {
				RegistroCnp registro = new RegistroCnpConversor().converter(RegistroCnp.class, tituloCnpVO);
				registrosCnp.add(registro);
			}
		}
		return registrosCnp;
	}

	public List<TituloCnpVO> converterRegistrosCnpParaTitulosCnpVO(LoteCnp lote, Integer sequencial) {
		List<TituloCnpVO> titulosCnpVO = new ArrayList<TituloCnpVO>();

		for (RegistroCnp registroCnp : lote.getRegistrosCnp()) {
			TituloCnpVO tituloVO = new RegistroCnpConversor().converter(registroCnp, TituloCnpVO.class);
			tituloVO.setSequenciaRegistro(Integer.toString(sequencial));
			titulosCnpVO.add(tituloVO);
			sequencial++;
		}
		return titulosCnpVO;
	}
}