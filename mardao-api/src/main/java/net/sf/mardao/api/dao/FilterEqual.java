package net.sf.mardao.api.dao;

import com.google.appengine.api.datastore.Query.FilterOperator;

public class FilterEqual extends Expression {

	public FilterEqual(String column, Object operand) {
		super(column, FilterOperator.EQUAL, operand);
	}

}
