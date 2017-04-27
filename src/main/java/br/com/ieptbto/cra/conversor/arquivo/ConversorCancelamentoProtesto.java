package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ConversorCancelamentoProtesto {

	public RemessaDesistenciaProtestoVO converter(RemessaCancelamentoProtesto remessaCancelamentoProtesto) {
		RemessaDesistenciaProtestoVO remessaVO = new RemessaDesistenciaProtestoVO();
		remessaVO.setPedidoDesistencias(new ArrayList<DesistenciaProtestoVO>());

		remessaVO.setCabecalhoArquivo(new ConversorCabecalhoArquivoDesistenciaCancelamento().converter(remessaCancelamentoProtesto.getCabecalho(), CabecalhoArquivoDesistenciaProtestoVO.class));
		remessaVO.setRodapeArquivo(new ConversorRodapeArquivoDesistenciaCancelamento().converter(remessaCancelamentoProtesto.getRodape(),
		        RodapeArquivoDesistenciaProtestoVO.class));

		for (CancelamentoProtesto cancelamento : remessaCancelamentoProtesto.getCancelamentoProtesto()) {
			DesistenciaProtestoVO cancelamentoVO = new DesistenciaProtestoVO();
			cancelamentoVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			cancelamentoVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			cancelamentoVO.setCabecalhoCartorio(new ConversorCabecalhoCartorioDesistenciaCancelamento().converter(
			        cancelamento.getCabecalhoCartorio(), CabecalhoCartorioDesistenciaProtestoVO.class));

			for (PedidoCancelamento pedido : cancelamento.getCancelamentos()) {
				RegistroDesistenciaProtestoVO registro = new ConversorRegistroCancelamentoProtesto().converter(pedido, RegistroDesistenciaProtestoVO.class);
				registro.setSolicitacaoCancelamentoSustacao("C");
				cancelamentoVO.getRegistroDesistenciaProtesto().add(registro);
			}

			cancelamentoVO.setRodapeCartorio(new ConversorRodapeCartorioDesistenciaCancelamento().converter(cancelamento.getRodapeCartorio(),
			        RodapeCartorioDesistenciaProtestoVO.class));
			cancelamentoVO.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(Integer.toString(cancelamentoVO.getRegistroDesistenciaProtesto().size() * 2));
			remessaVO.getPedidoDesistencias().add(cancelamentoVO);
		}
		return remessaVO;
	}
	
	public RemessaDesistenciaProtestoVO converter(RemessaAutorizacaoCancelamento remessaAutorizacaoCancelamento) {
		RemessaDesistenciaProtestoVO remessaVO = new RemessaDesistenciaProtestoVO();
		remessaVO.setPedidoDesistencias(new ArrayList<DesistenciaProtestoVO>());

		remessaVO.setCabecalhoArquivo(new ConversorCabecalhoArquivoDesistenciaCancelamento().converter(remessaAutorizacaoCancelamento.getCabecalho(), CabecalhoArquivoDesistenciaProtestoVO.class));

		remessaVO.setRodapeArquivo(new ConversorRodapeArquivoDesistenciaCancelamento().converter(remessaAutorizacaoCancelamento.getRodape(),
		        RodapeArquivoDesistenciaProtestoVO.class));

		for (AutorizacaoCancelamento ac : remessaAutorizacaoCancelamento.getAutorizacaoCancelamento()) {
			DesistenciaProtestoVO acVO = new DesistenciaProtestoVO();
			acVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			acVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			acVO.setCabecalhoCartorio(new ConversorCabecalhoCartorioDesistenciaCancelamento().converter(
			        ac.getCabecalhoCartorio(), CabecalhoCartorioDesistenciaProtestoVO.class));

			for (PedidoAutorizacaoCancelamento pedido : ac.getAutorizacoesCancelamentos()) {
				RegistroDesistenciaProtestoVO registro = new ConversorRegistroAutorizacaoCancelamento().converter(pedido, RegistroDesistenciaProtestoVO.class);
				registro.setSolicitacaoCancelamentoSustacao("S");
				acVO.getRegistroDesistenciaProtesto().add(registro);
			}

			acVO.setRodapeCartorio(new ConversorRodapeCartorioDesistenciaCancelamento().converter(ac.getRodapeCartorio(),
			        RodapeCartorioDesistenciaProtestoVO.class));
			acVO.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(Integer.toString(acVO.getRegistroDesistenciaProtesto().size() * 2));
			remessaVO.getPedidoDesistencias().add(acVO);
		}
		return remessaVO;
	}
}
