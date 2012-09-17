package net.sf.mardao.core.dao;

import com.google.appengine.api.datastore.Query.FilterOperator;
import net.sf.mardao.core.Filter;

public class FilterEqual extends Filter {

	public FilterEqual(String column, Object operand) {
		super(column, FilterOperator.EQUAL, operand);
	}

}
