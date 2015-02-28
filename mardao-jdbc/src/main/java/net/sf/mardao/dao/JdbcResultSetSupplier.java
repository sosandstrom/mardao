package net.sf.mardao.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sosandstrom on 2015-02-27.
 */
public class JdbcResultSetSupplier extends SupplierAdapter<Object, ResultSet, Void, Void> {

    @Override
    protected Object getReadObject(ResultSet value, String column) {
        try {
            return value.getObject(column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
