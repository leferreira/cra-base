package br.com.ieptbto.cra.dataProvider;

import br.com.ieptbto.cra.beans.TituloCsvBean;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
public class TituloCsvProvider extends DataProvider<TituloCsvBean> {

	/***/
	private static final long serialVersionUID = 1L;
	
	public TituloCsvProvider(List<TituloCsvBean> list) {
		super(list);
		this.filterState = new TituloCsvBean();
	}
	
	@Override
	public void setFilterState(TituloCsvBean titulo) {
		this.filterState = titulo; 
	}

	@Override
	public TituloCsvBean getFilterState() {
		return filterState;
	}
	
	@Override
	public Iterator<TituloCsvBean> iterator(long first, long count) {
		if (StringUtils.isBlank(genericFilter)) {
			return this.objects.subList((int)first, (int)(first + count)).iterator();
		}
		List<TituloCsvBean> results = new ArrayList<TituloCsvBean>();
		for (TituloCsvBean titulo : this.objects) {
			if (StringUtils.isNotBlank(genericFilter)) {
				if (RemoverAcentosUtil.removeAcentos(titulo.getNomeDevedor()).toUpperCase().contains(RemoverAcentosUtil.removeAcentos(genericFilter).toUpperCase())) {
					results.add(titulo);
				} else if (titulo.getRemessa().toUpperCase().contains(genericFilter.toUpperCase())) {
					results.add(titulo);
				}
			}
		}
		count = results.size();
		return results.subList((int)first, (int)(first + count)).iterator();
	}

	@Override
	public int compare(TituloCsvBean o1, TituloCsvBean o2) {
		return 0;
	}
}
