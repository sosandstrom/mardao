package net.sf.mardao.api.dao;

import java.io.Serializable;

import net.sf.mardao.api.domain.PrimaryKeyEntity;

public class Expression {
    private final String column;
    private final Object operation;
    private final Object operand;

    public Expression(String column, Object operation, Object operand) {
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

    public static class IN extends Expression {
        public IN(String column, Object operand) {
            super(column, " IN ", operand);
        }

        public String getToken(String key) {
            return "(:" + key + ")";
        }
    }

    public static class Foreign<T extends PrimaryKeyEntity, ID extends Serializable> extends Expression {

        private final Dao<T, ID> foreignDao;
        private final Expression foreignExpression;

        public Foreign(String column, String operation, Dao<T, ID> foreignDao, Expression foreignExpression) {
            super(column, operation, foreignExpression);
            this.foreignExpression = foreignExpression;
            this.foreignDao = foreignDao;
        }

        public Dao<T, ID> getForeignDao() {
            return foreignDao;
        }

        public Expression getForeignExpression() {
            return foreignExpression;
        }

        /**
         * It is the foreign operand which should be added to the arguments map
         */
        @Override
        public Object getOperand() {
            return foreignExpression.getOperand();
        }

        @Override
        public String getToken(String key) {
            if (false == foreignDao.getColumnNames().contains(foreignExpression.getColumn())) {
                throw new IllegalArgumentException("No such " + foreignDao.getTableName() + " column "
                        + foreignExpression.getColumn());
            }
            StringBuffer sql = new StringBuffer("(SELECT ");
            sql.append(foreignDao.getPrimaryKeyColumnName());
            sql.append(" FROM ");
            sql.append(foreignDao.getTableName());
            sql.append(" WHERE ");
            sql.append(foreignExpression.getColumn());
            sql.append(foreignExpression.getOperation());
            sql.append(foreignExpression.getToken(key));
            sql.append(')');
            return sql.toString();
        }
    }
}
