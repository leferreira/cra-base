package br.com.ieptbto.cra.dataProvider;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
public abstract class DataProvider<T> extends SortableDataProvider<T, String> implements IFilterStateLocator<T>, Comparator<T>, Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	protected String genericFilter;
	protected T filterState;
	protected List<T> objects;

	public DataProvider(List<T> objects) {
		this.objects = objects;
	}

	@Override
	public abstract Iterator<? extends T> iterator(long first, long count);
	
	@Override
	public abstract T getFilterState();
	
	@Override
	public abstract void setFilterState(T state);
	
	@Override
	public long size() {
		return this.objects.size();
	}

	@Override
	public IModel<T> model(final T object) {
		return new AbstractReadOnlyModel<T>() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public T getObject() {
				return object;
			}
		};
	}
	
	public List<T> getList() {
		return this.objects;
	}
}