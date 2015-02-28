package net.sf.mardao.dao;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sosandstrom on 2015-02-27.
 */
public class JdbcRowMapper<T> implements RowMapper<T> {
    private final Mapper<T, ?> mapper;
    private final Supplier<Object, ResultSet, ?, ?> supplier;

    public JdbcRowMapper(Mapper<T, ?> mapper, Supplier<Object, ResultSet, ?, ?> supplier) {
        this.mapper = mapper;
        this.supplier = supplier;
    }

    @Override
    public T mapRow(ResultSet resultSet, int i) throws SQLException {
        return mapper.fromReadValue(resultSet, supplier);
    }
}
