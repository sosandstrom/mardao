package net.sf.mardao.core.filter;

/*
 * #%L
 * mardao-core
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

public class Filter {
    private final String column;
    private final FilterOperator operator;
    private final Object operand;

    private Filter(String column, FilterOperator operation, Object operand) {
        this.column = column;
        this.operator = operation;
        this.operand = operand;
    }

    /** Builds an EqualsFilter */
    public static Filter equalsFilter(String column, Object operand) {
      return new Filter(column, FilterOperator.EQUALS, operand);
    }

    /** Builds an InFilter */
    public static Filter inFilter(String column, Object operand) {
      return new Filter(column, FilterOperator.IN, operand);
    }

    public static Filter greaterThan(String column, Object operand) {
        return new Filter(column, FilterOperator.GREATER_THAN, operand);
    }

    public static Filter lessThan(String column, Object operand) {
        return new Filter(column, FilterOperator.LESS_THAN, operand);
    }

    public String toString() {
        return column + " " + operator + " " + operand;
    }

    public String getColumn() {
        return column;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public Object getOperand() {
        return operand;
    }

}
