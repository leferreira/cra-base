package br.com.ieptbto.cra.dataProvider;

import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LogCraProvider extends DataProvider<LogCra> {

	/***/
	private static final long serialVersionUID = 1L;
	
	public LogCraProvider(List<LogCra> list) {
		super(list);
		this.filterState = new LogCra();
	}
	
	@Override
	public void setFilterState(LogCra log) {
		this.filterState = log; 
	}

	@Override
	public LogCra getFilterState() {
		return filterState;
	}
	
	@Override
	public Iterator<LogCra> iterator(long first, long count) {
		if (StringUtils.isBlank(genericFilter)) {
			return this.objects.subList((int)first, (int)(first + count)).iterator();
		}
		List<LogCra> results = new ArrayList<LogCra>();
		for (LogCra log : this.objects) {
			if (StringUtils.isNotBlank(genericFilter)) {
				if (RemoverAcentosUtil.removeAcentos(log.getInstituicao()).toUpperCase().contains(RemoverAcentosUtil.removeAcentos(genericFilter).toUpperCase())) {
					results.add(log);
				} else if (RemoverAcentosUtil.removeAcentos(log.getDescricao()).toUpperCase().contains(RemoverAcentosUtil.removeAcentos(genericFilter).toUpperCase())) {
					results.add(log);
				} else if (RemoverAcentosUtil.removeAcentos(log.getTipoLog().getLabel()).toUpperCase().contains(RemoverAcentosUtil.removeAcentos(genericFilter).toUpperCase())) {
					results.add(log);
				} else if (RemoverAcentosUtil.removeAcentos(log.getAcao().getLabel()).toUpperCase().contains(RemoverAcentosUtil.removeAcentos(genericFilter).toUpperCase())) {
					results.add(log);
				} else if (DataUtil.localDateToString(log.getData()).contains(genericFilter.toUpperCase())) {
					results.add(log);
				}
			} 
		}
		count = results.size();
		return results.subList((int)first, (int)(first + count)).iterator();
	}

	@Override
	public int compare(LogCra o1, LogCra o2) {
		return 0;
	}
}