package br.com.ieptbto.cra.dataProvider;

import br.com.ieptbto.cra.entidade.view.ViewTitulo;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
public class TituloProvider extends DataProvider<ViewTitulo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	public TituloProvider(List<ViewTitulo> list) {
		super(list);
		this.filterState = new ViewTitulo();
	}
	
	@Override
	public void setFilterState(ViewTitulo titulo) {
		this.filterState = titulo; 
	}

	@Override
	public ViewTitulo getFilterState() {
		return filterState;
	}
	
	@Override
	public Iterator<ViewTitulo> iterator(long first, long count) {
		if (StringUtils.isBlank(genericFilter)) {
			return this.objects.subList((int)first, (int)(first + count)).iterator();
		}
		List<ViewTitulo> results = new ArrayList<ViewTitulo>();
		for (ViewTitulo titulo : this.objects) {
			if (StringUtils.isNotBlank(genericFilter)) {
				if (RemoverAcentosUtil.removeAcentos(titulo.getNomeDevedor_TituloRemessa()).toUpperCase().contains(RemoverAcentosUtil.removeAcentos(genericFilter).toUpperCase())) {
					results.add(titulo);
				} else if (titulo.getNomeArquivo_Arquivo_Remessa().toUpperCase().contains(genericFilter.toUpperCase())) {
					results.add(titulo);
				} else if (titulo.getSaldoTitulo_TituloRemessa().toString().contains(genericFilter.replace(".", "").replace(",", "."))) {
					results.add(titulo);
				}
			}
		}
		count = results.size();
		return results.subList((int)first, (int)(first + count)).iterator();
	}

	@Override
	public int compare(ViewTitulo o1, ViewTitulo o2) {
		return 0;
	}

}
