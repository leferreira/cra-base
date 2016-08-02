package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TaxaCraDAO;
import br.com.ieptbto.cra.entidade.TaxaCra;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TaxaCraMediator extends BaseMediator {

	@Autowired
	TaxaCraDAO taxaCraDAO;

	public TaxaCra buscarTaxaCraVigente(LocalDate dataCancelamentoOuPagamento) {
		return taxaCraDAO.buscarTaxaCraVigente(dataCancelamentoOuPagamento);
	}

	public List<TaxaCra> buscarTaxasCra() {
		return taxaCraDAO.buscarTaxasCra();
	}

	public TaxaCra salvarTaxa(TaxaCra taxaCra, TaxaCra taxaCraAtual) {
		List<TaxaCra> taxasInicial = taxaCraDAO.buscarTaxasComDataInicialNoPeriodo(taxaCra);
		if (!taxasInicial.isEmpty()) {
			throw new InfraException("A data inicial contém vigências em conflito! Altere as vigências das taxas para que não exista conflitos!");
		}

		return taxaCraDAO.salvarTaxa(taxaCra, taxaCraAtual);
	}
}