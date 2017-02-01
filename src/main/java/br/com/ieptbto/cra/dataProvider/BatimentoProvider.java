package br.com.ieptbto.cra.dataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.entidade.ViewBatimento;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
public class BatimentoProvider extends DataProvider<ViewBatimento>{

	/***/
	private static final long serialVersionUID = 1L;
	
	public BatimentoProvider(List<ViewBatimento> list) {
		super(list);
		this.filterState = new ViewBatimento();
	}
	
	@Override
	public void setFilterState(ViewBatimento batimento) {
		this.filterState = batimento; 
	}

	@Override
	public ViewBatimento getFilterState() {
		return filterState;
	}
	
	@Override
	public Iterator<ViewBatimento> iterator(long first, long count) {
		if (StringUtils.isBlank(genericFilter)) {
			 return this.objects.subList((int)first, (int)(first + count)).iterator();
		}
		List<ViewBatimento> results = new ArrayList<ViewBatimento>();
		for (ViewBatimento batimento : this.objects) {
			if (StringUtils.isNotBlank(genericFilter)) {
				if (RemoverAcentosUtil.removeAcentos(batimento.getNomeFantasia_Cartorio()).toUpperCase().contains(genericFilter.toUpperCase())) {
					results.add(batimento);
				} 
			}
		}
		count = results.size();
		return results.subList((int)first, (int)(first + count)).iterator();
	}
}
