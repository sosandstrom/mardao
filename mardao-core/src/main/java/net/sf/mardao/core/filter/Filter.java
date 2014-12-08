package net.sf.mardao.core.filter;

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
