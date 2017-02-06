package br.com.ieptbto.cra.dataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RemessaProvider extends DataProvider<Remessa> {

	/***/
	private static final long serialVersionUID = 1L;
	
	public RemessaProvider(List<Remessa> list) {
		super(list);
		this.filterState = new Remessa();
	}
	
	@Override
	public void setFilterState(Remessa remessa) {
		this.filterState = remessa; 
	}

	@Override
	public Remessa getFilterState() {
		return filterState;
	}
	
	@Override
	public Iterator<Remessa> iterator(long first, long count) {
		if (StringUtils.isBlank(genericFilter)) {
			 return this.objects.subList((int)first, (int)(first + count)).iterator();
		}
		List<Remessa> results = new ArrayList<Remessa>();
		for (Remessa r : this.objects) {
			if (StringUtils.isNotBlank(genericFilter)) {
				if (RemoverAcentosUtil.removeAcentos(r.getArquivo().getNomeArquivo()).toUpperCase().contains(genericFilter.toUpperCase())) {
					results.add(r);
				} 
			}
		}
		count = results.size();
		return results.subList((int)first, (int)(first + count)).iterator();
	}

	@Override
	public int compare(Remessa o1, Remessa o2) {
		// TODO Auto-generated method stub
		return 0;
	}
}