package net.sf.mardao.api.jdbc;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.HashMap;

import net.sf.mardao.api.domain.JDBCPrimaryKeyEntity;

public final class Entity implements Serializable {
    private static final long                                  serialVersionUID = -7502074119844190854L;
    final private JDBCPrimaryKeyEntity<? extends Serializable> domain;
    final private ResultSet                                    resultSet;
    final private int                                          rowNum;
    final private HashMap<String, Object>                      parameters;

    public Entity(JDBCPrimaryKeyEntity<? extends Serializable> domain, ResultSet resultSet, int rowNum,
            HashMap<String, Object> parameters) {
        this.domain = domain;
        this.resultSet = resultSet;
        this.rowNum = rowNum;
        this.parameters = parameters;
    }

    public JDBCPrimaryKeyEntity<? extends Serializable> getDomain() {
        return domain;
    }

    public final ResultSet getResultSet() {
        return resultSet;
    }

    public final int getRowNum() {
        return rowNum;
    }

    public final HashMap<String, Object> getParameters() {
        return parameters;
    }

}
