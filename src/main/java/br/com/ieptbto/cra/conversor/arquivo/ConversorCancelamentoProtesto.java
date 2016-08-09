package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.vo.CabecalhoArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoCartorioDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.DesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RegistroDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeCartorioDesistenciaProtestoVO;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ConversorCancelamentoProtesto {

	public RemessaDesistenciaProtestoVO converter(RemessaCancelamentoProtesto remessaCancelamentoProtesto) {
		RemessaDesistenciaProtestoVO remessaVO = new RemessaDesistenciaProtestoVO();
		remessaVO.setPedidoDesistencias(new ArrayList<DesistenciaProtestoVO>());

		remessaVO.setCabecalhoArquivo(new CabecalhoArquivoDesistenciaProtestoConversor().converter(remessaCancelamentoProtesto.getCabecalho(), CabecalhoArquivoDesistenciaProtestoVO.class));

		remessaVO.setRodapeArquivo(new RodapeArquivoDesistenciaProtestoVOConversor().converter(remessaCancelamentoProtesto.getRodape(),
		        RodapeArquivoDesistenciaProtestoVO.class));

		for (CancelamentoProtesto cancelamento : remessaCancelamentoProtesto.getCancelamentoProtesto()) {
			DesistenciaProtestoVO cancelamentoVO = new DesistenciaProtestoVO();
			cancelamentoVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			cancelamentoVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			cancelamentoVO.setCabecalhoCartorio(new CabecalhoCartorioDesistenciaProtestoConversor().converter(
			        cancelamento.getCabecalhoCartorio(), CabecalhoCartorioDesistenciaProtestoVO.class));

			for (PedidoCancelamento pedido : cancelamento.getCancelamentos()) {
				RegistroDesistenciaProtestoVO registro = new RegistroCancelamentoProtestoConversor().converter(pedido, RegistroDesistenciaProtestoVO.class);
				registro.setSolicitacaoCancelamentoSustacao("C");
				cancelamentoVO.getRegistroDesistenciaProtesto().add(registro);
			}

			cancelamentoVO.setRodapeCartorio(new RodapeCartorioDesistenciaProtestoConversor().converter(cancelamento.getRodapeCartorio(),
			        RodapeCartorioDesistenciaProtestoVO.class));
			remessaVO.getPedidoDesistencias().add(cancelamentoVO);
		}
		return remessaVO;
	}
	
	public RemessaDesistenciaProtestoVO converter(RemessaAutorizacaoCancelamento remessaAutorizacaoCancelamento) {
		RemessaDesistenciaProtestoVO remessaVO = new RemessaDesistenciaProtestoVO();
		remessaVO.setPedidoDesistencias(new ArrayList<DesistenciaProtestoVO>());

		remessaVO.setCabecalhoArquivo(new CabecalhoArquivoDesistenciaProtestoConversor().converter(remessaAutorizacaoCancelamento.getCabecalho(), CabecalhoArquivoDesistenciaProtestoVO.class));

		remessaVO.setRodapeArquivo(new RodapeArquivoDesistenciaProtestoVOConversor().converter(remessaAutorizacaoCancelamento.getRodape(),
		        RodapeArquivoDesistenciaProtestoVO.class));

		for (AutorizacaoCancelamento ac : remessaAutorizacaoCancelamento.getAutorizacaoCancelamento()) {
			DesistenciaProtestoVO acVO = new DesistenciaProtestoVO();
			acVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			acVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			acVO.setCabecalhoCartorio(new CabecalhoCartorioDesistenciaProtestoConversor().converter(
			        ac.getCabecalhoCartorio(), CabecalhoCartorioDesistenciaProtestoVO.class));

			for (PedidoAutorizacaoCancelamento pedido : ac.getAutorizacoesCancelamentos()) {
				RegistroDesistenciaProtestoVO registro = new RegistroAutorizacaoCancelamentoConversor().converter(pedido, RegistroDesistenciaProtestoVO.class);
				registro.setSolicitacaoCancelamentoSustacao("S");
				acVO.getRegistroDesistenciaProtesto().add(registro);
			}

			acVO.setRodapeCartorio(new RodapeCartorioDesistenciaProtestoConversor().converter(ac.getRodapeCartorio(),
			        RodapeCartorioDesistenciaProtestoVO.class));
			remessaVO.getPedidoDesistencias().add(acVO);
		}
		return remessaVO;
	}
}
