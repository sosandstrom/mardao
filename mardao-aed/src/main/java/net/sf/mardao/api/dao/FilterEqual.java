package net.sf.mardao.api.dao;

import com.google.appengine.api.datastore.Query.FilterOperator;
import net.sf.mardao.api.dao.Expression;

public class FilterEqual extends Expression {

	public FilterEqual(String column, Object operand) {
		super(column, FilterOperator.EQUAL, operand);
	}

}
