package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.ConversorArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.PedidoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.DesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RegistroDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaDesistenciaProtestoVO;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ConversorArquivoDesistenciaProtesto {

	public Arquivo converter(ArquivoDesistenciaProtestoVO arquivoVO, List<Exception> erros) {
		Arquivo arquivo = new Arquivo();

		arquivo.setDataEnvio(new LocalDate());
		arquivo.setTipoArquivo(arquivoVO.getTipoArquivo());
		arquivo.setRemessaDesistenciaProtesto(getRemessaDesistenciaProtesto(arquivoVO));

		return arquivo;
	}

	private RemessaDesistenciaProtesto getRemessaDesistenciaProtesto(ArquivoDesistenciaProtestoVO arquivoVO) {
		DesistenciaProtesto desistenciaProtesto = new DesistenciaProtesto();
		RemessaDesistenciaProtesto remessa = new RemessaDesistenciaProtesto();
		RemessaDesistenciaProtestoVO remessaVo = ConversorArquivoDesistenciaProtestoVO.converterParaRemessaVO(arquivoVO);

		CabecalhoArquivo cabecalhoArquivo = new CabecalhoArquivoDesistenciaProtestoConversor().converter(CabecalhoArquivo.class,
		        remessaVo.getCabecalhoArquivo());
		remessa.setCabecalho(cabecalhoArquivo);

		RodapeArquivo rodapeArquivo = new RodapeArquivoDeistenciaProtestoVOConversor().converter(RodapeArquivo.class,
		        remessaVo.getRodapeArquivo());
		remessa.setRodape(rodapeArquivo);

		remessa.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
		for (DesistenciaProtestoVO pedidoDesistencia : remessaVo.getPedidoDesistencias()) {
			desistenciaProtesto = new DesistenciaProtesto();
			desistenciaProtesto.setDesistencias(new ArrayList<PedidoDesistenciaCancelamento>());
			desistenciaProtesto.setCabecalhoCartorio(new CabecalhoCartorioDesistenciaProtestoConversor().converter(CabecalhoCartorio.class,
			        pedidoDesistencia.getCabecalhoCartorio()));
			desistenciaProtesto.setRodapeCartorio(new RodapeCartorioDesistenciaProtestoConversor().converter(RodapeCartorio.class,
			        pedidoDesistencia.getRodapeCartorio()));
			for (RegistroDesistenciaProtestoVO registro : pedidoDesistencia.getRegistroDesistenciaProtesto()) {
				PedidoDesistenciaCancelamento registroPedido = new RegistroDesistenciaProtestoConversor().converter(
				        PedidoDesistenciaCancelamento.class, registro);
				desistenciaProtesto.getDesistencias().add(registroPedido);
				registroPedido.setDesistenciaProtesto(desistenciaProtesto);
			}
			remessa.getDesistenciaProtesto().add(desistenciaProtesto);
		}

		return remessa;
	}
}
