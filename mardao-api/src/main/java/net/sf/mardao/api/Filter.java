package net.sf.mardao.api;

import java.io.Serializable;
import net.sf.mardao.api.dao.Dao;
import net.sf.mardao.api.domain.PrimaryKeyEntity;

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

    public static class Foreign<T extends PrimaryKeyEntity<ID>, ID extends Serializable>
            extends Filter {

        private final Dao<T, ID> foreignDao;
        private final Filter       foreignExpression;

        public Foreign(String column, String operation, Dao<T, ID> foreignDao, Filter foreignExpression) {
            super(column, operation, foreignExpression);
            this.foreignExpression = foreignExpression;
            this.foreignDao = foreignDao;
        }

        public Dao<T, ID> getForeignDao() {
            return foreignDao;
        }

        public Filter getForeignExpression() {
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
