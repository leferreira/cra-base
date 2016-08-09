package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
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
@Service
public class ConversorDesistenciaProtesto {

	public Arquivo converterParaArquivo(ArquivoDesistenciaProtestoVO arquivoVO, Arquivo arquivo, List<Exception> erros) {
		RemessaDesistenciaProtestoVO remessaVO = ConversorDesistenciaProtestoVO.converterParaRemessaVO(arquivoVO);
		RemessaDesistenciaProtesto remessa = new RemessaDesistenciaProtesto();

		CabecalhoArquivo cabecalho =
				new CabecalhoArquivoDesistenciaProtestoConversor().converter(CabecalhoArquivo.class, remessaVO.getCabecalhoArquivo());
		remessa.setCabecalho(cabecalho);
		remessa.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
		DesistenciaProtesto dp = null;
		for (DesistenciaProtestoVO pedidoDesistencia : remessaVO.getPedidoDesistencias()) {
			dp = new DesistenciaProtesto();
			dp.setDesistencias(new ArrayList<PedidoDesistencia>());
			dp.setCabecalhoCartorio(
					new CabecalhoCartorioDesistenciaProtestoConversor().converter(CabecalhoCartorio.class, pedidoDesistencia.getCabecalhoCartorio()));
			dp.setRodapeCartorio(
					new RodapeCartorioDesistenciaProtestoConversor().converter(RodapeCartorio.class, pedidoDesistencia.getRodapeCartorio()));
			for (RegistroDesistenciaProtestoVO registro : pedidoDesistencia.getRegistroDesistenciaProtesto()) {
				PedidoDesistencia registroPedido = new RegistroDesistenciaProtestoConversor().converter(PedidoDesistencia.class, registro);
				dp.getDesistencias().add(registroPedido);
				registroPedido.setDesistenciaProtesto(dp);
			}
			remessa.getDesistenciaProtesto().add(dp);
		}
		RodapeArquivo rodape = new RodapeArquivoDesistenciaProtestoVOConversor().converter(RodapeArquivo.class, remessaVO.getRodapeArquivo());
		remessa.setRodape(rodape);
		arquivo.getRemessaDesistenciaProtesto().setArquivo(arquivo);
		return arquivo;
	}

	public RemessaDesistenciaProtestoVO converterParaVO(RemessaDesistenciaProtesto remessaDesistenciaProtesto) {
		RemessaDesistenciaProtestoVO remessaVO = new RemessaDesistenciaProtestoVO();
		remessaVO.setPedidoDesistencias(new ArrayList<DesistenciaProtestoVO>());

		remessaVO.setCabecalhoArquivo(new CabecalhoArquivoDesistenciaProtestoConversor().converter(remessaDesistenciaProtesto.getCabecalho(),
				CabecalhoArquivoDesistenciaProtestoVO.class));

		remessaVO.setRodapeArquivo(new RodapeArquivoDesistenciaProtestoVOConversor().converter(remessaDesistenciaProtesto.getRodape(),
				RodapeArquivoDesistenciaProtestoVO.class));

		for (DesistenciaProtesto desistencia : remessaDesistenciaProtesto.getDesistenciaProtesto()) {
			DesistenciaProtestoVO desistenciaVO = new DesistenciaProtestoVO();
			desistenciaVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			desistenciaVO.setRegistroDesistenciaProtesto(new ArrayList<RegistroDesistenciaProtestoVO>());
			desistenciaVO.setCabecalhoCartorio(new CabecalhoCartorioDesistenciaProtestoConversor().converter(desistencia.getCabecalhoCartorio(),
					CabecalhoCartorioDesistenciaProtestoVO.class));

			for (PedidoDesistencia pedido : desistencia.getDesistencias()) {
				RegistroDesistenciaProtestoVO registro =
						new RegistroDesistenciaProtestoConversor().converter(pedido, RegistroDesistenciaProtestoVO.class);
				desistenciaVO.getRegistroDesistenciaProtesto().add(registro);
			}

			desistenciaVO.setRodapeCartorio(new RodapeCartorioDesistenciaProtestoConversor().converter(desistencia.getRodapeCartorio(),
					RodapeCartorioDesistenciaProtestoVO.class));
			remessaVO.getPedidoDesistencias().add(desistenciaVO);
		}

		return remessaVO;
	}

}
