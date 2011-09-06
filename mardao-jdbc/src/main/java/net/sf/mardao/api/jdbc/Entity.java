package net.sf.mardao.api.jdbc;

import java.io.Serializable;
import java.sql.ResultSet;

import net.sf.mardao.api.domain.JDBCPrimaryKeyEntity;

public final class Entity implements Serializable {
    private static final long                          serialVersionUID = -7502074119844190854L;
    final JDBCPrimaryKeyEntity<? extends Serializable> domain;
    final ResultSet                                    resultSet;
    final int                                          rowNum;

    public Entity(JDBCPrimaryKeyEntity<? extends Serializable> domain, ResultSet resultSet, int rowNum) {
        this.domain = domain;
        this.resultSet = resultSet;
        this.rowNum = rowNum;
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

}
