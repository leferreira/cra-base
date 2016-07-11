package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.AdministracaoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;

@Service
public class AdministracaoMediator extends BaseMediator {

	@Autowired
	AdministracaoDAO administracaoDAO;

	public List<Arquivo> buscarArquivosParaRemover(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
			ArrayList<TipoArquivoEnum> tiposArquivo) {
		return administracaoDAO.buscarArquivosRemover(arquivo, tiposArquivo, municipio, dataInicio, dataFim);
	}

	public void removerArquivo(Arquivo arquivo) {
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			administracaoDAO.removerRemessa(arquivo);
		}
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			if (arquivo.getInstituicaoEnvio().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				administracaoDAO.removerConfirmacaoCRA(arquivo);
			} else {
				administracaoDAO.removerConfirmacaoCartorio(arquivo);
			}
		}
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			if (arquivo.getInstituicaoEnvio().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				administracaoDAO.removerRetornoCRA(arquivo);
			} else {
				administracaoDAO.removerRetornoCartorio(arquivo);
			}
		}
	}
}