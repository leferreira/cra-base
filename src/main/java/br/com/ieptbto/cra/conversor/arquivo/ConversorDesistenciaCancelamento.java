package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ConversorDesistenciaCancelamento {

	public Arquivo converterParaArquivo(ArquivoDesistenciaProtestoVO arquivoVO, Arquivo arquivo, List<Exception> erros) {
		RemessaDesistenciaProtestoVO remessaVO = ConversorDesistenciaCancelamentoVO.converterParaRemessaVO(arquivoVO);
		RemessaDesistenciaProtesto remessa = new RemessaDesistenciaProtesto();

		CabecalhoArquivo cabecalho = new ConversorCabecalhoArquivoDesistenciaCancelamento().converter(CabecalhoArquivo.class, remessaVO.getCabecalhoArquivo());
		remessa.setCabecalho(cabecalho);
		remessa.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
		DesistenciaProtesto dp = null;
		for (DesistenciaProtestoVO pedidoDesistencia : remessaVO.getPedidoDesistencias()) {
			dp = new DesistenciaProtesto();
			dp.setDesistencias(new ArrayList<PedidoDesistencia>());
			dp.setCabecalhoCartorio(
					new ConversorCabecalhoCartorioDesistenciaCancelamento().converter(CabecalhoCartorio.class, pedidoDesistencia.getCabecalhoCartorio()));
			dp.setRodapeCartorio(new ConversorRodapeCartorioDesistenciaCancelamento().converter(RodapeCartorio.class, pedidoDesistencia.getRodapeCartorio()));
			for (RegistroDesistenciaProtestoVO registro : pedidoDesistencia.getRegistroDesistenciaProtesto()) {
				PedidoDesistencia registroPedido = new ConversorRegistroDesistenciaProtesto().converter(PedidoDesistencia.class, registro);
				dp.getDesistencias().add(registroPedido);
				registroPedido.setDesistenciaProtesto(dp);
			}
			remessa.getDesistenciaProtesto().add(dp);
		}
		RodapeArquivo rodape = new ConversorRodapeArquivoDesistenciaCancelamento().converter(RodapeArquivo.class, remessaVO.getRodapeArquivo());
		remessa.setRodape(rodape);
		arquivo.setRemessaDesistenciaProtesto(remessa);
		arquivo.getRemessaDesistenciaProtesto().setArquivo(arquivo);
		return arquivo;
	}

	public RemessaDesistenciaProtestoVO converterParaVO(RemessaDesistenciaProtesto remessaDesistenciaProtesto) {
		RemessaDesistenciaProtestoVO remessaVO = new RemessaDesistenciaProtestoVO();
		remessaVO.setPedidoDesistencias(new ArrayList<DesistenciaProtestoVO>());

		remessaVO.setCabecalhoArquivo(new ConversorCabecalhoArquivoDesistenciaCancelamento().converter(remessaDesistenciaProtesto.getCabecalho(),
				CabecalhoArquivoDesistenciaProtestoVO.class));

		remessaVO.setRodapeArquivo(
				new ConversorRodapeArquivoDesistenciaCancelamento().converter(remessaDesistenciaProtesto.getRodape(), RodapeArquivoDesistenciaProtestoVO.class));

		for (DesistenciaProtesto desistencia : remessaDesistenciaProtesto.getDesistenciaProtesto()) {
			DesistenciaProtestoVO desistenciaVO = new DesistenciaProtestoVO();
			desistenciaVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			desistenciaVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			desistenciaVO.setCabecalhoCartorio(new ConversorCabecalhoCartorioDesistenciaCancelamento().converter(desistencia.getCabecalhoCartorio(),
					CabecalhoCartorioDesistenciaProtestoVO.class));

			for (PedidoDesistencia pedido : desistencia.getDesistencias()) {
				RegistroDesistenciaProtestoVO registro = new ConversorRegistroDesistenciaProtesto().converter(pedido, RegistroDesistenciaProtestoVO.class);
				desistenciaVO.getRegistroDesistenciaProtesto().add(registro);
			}

			desistenciaVO.setRodapeCartorio(
					new ConversorRodapeCartorioDesistenciaCancelamento().converter(desistencia.getRodapeCartorio(), RodapeCartorioDesistenciaProtestoVO.class));
			remessaVO.getPedidoDesistencias().add(desistenciaVO);
		}

		return remessaVO;
	}

	public RemessaDesistenciaProtestoVO converter(RemessaDesistenciaProtesto remessaDesistenciaProtesto) {
		RemessaDesistenciaProtestoVO remessaVO = new RemessaDesistenciaProtestoVO();
		remessaVO.setPedidoDesistencias(new ArrayList<DesistenciaProtestoVO>());

		remessaVO.setCabecalhoArquivo(new ConversorCabecalhoArquivoDesistenciaCancelamento().converter(remessaDesistenciaProtesto.getCabecalho(),
				CabecalhoArquivoDesistenciaProtestoVO.class));

		remessaVO.setRodapeArquivo(
				new ConversorRodapeArquivoDesistenciaCancelamento().converter(remessaDesistenciaProtesto.getRodape(), RodapeArquivoDesistenciaProtestoVO.class));

		for (DesistenciaProtesto desistencia : remessaDesistenciaProtesto.getDesistenciaProtesto()) {
			DesistenciaProtestoVO desistenciaVO = new DesistenciaProtestoVO();
			desistenciaVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			desistenciaVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			desistenciaVO.setCabecalhoCartorio(new ConversorCabecalhoCartorioDesistenciaCancelamento().converter(desistencia.getCabecalhoCartorio(),
					CabecalhoCartorioDesistenciaProtestoVO.class));

			for (PedidoDesistencia pedido : desistencia.getDesistencias()) {
				RegistroDesistenciaProtestoVO registro = new ConversorRegistroDesistenciaProtesto().converter(pedido, RegistroDesistenciaProtestoVO.class);
				desistenciaVO.getRegistroDesistenciaProtesto().add(registro);
			}

			desistenciaVO.setRodapeCartorio(
					new ConversorRodapeCartorioDesistenciaCancelamento().converter(desistencia.getRodapeCartorio(), RodapeCartorioDesistenciaProtestoVO.class));
			remessaVO.getPedidoDesistencias().add(desistenciaVO);
		}
		return remessaVO;
	}

}
