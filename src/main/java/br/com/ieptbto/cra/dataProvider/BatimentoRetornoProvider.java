package br.com.ieptbto.cra.dataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.entidade.ViewBatimentoRetorno;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
public class BatimentoRetornoProvider extends DataProvider<ViewBatimentoRetorno> {

	/***/
	private static final long serialVersionUID = 1L;
	
	public BatimentoRetornoProvider(List<ViewBatimentoRetorno> list) {
		super(list);
		this.filterState = new ViewBatimentoRetorno();
	}
	
	@Override
	public void setFilterState(ViewBatimentoRetorno batimento) {
		this.filterState = batimento; 
	}

	@Override
	public ViewBatimentoRetorno getFilterState() {
		return filterState;
	}
	
	@Override
	public Iterator<ViewBatimentoRetorno> iterator(long first, long count) {
		if (StringUtils.isBlank(genericFilter)) {
			return this.objects.subList((int)first, (int)(first + count)).iterator();
		}
		List<ViewBatimentoRetorno> results = new ArrayList<ViewBatimentoRetorno>();
		for (ViewBatimentoRetorno batimento : this.objects) {
			if (StringUtils.isNotBlank(genericFilter)) {
				if (RemoverAcentosUtil.removeAcentos(batimento.getNomeFantasia_Cartorio()).toUpperCase().contains(RemoverAcentosUtil.removeAcentos(genericFilter).toUpperCase())) {
					results.add(batimento);
				} else if (batimento.getNomeArquivo_Arquivo().toUpperCase().contains(genericFilter.toUpperCase())) {
					results.add(batimento);
				} else if (DataUtil.localDateToString(batimento.getDataEnvio_Arquivo()).contains(genericFilter.toUpperCase())) {
					results.add(batimento);
				} else if (batimento.getTotalValorlPagos().toString().contains(genericFilter.replace(".", "").replace(",", "."))) {
					results.add(batimento);
				} else if (batimento.getTotalCustasCartorio().toString().contains(genericFilter.replace(".", "").replace(",", "."))) {
					results.add(batimento);
				}
			}
		}
		count = results.size();
		return results.subList((int)first, (int)(first + count)).iterator();
	}

	@Override
	public int compare(ViewBatimentoRetorno o1, ViewBatimentoRetorno o2) {
		return 0;
	}
}
