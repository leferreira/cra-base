package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloFiliadoDAO;
import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamentoConvenio;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoTituloConvenio;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloFiliadoMediator {

	@Autowired
	TituloFiliadoDAO tituloFiliadoDAO;
	
	public TituloFiliado salvarTituloFiliado(TituloFiliado titulo){
		return tituloFiliadoDAO.salvar(titulo);
	}

	public TituloFiliado alterarTituloFiliado(TituloFiliado titulo) {
		return tituloFiliadoDAO.alterar(titulo);
	}

	public TituloRemessa buscarTituloDoConvenioNaCra(TituloFiliado tituloFiliado) {
		return tituloFiliadoDAO.buscarTituloDoConvenioNaCra(tituloFiliado);
	}
	
	public List<TituloFiliado> titulosParaEnvioAoConvenio(Filiado empresaFiliada) {
		return tituloFiliadoDAO.buscarTitulosParaEnvioAoConvenio(empresaFiliada);
	}

	public void removerTituloFiliado(TituloFiliado titulo) {
		tituloFiliadoDAO.removerTituloFiliado(titulo);
	}

	public void enviarTitulosPendentes(List<TituloFiliado> listaTitulosFiliado) {
		tituloFiliadoDAO.enviarTitulosPendentes(listaTitulosFiliado);
	}

	public List<TituloFiliado> consultarTitulosFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim, Municipio pracaProtesto, 
			TituloFiliado tituloBuscado, SituacaoTituloConvenio situacaoTituloAguardando) {
		return tituloFiliadoDAO.consultarTitulosFiliado(filiado, dataInicio, dataFim, pracaProtesto ,tituloBuscado, situacaoTituloAguardando);
	}

	public List<TituloFiliado> consultarTitulosConvenio(Instituicao convenio, LocalDate dataInicio, LocalDate dataFim, Filiado filiado, Municipio pracaProtesto, TituloFiliado tituloBuscado) {
		return tituloFiliadoDAO.consultarTitulosConvenio(convenio, dataInicio, dataFim,filiado, pracaProtesto, tituloBuscado);
	}

	public List<Avalista> buscarAvalistasPorTitulo(TituloFiliado titulo){
		return tituloFiliadoDAO.avalistasTituloFiliado(titulo);
	}
	
	public List<TituloFiliado> buscarTitulosParaRelatorioFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim, TipoRelatorio tipoRelatorio, 
			Municipio pracaProtesto) {
		return tituloFiliadoDAO.buscarTitulosParaRelatorioFiliado(filiado, dataInicio, dataFim, tipoRelatorio, pracaProtesto);
	}

	public List<TituloFiliado> buscarTitulosParaRelatorioConvenio(Instituicao convenio, Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
			Municipio pracaProtesto, TipoRelatorio tipoRelatorio) {
		return tituloFiliadoDAO.buscarTitulosParaRelatorioConvenio(convenio, filiado ,dataInicio, dataFim, pracaProtesto);
	}

	public int quatidadeTitulosPendentesEnvioFiliados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		return tituloFiliadoDAO.quatidadeTitulosPendentesEnvioFiliados(filiado, dataInicio, dataFim);
	}
	
	public int quatidadeTitulosEmProcessoFiliados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		return tituloFiliadoDAO.quatidadeTitulosEmProcessoFiliados(filiado, dataInicio, dataFim);
	}
	
	public int quatidadeTitulosFinalizadosFiliados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		return tituloFiliadoDAO.quatidadeTitulosFnalizados(filiado, dataInicio, dataFim);
	}

	public SolicitacaoDesistenciaCancelamentoConvenio enviarSolicitacaoDesistenciaCancelamento(SolicitacaoDesistenciaCancelamentoConvenio solicitacao) {
		return tituloFiliadoDAO.enviarSolicitacaoDesistenciaCancelamento(solicitacao);
	}
}
