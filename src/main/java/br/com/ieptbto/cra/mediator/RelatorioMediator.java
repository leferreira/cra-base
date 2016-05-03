package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.RelatorioDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RelatorioMediator {

	@Autowired
	RelatorioDAO relatorioDAO;

	public List<TituloRemessa> relatorioTitulosPorSituacao(SituacaoTituloRelatorio situacaoTitulo, Instituicao instituicao,
			Instituicao cartorio, LocalDate dataInicio, LocalDate dataFim) {

		if (situacaoTitulo.equals(SituacaoTituloRelatorio.GERAL)) {
			return relatorioDAO.relatorioTitulosGeral(dataInicio, dataFim, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.SEM_CONFIRMACAO)) {
			return relatorioDAO.relatorioTitulosSemConfirmacao(dataInicio, dataFim, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.COM_CONFIRMACAO)) {
			return relatorioDAO.relatorioTitulosConfirmadosSemRetorno(dataInicio, dataFim, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.COM_RETORNO)) {
			return relatorioDAO.relatorioTitulosRetorno(dataInicio, dataFim, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.PAGOS)) {
			return relatorioDAO.relatorioTitulosPagos(dataInicio, dataFim, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.PROTESTADOS)) {
			return relatorioDAO.relatorioTitulosProtestados(dataInicio, dataFim, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS)) {
			return relatorioDAO.relatorioTitulosRetiradosDevolvidos(dataInicio, dataFim, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.DESISTÊNCIA_DE_PROTESTO)) {
			return relatorioDAO.relatorioTitulosDesistenciaProtesto(dataInicio, dataFim, instituicao, cartorio);

		} else if (situacaoTitulo.equals(SituacaoTituloRelatorio.AUTORIZACAO_CANCELAMENTO)) {
			return relatorioDAO.relatorioTitulosAutorizacaoCancelamento(dataInicio, dataFim, instituicao, cartorio);
		}
		return null;
	}

}
