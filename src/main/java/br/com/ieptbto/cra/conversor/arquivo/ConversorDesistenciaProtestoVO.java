package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoCartorioDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.DesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RegistroDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeCartorioDesistenciaProtestoVO;

/**
 * 
 * @author Lefer
 *
 */
public class ConversorDesistenciaProtestoVO {

	public static RemessaDesistenciaProtestoVO converterParaRemessaVO(ArquivoDesistenciaProtestoVO arquivoRecebido) {
		RemessaDesistenciaProtestoVO remessa = new RemessaDesistenciaProtestoVO();
		CabecalhoArquivoDesistenciaProtestoVO cabecalhoArquivo = arquivoRecebido.getCabecalhoArquivo();
		List<CabecalhoCartorioDesistenciaProtestoVO> cabecalhoCartorio = arquivoRecebido.getCabecalhoCartorio();
		List<RegistroDesistenciaProtestoVO> desistenciaProtesto = arquivoRecebido.getRegistroDesistenciaProtesto();
		List<RodapeCartorioDesistenciaProtestoVO> rodapeCartorio = arquivoRecebido.getRodapeCartorio();
		RodapeArquivoDesistenciaProtestoVO rodapeArquivo = arquivoRecebido.getRodapeArquivo();

		remessa.setCabecalhoArquivo(cabecalhoArquivo);
		remessa.setRodapeArquivo(rodapeArquivo);
		remessa.setPedidoDesistencias(new ArrayList<DesistenciaProtestoVO>());
		remessa.setIdentificacaoRegistro(arquivoRecebido.getIdentificacaoRegistro());

		for (int i = 0; i < cabecalhoCartorio.size(); i++) {
			DesistenciaProtestoVO dp = new DesistenciaProtestoVO();
			dp.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			dp.setCabecalhoCartorio(cabecalhoCartorio.get(i));
			dp.setRodapeCartorio(rodapeCartorio.get(i));
			dp.getRegistroDesistenciaProtesto().addAll(
			        desistenciaProtesto.subList(0, Integer.parseInt(cabecalhoCartorio.get(i).getQuantidadeDesistencia())));
			desistenciaProtesto.removeAll(desistenciaProtesto.subList(0,
			        Integer.parseInt(cabecalhoCartorio.get(i).getQuantidadeDesistencia())));
			remessa.getPedidoDesistencias().add(dp);
		}
		return remessa;

	}

}
