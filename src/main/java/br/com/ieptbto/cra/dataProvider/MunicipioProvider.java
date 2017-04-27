package br.com.ieptbto.cra.dataProvider;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
public class MunicipioProvider extends DataProvider<Municipio> {

	/***/
	private static final long serialVersionUID = 1L;
	
	public MunicipioProvider(List<Municipio> list) {
		super(list);
		this.filterState = new Municipio();
		setSort("nomeMunicipio", SortOrder.ASCENDING);
	}
	
	@Override
	public void setFilterState(Municipio municipio) {
		this.filterState = municipio; 
	}

	@Override
	public Municipio getFilterState() {
		return filterState;
	}
	
	@Override
	public Iterator<Municipio> iterator(long first, long count) {
		if (StringUtils.isBlank(genericFilter)) {
			 return this.objects.subList((int)first, (int)(first + count)).iterator();
		}
		List<Municipio> results = new ArrayList<Municipio>();
		for (Municipio municipio : this.objects) {
			if (StringUtils.isNotBlank(genericFilter)) {
				if (RemoverAcentosUtil.removeAcentos(municipio.getNomeMunicipio()).toUpperCase().contains(genericFilter.toUpperCase())) {
					results.add(municipio);
				} else if (municipio.getUf().toUpperCase().contains(genericFilter.toUpperCase())) {
					results.add(municipio);
				} else if (municipio.getCodigoIBGE().toUpperCase().contains(genericFilter.toUpperCase())) {
					results.add(municipio);
				}
			}
		}
		count = results.size();
		return results.subList((int)first, (int)(first + count)).iterator();
	}

	@Override
	public int compare(Municipio o1, Municipio o2) {
		// TODO Auto-generated method stub
		return 0;
	}
}