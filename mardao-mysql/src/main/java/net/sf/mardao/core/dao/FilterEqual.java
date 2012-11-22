package net.sf.mardao.core.dao;

import net.sf.mardao.core.Filter;

public class FilterEqual extends Filter {

	public FilterEqual(String column, Object operand) {
		super(column, "=", operand);
	}

}
