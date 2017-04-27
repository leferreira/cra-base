package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.entidade.vo.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
public class ConversorDesistenciaCancelamentoVO {

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
