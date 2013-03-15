package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.sql.DataSource;
import net.sf.mardao.core.CompositeKey;
import net.sf.mardao.core.CoreEntity;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;
import net.sf.mardao.core.geo.DLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;

public abstract class TypeDaoImpl<T, ID extends Serializable> extends
        DaoImpl<T, ID, Long, Iterable, CoreEntity, CompositeKey> {
    public static final String DIALECT_DEFAULT = "SQL";
    public static final String DIALECT_MySQL = "MySQL";
    
    protected static final Properties DATA_TYPES_DEFAULT = new Properties();
    protected static final Properties DATA_TYPES_MySQL = new Properties(DATA_TYPES_DEFAULT);
    protected static final Map<String, Properties> DATA_DIALECTS = new HashMap<String, Properties>();
    
    static {
        DATA_DIALECTS.put(DIALECT_DEFAULT, DATA_TYPES_DEFAULT);
        DATA_DIALECTS.put(DIALECT_MySQL, DATA_TYPES_MySQL);

        DATA_TYPES_DEFAULT.setProperty(Double.class.getName(), "DOUBLE PRECISION");
        DATA_TYPES_DEFAULT.setProperty(Long.class.getName(), "BIGINT");
        DATA_TYPES_DEFAULT.setProperty(Integer.class.getName(), "INTEGER");
        DATA_TYPES_DEFAULT.setProperty(Short.class.getName(), "SMALLINT");
        DATA_TYPES_DEFAULT.setProperty(Byte.class.getName(), "TINYINT");
        DATA_TYPES_DEFAULT.setProperty(Date.class.getName(), "TIMESTAMP");
        DATA_TYPES_DEFAULT.setProperty(String.class.getName(), "VARCHAR");
        DATA_TYPES_DEFAULT.setProperty(Boolean.class.getName(), "BIT(1)");
        DATA_TYPES_DEFAULT.setProperty(DLocation.class.getName(), "VARCHAR(33)");
        DATA_TYPES_DEFAULT.setProperty(getPrimaryKeyClass(Long.class.getName()), "BIGINT");
        DATA_TYPES_DEFAULT.setProperty(getPrimaryKeyClass(String.class.getName()), "VARCHAR(128)");
        DATA_TYPES_DEFAULT.setProperty("AUTO_INCREMENT", "AUTO_INCREMENT");
        DATA_TYPES_DEFAULT.setProperty("COLUMN_QUOTE", "");

        DATA_TYPES_MySQL.setProperty(String.class.getName(), "VARCHAR(500)");
        DATA_TYPES_MySQL.setProperty("COLUMN_QUOTE", "`");
    }
    
    protected NamedParameterJdbcTemplate jdbcTemplate;
    protected SimpleJdbcInsert jdbcInsert;
    private String dialect = DIALECT_DEFAULT;
    
    /** Used to generate unique ids */
    @Autowired
    protected DataFieldMaxValueIncrementer jdbcIncrementer;
    
    protected TypeDaoImpl(Class<T> type, Class<ID> idType) {
        super(type, idType);
    }
    
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(getTableName());
    }
    
    protected final RowMapper<T> jdbcRowMapper = new RowMapper<T>() {

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            CompositeKey parentKey = null;
            if (null != getParentKeyColumnName()) {
                final Long parentId = rs.getLong(getParentKeyColumnName());
                parentKey = new CompositeKey(null, parentId, null);
            }
            final Class pkClass = getColumnClass(getPrimaryKeyColumnName());
            final ID simpleKey = (ID) (Long.class.equals(pkClass) ? 
                          rs.getLong(getPrimaryKeyColumnName()) : rs.getString(getPrimaryKeyColumnName()));
            final T domain;
            try {
                domain = createDomain(parentKey, simpleKey);
                
                // TODO: populate
                
            } catch (InstantiationException ex) {
                throw new SQLException("Instantiating domain", ex);
            } catch (IllegalAccessException ex) {
                throw new SQLException("Accessing domain", ex);
            }
            
            return domain;
        }
        
    };
    
    /**
     * Checks that the table exists
     */
    public void init() {
        // when autowired, no setter is invoked:
        if (jdbcIncrementer instanceof MySQLMaxValueIncrementer) {
            dialect = DIALECT_MySQL;
        }
        
        LOG.debug("init with dialect {} and incrementer {}", dialect, jdbcIncrementer);
        // check if queryable
        try {
            final StringBuffer sql = new StringBuffer();
            sql.append("SELECT 1 FROM ");
            sql.append(getTableName());
            LOG.debug(sql.toString());
            jdbcTemplate.getJdbcOperations().execute(sql.toString());
        }
        catch (NonTransientDataAccessException whenCreate) {
            checkIncrementer();
            createTable();
        }
    }
    
    protected void checkIncrementer() {
        final String sql = "SELECT 1 FROM id_sequence;";
        try {
            jdbcTemplate.getJdbcOperations().execute(sql);
        }        
        catch (NonTransientDataAccessException whenCreate) {
            createIncrementer();
        }
    }
    
    protected void createIncrementer() {
        if (DIALECT_MySQL.equals(dialect)) {
            final StringBuffer sql = new StringBuffer();
            sql.append("CREATE TABLE id_sequence");
            // column definitions
                sql.append(" (highest INT NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            LOG.info(sql.toString());
            jdbcTemplate.getJdbcOperations().execute(sql.toString());

            final String sqlInsert = "INSERT INTO id_sequence VALUES(0);";
            jdbcTemplate.getJdbcOperations().execute(sqlInsert);
        }
    }
    
    protected void createTable() {
        final StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ");
        sql.append(getTableName());
        // column definitions
        sql.append(" (");

        appendPrimaryKeyColumnDefinition(sql);
        appendParentKeyColumnDefinition(sql);
        
        for (String columnName : getBasicColumnNames()) {
            sql.append(", ");
            appendColumnDefinition(sql, columnName);
        }
        
        // foreign keys?
        for (String columnName : getManyToOneColumnNames()) {
            sql.append(", ");
            appendColumnDefinition(sql, columnName);
            if (DIALECT_MySQL.equals(dialect)) {
                sql.append(" DEFAULT NULL");
            }
            else {
                final DaoImpl foreignDao = getManyToOneDao(columnName);
                sql.append(" CONSTRAINT ");
                sql.append(getTableName());
                sql.append('_');
                sql.append(columnName);
    //            sql.append(" FOREIGN KEY (");
    //            sql.append(columnName);
    //            sql.append(')');
                sql.append(" REFERENCES ");
                sql.append(foreignDao.getTableName());
                sql.append('(');
                sql.append(foreignDao.getPrimaryKeyColumnName());
                sql.append(')');
            }
        }
        
        if (DIALECT_MySQL.equals(dialect)) {
            appendConstraints(sql);
        }
        
        if (DIALECT_MySQL.equals(dialect)) {
            sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }
        else {
            sql.append(");");
        }
        
        LOG.info(sql.toString());
        jdbcTemplate.getJdbcOperations().execute(sql.toString());
    }

    protected void appendColumnDefinition(StringBuffer sql, String columnName) {
        appendColumnDefinition(sql, columnName, false);
    }
    
    protected void appendColumnDefinition(StringBuffer sql, String columnName, 
            boolean isPrimaryKey) {
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(columnName);
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(' ');
        final String className = getColumnClass(columnName).getName();
        String dataType = getDataType(className, isPrimaryKey);
        if (null == dataType) {
            dataType = getDataType(Long.class.getName());
        }
        sql.append(dataType);
    }
    
    protected void appendPrimaryKeyColumnDefinition(StringBuffer sql) {
        final String columnName = getPrimaryKeyColumnName();
        appendColumnDefinition(sql, columnName, true);

        if (DIALECT_MySQL.equals(dialect)) {
            sql.append(" NOT NULL");
        }
        else {
            sql.append(" PRIMARY KEY");
        }
    }
    
    protected void appendParentKeyColumnDefinition(StringBuffer sql) {
        final String columnName = getParentKeyColumnName();
        if (null != columnName) {
            sql.append(", ");
            appendColumnDefinition(sql, columnName, true);
        }
    }
    
    protected void appendConstraints(StringBuffer sql) {
        // primary key
        sql.append(", ");
        sql.append("PRIMARY KEY (");
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(getPrimaryKeyColumnName());
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(")");
        
        // parent key
        if (null != getParentKeyColumnName()) {
            appendConstraint(sql, getParentKeyColumnName(), mardaoParentDao);
        }
        
        // ManyToOnes
        for (String columnName : getManyToOneColumnNames()) {
            appendConstraint(sql, columnName, getManyToOneDao(columnName));
        }
    }
    
    protected void appendConstraint(StringBuffer sql, String columnName, DaoImpl foreignDao) {
        sql.append(", ");
        if (DIALECT_MySQL.equals(dialect)) {
            sql.append("CONSTRAINT ");
            sql.append(getDataType("COLUMN_QUOTE"));
            sql.append("Fk");
            sql.append(getTableName());
            sql.append(columnName);
            sql.append(getDataType("COLUMN_QUOTE"));
            sql.append(" FOREIGN KEY (");
            sql.append(getDataType("COLUMN_QUOTE"));
            sql.append(columnName);
            sql.append(getDataType("COLUMN_QUOTE"));
            sql.append(") REFERENCES ");
            sql.append(getDataType("COLUMN_QUOTE"));
            sql.append(foreignDao.getTableName());
            sql.append(getDataType("COLUMN_QUOTE"));
            sql.append('(');
            sql.append(getDataType("COLUMN_QUOTE"));
            sql.append(foreignDao.getPrimaryKeyColumnName());
            sql.append(getDataType("COLUMN_QUOTE"));
            sql.append(')');
        }
        else {
        }
    }
    
    protected String getDataType(String className) {
        return getDataType(className, false);
    }
    
    protected String getDataType(String className, boolean isPrimaryKey) {
        final Properties dialectTypes = DATA_DIALECTS.get(dialect);
        String returnValue = dialectTypes.getProperty(
                isPrimaryKey ? getPrimaryKeyClass(className) :  className);
//        if (null == returnValue) {
//            returnValue = dialectTypes.getProperty(Long.class.getName());
//        }
        return returnValue;
    }
    
    protected static final String getPrimaryKeyClass(String className) {
        return String.format("pk_%s", className);
    }
    
    // --- BEGIN DaoImpl overrides ---

//    @Override
//    public CoreEntity domainToCore(T domain, Date currentDate) {
//        if (null == domain) {
//            return null;
//        }
//        
//        final CoreEntity core = createCore(getParentKey(domain), getSimpleKey(domain));
//        
//        // created, updated
//        String principal = getCreatedBy(domain);
//        if (null == principal) {
//            principal = getPrincipalName();
//            if (null == principal) {
//                principal = PRINCIPAL_NAME_ANONYMOUS;
//            }
//            _setCreatedBy(domain, principal);
//        }
//        setCoreProperty(core, getCreatedByColumnName(), principal);
//        
//        Date date = getCreatedDate(domain);
//        if (null == date) {
//            date = currentDate;
//            _setCreatedDate(domain, currentDate);
//        }
//        setCoreProperty(core, getCreatedDateColumnName(), date);
//        
//        principal = getPrincipalName();
//        if (null == principal) {
//            principal = PRINCIPAL_NAME_ANONYMOUS;
//        }
//        _setUpdatedBy(domain, principal);
//        setCoreProperty(core, getUpdatedByColumnName(), principal);
//        
//        _setUpdatedDate(domain, currentDate);
//        setCoreProperty(core, getUpdatedDateColumnName(), currentDate);
//        
//        // Domain Entity-specific properties
//        for (String name : getColumnNames()) {
//            copyDomainPropertyToCore(name, domain, core);
//        }
//
//        // geoboxes
//        if (null != getGeoLocationColumnName()) {
//            updateGeoModel(domain, core);
//        }
//
//        return core;
//    }
    
    // --- END DaoImpl overrides ---
    
    // --- BEGIN persistence-type beans must implement these ---

    @Override
    protected ID coreToSimpleKey(CoreEntity core) {
        return null != core ? coreKeyToSimpleKey(core.getPrimaryKey()) : null;
    }

    
    @Override
    protected ID coreKeyToSimpleKey(CompositeKey key) {
        if (null == key) {
            return null;
        }
        
        if (Long.class.isAssignableFrom(simpleIdClass)) {
            if (null == key.getId()) {
                return null;
            }
            return (ID) Long.valueOf(key.getId());
        }
        
        return (ID) key.getName();
    }

    @Override
    protected Long coreToParentKey(CoreEntity core) {
        CompositeKey parentKey = null != core ? core.getParentKey() : null;
        
        return null != parentKey ? parentKey.getId() : null;
    }

    @Override
    protected Long coreKeyToParentKey(CompositeKey primaryKey) {
        CompositeKey parentKey = null != primaryKey ? primaryKey.getParentKey() : null;
        
        return null != parentKey ? parentKey.getId() : null;
    }
    
    protected Long coreKeyToParentKey(Long parentId) {
        return parentId;
    }

    @Override
    protected int count(Object ancestorKey, Object simpleKey, Filter... filters) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(");
        sql.append(getPrimaryKeyColumnName());
        sql.append(") FROM ");
        sql.append(getTableName());
        
        Map<String, Object> params = appendWhereFilters(sql, filters);
        final int count = jdbcTemplate.queryForInt(sql.toString(), params);
        LOG.debug("{} returns {}", sql.toString(), count);
        return count;
    }
    
    @Override
    public CoreEntity createCore(Object primaryKey) {
        final CoreEntity core = new CoreEntity();
        core.setPrimaryKey((CompositeKey) primaryKey);
        
        final TreeMap<String, Object> props = new TreeMap<String, Object>();
        core.setProperties(props);
        
        // populate props with parent key
        setCoreProperty(core, getParentKeyColumnName(), getParentKeyByPrimaryKey(primaryKey));
        
        // populate props with simple key
        setCoreProperty(core, getPrimaryKeyColumnName(), getSimpleKeyByPrimaryKey(primaryKey));
        
        return core;
    }    
    
    @Override
    public CoreEntity createCore(Object parentKey, ID simpleKey) {
        final CompositeKey pk = createCoreKey(parentKey, simpleKey);
        
        return createCore(pk);
    }

    protected CompositeKey createCoreKey(Map<String, Object> props) {
        CompositeKey parentKey = null;
        if (null != getParentKeyColumnName()) {
            final Long parentId = (Long) props.get(getParentKeyColumnName());
            parentKey = new CompositeKey(null, parentId, null);
        }
        final ID simpleKey = (ID) props.get(getPrimaryKeyColumnName());
        return createCoreKey(parentKey, simpleKey);
    }
    
    protected CompositeKey createCoreKey(Object parentKey, ID simpleKey) {
        final CompositeKey pk = new CompositeKey((CompositeKey) parentKey, null, null);
        if (simpleKey instanceof String) {
            pk.setName((String) simpleKey);
        }
        else {
            pk.setId((Long) simpleKey);
        }
        
        return pk;
    }
    
    protected Collection<CompositeKey> createCoreKeys(Object parentKey, Iterable<ID> simpleKeys) {
        final ArrayList<CompositeKey> coreKeys = new ArrayList<CompositeKey>();
        CompositeKey core;
        for (ID simpleKey : simpleKeys) {
            core = createCoreKey(parentKey, simpleKey);
            coreKeys.add(core);
        }
        return coreKeys;
    }
    
    @Override
    public final Filter createEqualsFilter(String fieldName, Object param) {
        final Class columnClass = getColumnClass(fieldName);
        // is this a Entity reference?
        if (AbstractCreatedUpdatedEntity.class.isAssignableFrom(columnClass)){
            if (param instanceof CompositeKey) {
                param = getSimpleKeyByPrimaryKey(param);
            }
        }
        return new Filter(fieldName, "=", param);
    }

    @Override
    public Filter createGreaterThanOrEqualFilter(String columnName, Object value) {
        return new Filter(columnName, ">=", value);
    }
    
    @Override
    public final Filter createInFilter(String fieldName, Collection param) {
        return new Filter.IN(fieldName, param);
    }
    

    @Override
    protected String createMemCacheKey(Object parentKey, ID simpleKey) {
        return String.format("%s:%s", getTableName(), simpleKey);
    }
    
    @Override
    protected int doDelete(Object parentKey, Iterable<ID> simpleKeys) {
        if (simpleKeys instanceof ArrayList && ((ArrayList) simpleKeys).isEmpty()) {
            return 0;
        }
        final StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM ");
        sql.append(getTableName());
        sql.append(" WHERE ");
        sql.append(getPrimaryKeyColumnName());
        sql.append(" IN (:ids)");
        
        // we know simpleKeys is a List:
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("ids", simpleKeys);
        
        LOG.debug("{} for {}", sql.toString(), params);
        return jdbcTemplate.update(sql.toString(), params);
    }

    @Override
    protected int doDelete(Iterable<T> domains) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final Iterable<Key> keys = (Iterable) domainsToPrimaryKeys(domains);
//        getDatastoreService().delete(keys);
//        return -1;
    }
    
    @Override
    public int deleteAll() {
        String sql = String.format("DELETE FROM %s;", getTableName());
        LOG.info(sql.toString());
        jdbcTemplate.getJdbcOperations().execute(sql.toString());
        return -1;
    }
    
    @Override
    public void dropTable() {
        String sql = String.format("DROP TABLE %s;", getTableName());
        LOG.info(sql.toString());
        jdbcTemplate.getJdbcOperations().execute(sql.toString());
    }
    
    protected Map<String, Object>  appendWherePrimaryKeys(StringBuffer sql, 
            CompositeKey parentKey, ID simpleKey) {
        final Map<String, Object> params = new HashMap<String, Object>();
        if (null == parentKey && null == simpleKey) {
            return params;
        }
        
        sql.append(" WHERE ");
        
        if (null != simpleKey) {
            sql.append(getPrimaryKeyColumnName());
            sql.append("=:");
            sql.append(getPrimaryKeyColumnName());
            params.put(getPrimaryKeyColumnName(), simpleKey);
            
            if (null != parentKey) {
                sql.append(" AND ");
            }
        }
        
        if (null != parentKey) {
            sql.append(getParentKeyColumnName());
            sql.append("=:");
            sql.append(getParentKeyColumnName());
            params.put(getParentKeyColumnName(), parentKey.getId());
        }
        
        return params;
    }
    
    protected Map<String, Object> appendSelectFilters(StringBuffer sql, Filter... filters) {
        final Map<String, Object> params = appendWhereFilters(sql, filters);
        
        return params;
    }


    protected Map<String, Object> appendWhereFilters(StringBuffer sql, 
            Filter... filters) {
        final Map<String, Object> params = new HashMap<String, Object>();
        if (null == filters || 0 == filters.length) {
            return params;
        }
        
        sql.append(" WHERE ");
        int i = 0;
        String token;
        for (Filter f : filters) {
            if (0 < i) {
                sql.append(" AND ");
            }
            sql.append(f.getColumn());
            if (null == f.getOperand() && "=".equals(f.getOperation())) {
                sql.append(" IS NULL");
            }
            else {
                sql.append(f.getOperation());
                token = String.format("%s%d", f.getColumn(), i);
                sql.append(f.getToken(token));
                params.put(token, f.getOperand());
            }
            i++;
        }
        return params;
    }
    
    protected void appendLimitOffset(StringBuffer sql, int limit, int offset) {
        if (0 < limit) {
            sql.append(" LIMIT ");
            sql.append(limit);
            if (0 < offset) {
                sql.append(" OFFSET ");
                sql.append(offset);
            }
        }
    }

    protected StringBuffer createSelect(boolean keysOnly) {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        sql.append(keysOnly ? getPrimaryKeyColumnName() : "*");
        sql.append(" FROM ");
        sql.append(getTableName());
        return sql;
    }
    
    @Override
    protected T doFindByPrimaryKey(Object parentKey, ID simpleKey) {
        return findUniqueBy(createEqualsFilter(getPrimaryKeyColumnName(), simpleKey));
        
//        final StringBuffer sql = createSelect(false);
//        final Map<String, Object> params = appendWherePrimaryKeys(sql, (CompositeKey) parentKey, simpleKey);
//        
//        LOG.debug("SQL: {} Params: {}", sql.toString(), params);
//        final Map<String, Object> props = jdbcTemplate.queryForMap(sql.toString(), params);
//        
//        final CompositeKey pk = createCoreKey(props);
//        final CoreEntity core = new CoreEntity();
//        core.setPrimaryKey(pk);
//        core.setProperties(props);
//        final T domain = coreToDomain(core);
//        return domain;
    }

    @Override
    protected Future<?> doFindByPrimaryKeyForFuture(final Object parentKey, final ID simpleKey) {
        final Filter primaryKeyFilter = createEqualsFilter(getPrimaryKeyColumnName(), simpleKey);
        final CompositeKey primaryKey = (CompositeKey) getPrimaryKey(parentKey, simpleKey);
        FutureTask<CoreEntity> task = new FutureTask<CoreEntity>(new Callable<CoreEntity>() {
            @Override
            public CoreEntity call() throws Exception {
                Map<String, Object> props = findUniquePropsBy(primaryKeyFilter);
                final CoreEntity core = new CoreEntity();
                core.setProperties(props);
                core.setPrimaryKey(primaryKey);
                return core;
            }
        });
        new Thread(task).start();
        return task;
    }

    @Override
    protected Future<?> doPersistCoreForFuture(final CoreEntity core) {
        FutureTask<CompositeKey> task = new FutureTask<CompositeKey>(new Callable<CompositeKey>() {
            @Override
            public CompositeKey call() throws Exception {
                Collection<CompositeKey> ids = persistCore(Arrays.asList(core));
                return ids.iterator().next();
            }
        });
        new Thread(task).start();
        return task;
    }

    @Override
    protected Future<List<CompositeKey>> doPersistCoreForFuture(final Iterable<CoreEntity> entities) {
        FutureTask<List<CompositeKey>> task = new FutureTask<List<CompositeKey>>(new Callable<List<CompositeKey>>() {
            @Override
            public List<CompositeKey> call() throws Exception {
                List<CompositeKey> ids = persistCore(entities);
                return ids;
            }
        });
        new Thread(task).start();
        return task;
    }
    
    
    
    @Override
    protected Iterable<T> doQueryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys) {
        final ArrayList<ID> values = new ArrayList<ID>();
        for (ID id : simpleKeys) {
            values.add(id);
        }
        final Filter inFilter = createInFilter(getPrimaryKeyColumnName(), values);
        return queryIterable(false, 0, -1, null, parentKey, 
                null, false, null, false, inFilter);
    }
    
    protected Map<String, Object> findUniquePropsBy(Filter... filters) {
        StringBuffer sql = createSelect(false);
        Map<String, Object> params = appendWhereFilters(sql, filters);
        LOG.info("{} with params {}", sql.toString(), params);
        try {
            final Map<String, Object> props = jdbcTemplate.queryForMap(sql.toString(), params);
            return props;
        }
        catch (EmptyResultDataAccessException notFound) {
            return null;
        }
    }

    @Override
    protected T findUniqueBy(Filter... filters) {
        final Map<String, Object> props = findUniquePropsBy(filters);
        if (null == props) {
            return null;
        }
        final T domain = propsToDomain(props);
        return domain;
    }

    @Override
    protected ID findUniqueKeyBy(Filter... filters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    protected Object getCoreProperty(CoreEntity core, String name, Class domainPropertyClass) {
        Object value = null;
        if (null != core && null != name) {
            value = core.getProperty(name);
            if (DLocation.class.equals(domainPropertyClass) && null != value) {
                final String latLong = (String)value;
                final int commaIndex = latLong.indexOf(',');
                value = new DLocation(Float.parseFloat(latLong.substring(0, commaIndex)), 
                        Float.parseFloat(latLong.substring(commaIndex+1)));
            }
        }
        return value;
    }

    @Override
    public String getKeyString(Object key) {
        return CompositeKey.keyToString((CompositeKey) key);
    }
    
    @Override
    public Object getParentKey(T domain) {
        return null;
    }
    
    @Override
    public Object getParentKeyByPrimaryKey(Object primaryKey) {
        return null != primaryKey ? ((CompositeKey) primaryKey).getParentKey() : null;
    }
    
    @Override
    public Object getPrimaryKey(T domain) {
        if (null == domain) {
            return null;
        }
        return createCoreKey(getParentKey(domain), getSimpleKey(domain));
    }

    @Override
    public Object getPrimaryKey(String keyString) {
        return CompositeKey.stringToKey(keyString);
    }
    
    @Override
    protected List<CompositeKey> persistCore(Iterable<CoreEntity> itrbl) {
        final List<CompositeKey> returnValue = new ArrayList<CompositeKey>();
        final ArrayList<Map<String, Object>> propsList = new ArrayList<Map<String, Object>>();
        CompositeKey parentKey, primaryKey;
        ID simpleKey;
        Map<String, Object> props;
        for (CoreEntity core : itrbl) {
            props = core.getProperties();
            simpleKey = (ID) props.get(getPrimaryKeyColumnName());
            if (null == simpleKey) {
                Long lid = jdbcIncrementer.nextLongValue();
                simpleKey = (ID) lid;
                props.put(getPrimaryKeyColumnName(), simpleKey);
                LOG.info("generating ID {} for {}", simpleKey, getTableName());
            }
            propsList.add(props);
            LOG.info("persistCore {} with {}", getTableName(), props);
            
            parentKey = null;
            if (null != getParentKeyColumnName()) {
                final Long parentId = (Long) props.get(getParentKeyColumnName());
                parentKey = new CompositeKey(null, parentId, null);
            }
            primaryKey = createCoreKey(parentKey, simpleKey);
            returnValue.add(primaryKey);
        }
        
        // batch-insert entities with simpleKey assigned
        if (!propsList.isEmpty()) {
            final Map[] empty = new Map[propsList.size()];
            final Map<String, Object>[] batch = propsList.toArray(empty);

            jdbcInsert.executeBatch(batch);
        }
        return returnValue;
    }
    
    @Override
    protected CursorPage<T, ID> queryPage(boolean keysOnly, int requestedPageSize,
            Object ancestorKey, Object simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            String cursorString,
            Filter... filters) {
        
        final int offset = null != cursorString ? Integer.parseInt(cursorString.toString()) : 0;
        
        // we now this Iterable is backed by a List:
        final List<T> domains = (List<T>) queryIterable(keysOnly, offset, requestedPageSize, 
                ancestorKey, simpleKey, 
                primaryOrderBy, primaryIsAscending, 
                secondaryOrderBy, secondaryIsAscending, filters);
        
        final CursorPage<T, ID> cursorPage = new CursorPage<T, ID>();
        cursorPage.setRequestedPageSize(requestedPageSize);
        cursorPage.setItems(domains);
        
        // only if next is available
        if (domains.size() == requestedPageSize) {
            cursorPage.setCursorKey(Integer.toString(offset + requestedPageSize));
        }
        
        return cursorPage;
    }

    @Override
    protected Iterable<T> queryIterable(boolean keysOnly, 
            int offset, int limit,
            Object ancestorKey, Object simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters) {
        if ((/* -1 == limit ||*/ 1000 < limit) && !keysOnly) {
            throw new UnsupportedOperationException("Not supported for Large Objects yet.");
        }
        
        final StringBuffer sql = createSelect(keysOnly);
        
        final Map<String, Object> params = appendWhereFilters(sql, filters);
        
        appendLimitOffset(sql, limit, offset);
        
        LOG.debug("{} with params {}", sql.toString(), params);
        
        final List<Map<String, Object>> itemsProps = jdbcTemplate.queryForList(sql.toString(), params);
        
        final List<T> domains = new ArrayList<T>();
        
        CoreEntity core;
        CompositeKey pk;
        T domain;
        for (Map<String, Object> props : itemsProps) {
            domain = propsToDomain(props);
            domains.add(domain);
        }
        
        return domains;
    }

    @Override
    protected Iterable<ID> queryIterableKeys(
            int offset, int limit,
            Object ancestorKey, Object simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters) {
        
        final StringBuffer sql = createSelect(true);
        
        final Map<String, Object> params = appendWhereFilters(sql, filters);
        
        final Class simpleClass = getColumnClass(getPrimaryKeyColumnName());
        LOG.debug("{}, ID.class is {}", sql.toString(), simpleClass);
        final List keys = jdbcTemplate.queryForList(sql.toString(), params, simpleClass);

        return keys;
    }

    @Override
    protected void setCoreProperty(Object core, String name, Object value) {
        if (null != name) {
            if (value instanceof DLocation) {
                final DLocation location = (DLocation) value;
                value = String.format("%f,%f", location.getLatitude(), location.getLongitude());
            }
            else if (value instanceof CompositeKey) {
                value = ((CompositeKey) value).getId();
            }
            ((CoreEntity) core).setProperty(name, value);
        }
    }

    @Override
    protected CursorPage<ID, ID> whatsDeleted(Date since, int pageSize, String cursorKey) {
        LOG.warn("whatsDeleted not implemented for MySQL yet.");
        CursorPage<ID, ID> page = new CursorPage<ID, ID>();
        page.setItems(Collections.EMPTY_LIST);
        return page;
    }
    
    // --- END persistence-type beans must implement these ---

    @Override
    public void update(T domain) {
        update(Arrays.asList(domain));
    }

    @Override
    public void update(Iterable<T> domains) {
        // build the SQL
        final StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ");
        sql.append(getTableName());
        sql.append(" SET ");
        
        boolean isFirst = true;
        // @Basic columns
        for (String columnName : getBasicColumnNames()) {
            if (!isFirst) {
                sql.append(", ");
            }
            else {
                isFirst = false;
            }
            sql.append(columnName);
            sql.append(" = :");
            sql.append(columnName);
        }
        
        // @ManyToOne columns
        for (String columnName : getManyToOneColumnNames()) {
            if (!isFirst) {
                sql.append(", ");
            }
            else {
                isFirst = false;
            }
            sql.append(columnName);
            sql.append(" = :");
            sql.append(columnName);
        }
        
        // WHERE primaryKey
        sql.append(" WHERE ");
        sql.append(getPrimaryKeyColumnName());
        sql.append(" = :");
        sql.append(getPrimaryKeyColumnName());
        
        final Date currentDate = new Date();
        ArrayList<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
        for (T domain : domains) {
            final CoreEntity core = domainToCore(domain, currentDate);
            params.add(core.getProperties());
        }
        Map<String, Object>[] batchValues = (Map<String, Object>[]) params.toArray(new Map[params.size()]);
        jdbcTemplate.batchUpdate(sql.toString(), batchValues);
    }
    
    
    
    protected static final String convertText(Object value) {
        if (null == value) {
            return null;
        }
        return (String) value;
    }

    protected T propsToDomain(Map<String, Object> props) {
        CompositeKey pk;
        CoreEntity core;
        T domain;
        pk = createCoreKey(props);
        core = new CoreEntity();
        core.setPrimaryKey(pk);
        
        resolveForeignKeys(props);
        
        core.setProperties(props);
        domain = coreToDomain(core);
        return domain;
    }

    protected void resolveForeignKeys(Map<String, Object> props) {
        Class columnClass;
        for (String columnName : getColumnNames()) {
            columnClass = getColumnClass(columnName);
            // is this a Entity reference?
            if (AbstractCreatedUpdatedEntity.class.isAssignableFrom(columnClass)){
                Object value = props.get(columnName);
                if (value instanceof Long || value instanceof String) {
                    DaoImpl foreignDao = getManyToOneDao(columnName);
                    Object foreignKey = foreignDao.createCoreKey(null, (Serializable) value);
                    props.put(columnName, foreignKey);
                }
            }
        }
    }

    public void setJdbcIncrementer(DataFieldMaxValueIncrementer jdbcIncrementer) {
        this.jdbcIncrementer = jdbcIncrementer;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

}
