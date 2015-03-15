package net.sf.mardao.dao;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.filter.Filter;
import net.sf.mardao.core.filter.FilterOperator;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by sosandstrom on 2015-02-21.
 */
public class JdbcSupplier extends AbstractSupplier<JdbcKey, Object, JdbcWriteValue, Connection> {

    public static final String METHOD_SELECT = "SELECT ";
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
        final String sql = buildSQL(mapper, METHOD_SELECT,
                Arrays.asList("COUNT(" + mapper.getPrimaryKeyColumnName() + ")"), null, null, arguments, filters);
        return jdbcTemplate.queryForInt(sql, arguments.toArray());
    }

    @Override
    public void deleteValue(Connection tx, Mapper mapper, JdbcKey key) throws IOException {
        ArrayList<Filter> filters = buildKeyFilters(mapper, key);

        final ArrayList arguments = new ArrayList();
        final String sql = buildSQL(mapper, "DELETE ", Arrays.asList(""),
                null, null, arguments, filters.toArray(new Filter[filters.size()]));

        jdbcTemplate.update(sql, arguments.toArray());
    }

    @Override
    public void deleteValues(Connection tx, Mapper mapper, Collection<JdbcKey> keys) throws IOException {

    }

    @Override
    public Object readValue(Connection tx, Mapper mapper, JdbcKey key) throws IOException {
        ArrayList<Filter> filters = buildKeyFilters(mapper, key);

        ArrayList arguments = new ArrayList();
        final String sql = buildSQL(mapper, METHOD_SELECT, null, null, null, arguments, filters.toArray(new Filter[filters.size()]));

        RowMapper rowMapper = new JdbcRowMapper(mapper, new JdbcResultSetSupplier());
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, arguments.toArray());
        }
        catch (EmptyResultDataAccessException whenMissing) {
            return null;
        }
    }

    private ArrayList<Filter> buildKeyFilters(Mapper mapper, JdbcKey key) {
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(Filter.equalsFilter(mapper.getPrimaryKeyColumnName(),
                null != key.getName() ? key.getName() : key.getId()));

        if (null != mapper.getParentKeyColumnName()) {
            filters.add(Filter.equalsFilter(mapper.getParentKeyColumnName(),
                    null == key.getParentKey() ? null :
                            (null != key.getParentKey().getName() ? key.getParentKey().getName() : key.getParentKey().getId())));
        }
        return filters;
    }

    private String buildSQL(Mapper mapper, String method, Iterable<String> projectionsList,
                            Integer offset, Integer limit, Collection<Object> params, Filter... filters) {
        String projections = "*";
        if (null != projectionsList) {
            StringBuilder sb = new StringBuilder();
            for (String col : projectionsList) {
                if (0 < sb.length()) {
                    sb.append(',');
                }
                sb.append(col);
            }
            projections = sb.toString();
        }

        final StringBuilder sql = new StringBuilder(method)
                .append(projections)
                .append(" FROM ")
                .append(mapper.getKind());
        if (null != filters) {
            boolean isFirst = true;
            for (Filter f : filters) {

                sql.append(isFirst ? " WHERE " : " AND ");
                isFirst = false;
                sql.append(f.getColumn());
                sql.append(getOpsAsSQL(f.getOperator(), f.getOperand()));
                if (null != f.getOperand() || !FilterOperator.EQUALS.equals(f.getOperator())) {
                    params.add(f.getOperand());
                }
            }
        }

        if (null != limit && 0 < limit) {
            sql.append(" LIMIT ").append(limit);
            if (null != offset && 0 < offset) {
                sql.append(" OFFSET ").append(offset);
            }
        }

        return sql.toString();
    }

    private String getOpsAsSQL(FilterOperator operator, Object operand) {
        switch (operator) {
            case EQUALS: return null == operand ? " IS NULL" : " =?";
        }
        throw new IllegalArgumentException("Unsupported FilterOperator " + operator);
    }

    @Override
    public JdbcKey writeValue(Connection tx, JdbcKey key, JdbcWriteValue value) throws IOException {
        Serializable id = null == key ? null :
                (null != key.getName() ? key.getName() : key.getId());
        if (null == id) {

            // INSERT
            return insertValue(tx, key, value);
        }

        // UPDATE
        final Mapper mapper = value.mapper;
        final ArrayList arguments = new ArrayList();
        String sql = mapper.getWriteSQL(id, value, arguments);
        jdbcTemplate.update(sql, arguments.toArray());

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
    public Future<Object> readFuture(Connection tx, Mapper mapper, JdbcKey key) throws IOException {
        return null;
    }

    @Override
    public Future<JdbcKey> writeFuture(Connection tx, JdbcKey key, JdbcWriteValue value) throws IOException {
        return null;
    }

    @Override
    public JdbcKey toKey(JdbcKey parentKey, String kind, Serializable id) {
        if (id instanceof String) {
            return JdbcKey.of(parentKey, kind, (String) id);
        }
        return JdbcKey.of(parentKey, kind, (Long) id);
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
    protected Object getReadObject(Object value, String column) {
        throw new IllegalArgumentException("Not supported for this Supplier");
    }

    @Override
    public Object getWriteObject(Object value, String column) {
        final JdbcWriteValue writeValue = (JdbcWriteValue) value;
        return writeValue.parameterMap.get(column);
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
    public JdbcKey getKey(Object value, String column) {
        final Serializable id = (Serializable) getReadObject(value, column);
        return toKey(null, JdbcSupplier.class.getSimpleName(), id);
    }

    @Override
    public Object createEntity(Mapper mapper, Object readValue) {
        return readValue;
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
    public Iterable<Object> queryIterable(Connection tx, final Mapper mapper, boolean keysOnly, int offset, int limit, JdbcKey ancestorKey, JdbcKey simpleKey, String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters) {
        // TODO: add ancestor filter
//        if (null != ancestorKey && null != mapper.getParentKeyColumnName()) {
//            columns.add(mapper.getParentKeyColumnName());
//            arguments.add(null != ancestorKey.getName() ? ancestorKey.getName() : ancestorKey.getId());
//        }

        final ArrayList arguments = new ArrayList();
        final String sql = buildSQL(mapper, METHOD_SELECT, null, offset,
                limit, arguments, filters);

        RowMapper rowMapper = new JdbcRowMapper(mapper, new JdbcResultSetSupplier());
        final List items = jdbcTemplate.query(sql, rowMapper, arguments.toArray());
        return items;
    }

    @Override
    public Object queryUnique(Connection tx, Mapper mapper, JdbcKey parentKey, Filter... filters) {
        return null;
    }

    @Override
    public CursorPage<Object> queryPage(Connection tx, final Mapper mapper, boolean keysOnly, int requestedPageSize, JdbcKey ancestorKey,
                                           String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending,
                                           Collection<String> projections, String cursorString, Filter... filters) {

        // TODO: add ancestor filter
//        if (null != ancestorKey && null != mapper.getParentKeyColumnName()) {
//            columns.add(mapper.getParentKeyColumnName());
//            arguments.add(null != ancestorKey.getName() ? ancestorKey.getName() : ancestorKey.getId());
//        }

        final int offset = null != cursorString ? Integer.parseInt(cursorString) : 0;
        final ArrayList arguments = new ArrayList();
        final String sql = buildSQL(mapper, METHOD_SELECT, projections, offset,
                requestedPageSize, arguments, filters);

        RowMapper rowMapper = new JdbcRowMapper(mapper, new JdbcResultSetSupplier());
        final List items = jdbcTemplate.query(sql, rowMapper, arguments.toArray());

        final CursorPage<Object> cursorPage = new CursorPage<Object>();

        // if first page and populate totalSize, fetch this with async query:
        if (null == cursorString) {
            int count = count(tx, mapper, ancestorKey, null, filters);
            cursorPage.setTotalSize(count);
        }

        cursorPage.setItems(items);

        // only if next is available
        if (items.size() == requestedPageSize) {

            // only if page size != total size
            if (null == cursorPage.getTotalSize() || items.size() < cursorPage.getTotalSize()) {
                cursorPage.setCursorKey(Integer.toString(offset + items.size()));
            }
        }

        return cursorPage;
    }
}
