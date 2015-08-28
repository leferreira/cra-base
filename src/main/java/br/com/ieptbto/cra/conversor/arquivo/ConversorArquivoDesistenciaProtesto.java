package br.com.ieptbto.cra.conversor.arquivo;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.ConversorArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
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
		RemessaDesistenciaProtesto remessa = new RemessaDesistenciaProtesto();
		RemessaDesistenciaProtestoVO remessaVo = ConversorArquivoDesistenciaProtestoVO.converterParaRemessaVO(arquivoVO);

		return remessa;
	}

}
