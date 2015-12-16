package br.com.ieptbto.cra.conversor;

import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.vo.ArquivoConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.ConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaCnp;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;

public class ConversorArquivoVO {

	public static List<RemessaCnp> converterParaRemessaVO(ArquivoVO arquivoRecebido) {
		List<RemessaCnp> remessas = new ArrayList<RemessaCnp>();
		List<CabecalhoVO> cabecalhosVO = arquivoRecebido.getCabecalhos();
		List<RodapeVO> rodapesVO = arquivoRecebido.getRodapes();
		List<TituloVO> titulosVO = arquivoRecebido.getTitulos();

		for (int i = 0; i < cabecalhosVO.size(); i++) {
			RemessaCnp remessa = new RemessaCnp();
			remessa.setTitulos(new ArrayList<TituloVO>());
			List<TituloVO> titulos = titulosVO.subList(0, Integer.parseInt(cabecalhosVO.get(i).getQtdTitulosRemessa()));
			remessa.setCabecalho(cabecalhosVO.get(i));
			remessa.setRodapes(rodapesVO.get(i));
			remessa.getTitulos().addAll(titulos);
			titulosVO.removeAll(titulosVO.subList(0, Integer.parseInt(cabecalhosVO.get(i).getQtdTitulosRemessa())));
			remessa.setIdentificacaoRegistro(arquivoRecebido.getIdentificacaoRegistro());
			remessa.setTipoArquivo(arquivoRecebido.getTipoArquivo());
			remessas.add(remessa);
		}

		return remessas;
	}

	public static ConfirmacaoVO converterParaRemessaVO(ArquivoConfirmacaoVO arquivoRecebido) {
		List<CabecalhoVO> cabecalhosVO = arquivoRecebido.getCabecalhos();
		List<RodapeVO> rodapesVO = arquivoRecebido.getRodapes();
		List<TituloVO> titulosVO = arquivoRecebido.getTitulos();

		for (int i = 0; i < cabecalhosVO.size();) {
			ConfirmacaoVO remessa = new ConfirmacaoVO();
			remessa.setTitulos(new ArrayList<TituloVO>());
			List<TituloVO> titulos = titulosVO.subList(0, Integer.parseInt(cabecalhosVO.get(i).getQtdTitulosRemessa()));
			remessa.setCabecalho(cabecalhosVO.get(i));
			remessa.setRodapes(rodapesVO.get(i));
			remessa.getTitulos().addAll(titulos);
			titulosVO.removeAll(titulosVO.subList(0, Integer.parseInt(cabecalhosVO.get(i).getQtdTitulosRemessa())));
			remessa.setIdentificacaoRegistro(arquivoRecebido.getIdentificacaoRegistro());
			remessa.setTipoArquivo(arquivoRecebido.getTipoArquivo());
			return remessa;
		}
		return null;
	}
}
