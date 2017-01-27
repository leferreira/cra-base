package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;

@Service
public class BatimentoMediator extends BaseMediator {

	@Autowired
	BatimentoDAO batimentoDAO;
	@Autowired
	RetornoDAO retornoDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	
	/**
	 * @return
	 */
	public List<Remessa> buscarRetornosParaBatimento() {
		return batimentoDAO.buscarRetornosParaBatimento();
	}

	/**
	 * @param instiuicao
	 * @param dataBatimento
	 * @param dataComoDataLimite
	 * @return
	 */
	public List<Remessa> buscarRetornosAguardandoLiberacao(Instituicao instiuicao, LocalDate dataBatimento, boolean dataComoDataLimite) {
		return batimentoDAO.buscarRetornosAguardandoLiberacao(instiuicao, dataBatimento, dataComoDataLimite);
	}

	/**
	 * @param dataBatimento
	 * @return
	 */
	public List<Remessa> buscarRetornosParaPagamentoInstituicao(LocalDate dataBatimento) {
		return batimentoDAO.buscarRetornosParaPagamentoInstituicao(dataBatimento);
	}

	/**
	 * @param retornos
	 * @return
	 */
	public List<Remessa> salvarBatimentos(List<Remessa> retornos) {
		Instituicao cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		Boolean arquivoRetornoGeradoHoje = retornoDAO.verificarArquivoRetornoGeradoCra(cra);
		for (Remessa retorno : retornos) {
			Batimento batimento = new Batimento();
			batimento.setData(aplicarRegraDataBatimento(arquivoRetornoGeradoHoje));
			batimento.setDataBatimento(new LocalDateTime());
			batimento.setRemessa(retorno);
			batimento.setDepositosBatimento(new ArrayList<BatimentoDeposito>());

			for (Deposito depositosIdentificado : retorno.getListaDepositos()) {
				BatimentoDeposito depositosBatimento = new BatimentoDeposito();
				depositosBatimento.setBatimento(batimento);
				depositosBatimento.setDeposito(depositosIdentificado);

				batimento.getDepositosBatimento().add(depositosBatimento);
			}
			batimentoDAO.salvarBatimento(batimento);
		}
		return retornos;
	}

	/**
	 * Aplicando a regra de data do batimento
	 * 
	 * @param arquivoRetornoGeradoHoje
	 * @return
	 */
	public LocalDate aplicarRegraDataBatimento(Boolean arquivoRetornoGeradoHoje) {
		if (arquivoRetornoGeradoHoje.equals(false)) {
			return new LocalDate();
		} else if (arquivoRetornoGeradoHoje.equals(true)) {
			Integer contadorDeDias = 1;
			while (true) {
				LocalDate proximoDiaUtil = new LocalDate().plusDays(contadorDeDias);
				Date date = proximoDiaUtil.toDate();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					return proximoDiaUtil;
				}
				contadorDeDias++;
			}
		}
		return null;
	}

	/**
	 * Buscar Depositos vínculados a um batimento
	 * @param batimento
	 * @return
	 */
	public List<Deposito> buscarDepositosPorBatimento(Batimento batimento) {
		return batimentoDAO.buscarDepositosPorBatimento(batimento);
	}
}