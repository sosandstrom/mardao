package net.sf.mardao.core;

import java.io.Serializable;

public class Filter {
    private final String column;
    private final Object operation;
    private final Object operand;

    public Filter(String column, Object operation, Object operand) {
        this.column = column;
        this.operation = operation;
        this.operand = operand;
    }

    public String toString() {
        return column + operation + operand;
    }

    public String getColumn() {
        return column;
    }

    public Object getOperation() {
        return operation;
    }

    public Object getOperand() {
        return operand;
    }

    public String getToken(String key) {
        return ":" + key;
    }

    public static class IN extends Filter {
        public IN(String column, Object operand) {
            super(column, " IN ", operand);
        }

        public String getToken(String key) {
            return "(:" + key + ")";
        }
    }

}
