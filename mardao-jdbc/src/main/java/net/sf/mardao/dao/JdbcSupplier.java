package net.sf.mardao.dao;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.filter.Filter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Future;

/**
 * Created by sosandstrom on 2015-02-21.
 */
public class JdbcSupplier extends AbstractSupplier<JdbcKey, SqlRowSet, JdbcWriteValue, Connection> {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final DataFieldMaxValueIncrementer incrementer;

    public JdbcSupplier(DataSource dataSource, DataFieldMaxValueIncrementer incrementer) {
        this.dataSource = dataSource;
        this.incrementer = incrementer;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int count(Connection tx, Mapper mapper, JdbcKey ancestorKey, JdbcKey simpleKey, Filter... filters) {
        ArrayList arguments = new ArrayList();
        final String sql = "SELECT COUNT(" + mapper.getPrimaryKeyColumnName() + ") FROM " + mapper.getKind();
        return jdbcTemplate.queryForInt(sql, arguments.toArray());
    }

    @Override
    public void deleteValue(Connection tx, JdbcKey key) throws IOException {

    }

    @Override
    public void deleteValues(Connection tx, Collection<JdbcKey> keys) throws IOException {

    }

    @Override
    public SqlRowSet readValue(Connection tx, Mapper mapper, JdbcKey key) throws IOException {
        final StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(key.getKind())
                .append(" WHERE ")
                .append(mapper.getPrimaryKeyColumnName())
                .append("=?");
//                .append(mapper.getPrimaryKeyColumnName());
        ArrayList arguments = new ArrayList();
        arguments.add(null != key.getName() ? key.getName() : key.getId());

        if (null != mapper.getParentKeyColumnName()) {
            sql.append(" AND ")
                    .append(mapper.getParentKeyColumnName())
                    .append("=?");
//                    .append(mapper.getParentKeyColumnName());
            arguments.add(null == key.getParentKey() ? null :
                    (null != key.getParentKey().getName() ? key.getParentKey().getName() : key.getParentKey().getId()));
        }

        return jdbcTemplate.queryForRowSet(sql.toString(), arguments.toArray());
    }

    @Override
    public JdbcKey writeValue(Connection tx, JdbcKey key, JdbcWriteValue value) throws IOException {
        if (null == key.getName() && null == key.getId()) {

            // INSERT
            return insertValue(tx, key, value);
        }

        // UPDATE
        final Mapper mapper = value.mapper;
        String sql = "";
        jdbcTemplate.update(sql);

        return key;
    }

    @Override
    public JdbcKey insertValue(Connection tx, JdbcKey key, JdbcWriteValue value) throws IOException {
        final Mapper mapper = value.mapper;

        if (null == key.getName() && null == key.getId()) {
            final long id = incrementer.nextLongValue();
            key = toKey(key.getParentKey(), key.getKind(), id);
            value = createWriteValue(mapper, key.getParentKey(), id, value.entity);
            mapper.setPrimaryKey(value, key);
        }

        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName(mapper.getKind());
        insert.execute(value.parameterMap);
        return key;
    }

    @Override
    public Future<SqlRowSet> readFuture(Connection tx, Mapper mapper, JdbcKey key) throws IOException {
        return null;
    }

    @Override
    public Future<JdbcKey> writeFuture(Connection tx, JdbcKey key, JdbcWriteValue value) throws IOException {
        return null;
    }

    @Override
    public JdbcKey toKey(JdbcKey parentKey, String kind, Long lId) {
        return JdbcKey.of(parentKey, kind, lId);
    }

    @Override
    public JdbcKey toKey(JdbcKey parentKey, String kind, String sId) {
        return JdbcKey.of(parentKey, kind, sId);
    }

    @Override
    public Long toLongKey(JdbcKey key) {
        return key.getId();
    }

    @Override
    public String toStringKey(JdbcKey key) {
        return key.getName();
    }

    @Override
    public JdbcKey toParentKey(JdbcKey key) {
        return key.getParentKey();
    }

    @Override
    protected Object getReadObject(SqlRowSet value, String column) {
        return value.getObject(column);
    }

    @Override
    protected Object getWriteObject(JdbcWriteValue value, String column) {
        return value.parameterMap.get(column);
    }

    @Override
    public void setCollection(JdbcWriteValue value, String column, Collection c) {
        throw new IllegalArgumentException("Collections not supported yet.");
    }

    @Override
    protected void setObject(JdbcWriteValue value, String column, Object o) {
        value.parameterMap.put(column, o);
    }

    @Override
    public JdbcWriteValue createWriteValue(Mapper mapper, JdbcKey parentKey, Long id, Object entity) {
        final JdbcWriteValue writeValue = new JdbcWriteValue(entity, mapper);
        return writeValue;
    }

    @Override
    public JdbcWriteValue createWriteValue(Mapper mapper, JdbcKey parentKey, String id, Object entity) {
        return new JdbcWriteValue(entity, mapper);
    }

    @Override
    public void setPrimaryKey(JdbcWriteValue value, Mapper mapper, String column, JdbcKey primaryKey, Object Entity) {
        if (null != primaryKey) {
            if (null != primaryKey.getName()) {
                setString(value, column, primaryKey.getName());
                return;
            }
            if (null != primaryKey.getId()) {
                setLong(value, column, primaryKey.getId());
            }
        }
    }

    @Override
    public void setParentKey(JdbcWriteValue value, Mapper mapper, String column, JdbcKey parentKey, Object Entity) {
        if (null != parentKey) {
            if (null != parentKey.getName()) {
                setString(value, column, parentKey.getName());
                return;
            }
            if (null != parentKey.getId()) {
                setLong(value, column, parentKey.getId());
            }
        }
    }

    @Override
    public Connection beginTransaction() {
        return null;
    }

    @Override
    public void commitTransaction(Connection transaction) {

    }

    @Override
    public void rollbackActiveTransaction(Connection transaction) {

    }

    @Override
    public Iterable<SqlRowSet> queryIterable(Connection tx, String kind, boolean keysOnly, int offset, int limit, JdbcKey ancestorKey, JdbcKey simpleKey, String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters) {
        return null;
    }

    @Override
    public SqlRowSet queryUnique(Connection tx, JdbcKey parentKey, String kind, Filter... filters) {
        return null;
    }

    @Override
    public CursorPage<SqlRowSet> queryPage(Connection tx, final Mapper mapper, boolean keysOnly, int requestedPageSize, JdbcKey ancestorKey, String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, Collection<String> projections, String cursorString, Filter... filters) {
        final String sql = ""; // FIXME: mapper.getQuerySQL();
        ArrayList arguments = new ArrayList();
        // FIXME: build arguments from filters
        Object[] args = arguments.toArray();
        final CursorPage<SqlRowSet> jdbcPage = new CursorPage<SqlRowSet>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, args);
        jdbcPage.setItems(Arrays.asList(sqlRowSet));
        return jdbcPage;
    }
}
