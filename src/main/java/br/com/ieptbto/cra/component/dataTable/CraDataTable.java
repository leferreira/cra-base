package br.com.ieptbto.cra.component.dataTable;

import br.com.ieptbto.cra.dataProvider.DataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
public class CraDataTable<T> extends Panel {

	private static final long serialVersionUID = 1L;
	private DataTable<T, String> dataTable;
	private List<IColumn<T, String>> columns;
	private DataProvider<T> provider;

	/**
	 * Cra DataTable with Jquery
	 * @param id
	 * @param columns
	 * @param provider
	 */
	public CraDataTable(String id, List<IColumn<T, String>> columns, DataProvider<T> provider) {
		super(id);
		this.provider = provider;
		this.columns = columns;
		add(dataTableJquery());
	}
	
	private DataTable<T, String> dataTableJquery() {
		int itensPerPage = 10;
		List<T> itens = provider.getList();
		if (itens != null && !itens.isEmpty() && itens.size() > 10) {
			itensPerPage = itens.size();
		}
		
		this.dataTable = new DefaultDataTable<T, String>("table", columns, provider, itensPerPage);
		this.dataTable.setMarkupId("table");
		this.dataTable.setOutputMarkupId(true);
        return dataTable;
	}
}