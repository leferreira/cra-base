package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.vo.ArquivoRemessaVO;
import br.com.ieptbto.cra.entidade.vo.ConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaComarcaVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RetornoVO;

public class ConversorArquivoVO {

	public static List<RemessaVO> conversorParaArquivoRemessa(ArquivoRemessaVO arquivoRemessaVO) {
		List<RemessaVO> remessas = new ArrayList<RemessaVO>();

		for (RemessaComarcaVO remessaComarca : arquivoRemessaVO.getRemessasComarcas()) {
			RemessaVO remessaVO = new RemessaVO();
			remessaVO.setCabecalho(remessaComarca.getCabecalho());
			remessaVO.setTitulos(remessaComarca.getTitulos());
			remessaVO.setRodapes(remessaComarca.getRodape());
			remessas.add(remessaVO);
		}
		return remessas;
	}

	public static RemessaVO conversorParaArquivoConfirmacao(ConfirmacaoVO confirmacaoVO) {
		RemessaVO remessaVO = new RemessaVO();
		remessaVO.setCabecalho(confirmacaoVO.getCabecalho());
		remessaVO.setTitulos(confirmacaoVO.getTitulos());
		remessaVO.setRodapes(confirmacaoVO.getRodape());
		return remessaVO;
	}

	public static RemessaVO conversorParaArquivoRetorno(RetornoVO retornoVO) {
		RemessaVO remessaVO = new RemessaVO();
		remessaVO.setCabecalho(retornoVO.getCabecalho());
		remessaVO.setTitulos(retornoVO.getTitulos());
		remessaVO.setRodapes(retornoVO.getRodape());
		return remessaVO;
	}
}