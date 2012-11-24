package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.sql.DataSource;
import net.sf.mardao.core.CompositeKey;
import net.sf.mardao.core.CoreEntity;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;
import net.sf.mardao.core.geo.DLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public abstract class TypeDaoImpl<T, ID extends Serializable> extends
        DaoImpl<T, ID, Long, Iterable, CoreEntity, CompositeKey> {
    public static final String DIALECT_DEFAULT = "SQL";
    
    protected static final Properties DATA_TYPES_DEFAULT = new Properties();
    protected static final Map<String, Properties> DATA_DIALECTS = new HashMap<String, Properties>();
    
    static {
        DATA_DIALECTS.put(DIALECT_DEFAULT, DATA_TYPES_DEFAULT);
        
        DATA_TYPES_DEFAULT.setProperty(Long.class.getName(), "BIGINT");
        DATA_TYPES_DEFAULT.setProperty(Date.class.getName(), "TIMESTAMP");
        DATA_TYPES_DEFAULT.setProperty(String.class.getName(), "VARCHAR");
        DATA_TYPES_DEFAULT.setProperty(DLocation.class.getName(), "VARCHAR(33)");
        
        DATA_TYPES_DEFAULT.setProperty("AUTO_INCREMENT", "AUTO_INCREMENT");
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
//                .usingGeneratedKeyColumns(getPrimaryKeyColumnName());
        checkTable();
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
    
    protected void checkTable() {
        // check if queryable
        try {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT 1 FROM ");
        sql.append(getTableName());
        LOG.debug(sql.toString());
        jdbcTemplate.getJdbcOperations().execute(sql.toString());
        }
        catch (BadSqlGrammarException whenCreate) {
            createTable();
        }
    }
    
    protected void createTable() {
        final StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ");
        sql.append(getTableName());
        // column definitions
        sql.append(" (");

        appendPrimaryKeyColumnDefinition(sql);
        
        for (String columnName : getBasicColumnNames()) {
            sql.append(", ");
            appendColumnDefinition(sql, columnName);
        }
        
        // foreign keys?
        for (String columnName : getManyToOneColumnNames()) {
            sql.append(", ");
            appendColumnDefinition(sql, columnName);
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
        
        sql.append(");");
        
        LOG.info(sql.toString());
        jdbcTemplate.getJdbcOperations().execute(sql.toString());
    }
    
    protected void appendColumnDefinition(StringBuffer sql, String columnName) {
        sql.append(columnName);
        sql.append(' ');
        final String className = getColumnClass(columnName).getName();
        String dataType = getDataType(className);
        if (null == dataType) {
            dataType = getDataType(Long.class.getName());
        }
        sql.append(dataType);
    }
    
    protected void appendPrimaryKeyColumnDefinition(StringBuffer sql) {
        final String columnName = getPrimaryKeyColumnName();
        appendColumnDefinition(sql, columnName);

        final String className = getColumnClass(columnName).getName();
//        if (Long.class.getName().equals(className)) {
//            sql.append(' ');
//            sql.append(getDataType("AUTO_INCREMENT"));
//        }
        sql.append(" PRIMARY KEY");
    }
    
    protected String getDataType(String className) {
        final Properties dialectTypes = DATA_DIALECTS.get(dialect);
        String returnValue = dialectTypes.getProperty(className);
//        if (null == returnValue) {
//            returnValue = dialectTypes.getProperty(Long.class.getName());
//        }
        return returnValue;
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
    protected final Filter createEqualsFilter(String fieldName, Object param) {
        final Class columnClass = getColumnClass(fieldName);
        // is this a Entity reference?
        if (AbstractCreatedUpdatedEntity.class.isAssignableFrom(columnClass)){
            if (param instanceof CompositeKey) {
                param = getSimpleKeyByPrimaryKey(param);
            }
        }
        return new FilterEqual(fieldName, param);
    }
    
    @Override
    protected final Filter createInFilter(String fieldName, Collection param) {
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
            sql.append(f.getOperation());
            token = String.format("%s%d", f.getColumn(), i);
            sql.append(f.getToken(token));
            params.put(token, f.getOperand());
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
    protected Iterable<T> doQueryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys) {
        final ArrayList<ID> values = new ArrayList<ID>();
        for (ID id : simpleKeys) {
            values.add(id);
        }
        final Filter inFilter = createInFilter(getPrimaryKeyColumnName(), values);
        return queryIterable(false, 0, -1, null, parentKey, 
                null, false, null, false, inFilter);
    }
    
    @Override
    protected T findUniqueBy(Filter... filters) {
        StringBuffer sql = createSelect(false);
        Map<String, Object> params = appendWhereFilters(sql, filters);
        LOG.debug("{} with params {}", sql.toString(), params);
        try {
            final Map<String, Object> props = jdbcTemplate.queryForMap(sql.toString(), params);
            final T domain = propsToDomain(props);
            return domain;
        }
        catch (EmptyResultDataAccessException notFound) {
            return null;
        }
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
    protected Collection<CompositeKey> persistCore(Iterable<CoreEntity> itrbl) {
        final Collection<CompositeKey> returnValue = new ArrayList<CompositeKey>();
        final ArrayList<Map<String, Object>> propsList = new ArrayList<Map<String, Object>>();
        Long key;
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
                LOG.info("generating ID {}", simpleKey);
            }
            propsList.add(props);
            LOG.debug("persistCore {}", props);
            
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
            Serializable cursorString,
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
    
    // --- END persistence-type beans must implement these ---
    
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

}
