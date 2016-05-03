package br.com.ieptbto.cra.dataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * @author Thasso Araújo
 *
 */
public class DataProvider<T> extends SortableDataProvider<T, String> {

	/***/
	private static final long serialVersionUID = 1L;

	private List<T> objects;

	public DataProvider(List<T> list) {
		this.objects = list;
	}

	@Override
	public Iterator<? extends T> iterator(long first, long count) {
		return objects.iterator();
	}

	@Override
	public long size() {
		return getList().size();
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
		if (objects == null) {
			objects = new ArrayList<T>();
		}
		return objects;
	}
}
