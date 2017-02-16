package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.conversor.arquivo.ConversorCancelamentoProtesto;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaCancelamento;
import br.com.ieptbto.cra.dao.AutorizacaoCancelamentoDAO;
import br.com.ieptbto.cra.dao.CancelamentoDAO;
import br.com.ieptbto.cra.dao.DesistenciaDAO;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.vo.RemessaDesistenciaProtestoVO;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class DesistenciaCancelamentoMediator extends BaseMediator {

	@Autowired
	DesistenciaDAO desistenciaDAO;
	@Autowired
	CancelamentoDAO cancelamentoDAO;
	@Autowired
	AutorizacaoCancelamentoDAO autorizacaoDAO;
	
	/**
	 * @param cartorio
	 * @param nomeArquivo
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public RemessaDesistenciaProtestoVO buscarDesistenciaCancelamentoCartorio(Instituicao cartorio, String nomeArquivo) {
		RemessaDesistenciaProtestoVO desistenciaCancelamentoVO = null;
		cartorio.setMunicipio(desistenciaDAO.buscarPorPK(cartorio.getMunicipio(), Municipio.class));

		if (nomeArquivo.contains(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.getConstante())) {
			DesistenciaProtesto desistencia = desistenciaDAO.buscarDesistenciaProtesto(cartorio, nomeArquivo);
			if (desistencia == null) {
				return null;
			}
			RemessaDesistenciaProtesto remessaDesistencia = new RemessaDesistenciaProtesto();
			remessaDesistencia.setCabecalho(desistencia.getRemessaDesistenciaProtesto().getCabecalho());
			remessaDesistencia.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
			remessaDesistencia.getDesistenciaProtesto().add(desistencia);
			remessaDesistencia.setRodape(desistencia.getRemessaDesistenciaProtesto().getRodape());
			remessaDesistencia.setArquivo(desistencia.getRemessaDesistenciaProtesto().getArquivo());
			return new ConversorDesistenciaCancelamento().converterParaVO(remessaDesistencia);

		} else if (nomeArquivo.contains(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.getConstante())) {
			CancelamentoProtesto cancelamento = cancelamentoDAO.buscarCancelamentoProtesto(cartorio, nomeArquivo);
			if (cancelamento == null) {
				return null;
			}
			RemessaCancelamentoProtesto remessa = new RemessaCancelamentoProtesto();
			remessa.setCabecalho(cancelamento.getRemessaCancelamentoProtesto().getCabecalho());
			remessa.setCancelamentoProtesto(new ArrayList<CancelamentoProtesto>());
			remessa.getCancelamentoProtesto().add(cancelamento);
			remessa.setRodape(cancelamento.getRemessaCancelamentoProtesto().getRodape());
			remessa.setArquivo(cancelamento.getRemessaCancelamentoProtesto().getArquivo());
			return new ConversorCancelamentoProtesto().converter(remessa);

		} else if (nomeArquivo.contains(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
			AutorizacaoCancelamento autorizacaoCancelamento = autorizacaoDAO.buscarAutorizacaoCancelamentoProtesto(cartorio, nomeArquivo);
			if (autorizacaoCancelamento == null) {
				return null;
			}
			RemessaAutorizacaoCancelamento remessa = new RemessaAutorizacaoCancelamento();
			remessa.setCabecalho(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getCabecalho());
			remessa.setAutorizacaoCancelamento(new ArrayList<AutorizacaoCancelamento>());
			remessa.getAutorizacaoCancelamento().add(autorizacaoCancelamento);
			remessa.setRodape(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getRodape());
			remessa.setArquivo(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo());
			return new ConversorCancelamentoProtesto().converter(remessa);

		}
		return desistenciaCancelamentoVO;
	}

	/**
	 * @param cartorio
	 * @param nomeArquivo
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void confirmarRecebimentoDesistenciaCancelamento(Instituicao instituicao, String nomeArquivo) {
		instituicao.setMunicipio(desistenciaDAO.buscarPorPK(instituicao.getMunicipio(), Municipio.class));
		if (nomeArquivo.contains(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.getConstante())) {
			DesistenciaProtesto dp = desistenciaDAO.buscarDesistenciaProtesto(instituicao, nomeArquivo);
			desistenciaDAO.alterarSituacaoDesistenciaProtesto(dp, true);
		} else if (nomeArquivo.contains(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.getConstante())) {
			CancelamentoProtesto cp = cancelamentoDAO.buscarCancelamentoProtesto(instituicao, nomeArquivo);
			cancelamentoDAO.alterarSituacaoCancelamentoProtesto(cp, true);
		} else if (nomeArquivo.contains(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
			AutorizacaoCancelamento ac = autorizacaoDAO.buscarAutorizacaoCancelamentoProtesto(instituicao, nomeArquivo);
			autorizacaoDAO.alterarSituacaoAutorizacaoCancelamento(ac, true);
		}
	}
}
