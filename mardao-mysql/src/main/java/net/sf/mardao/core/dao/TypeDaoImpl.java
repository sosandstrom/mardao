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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import net.sf.mardao.core.CompositeKey;
import net.sf.mardao.core.CoreEntity;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.geo.DLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

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
    
    protected TypeDaoImpl(Class<T> type, Class<ID> idType) {
        super(type, idType);
    }
    
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(getTableName());
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
        // TODO: check if queryable
        
        createTable();
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
        if (Long.class.getName().equals(className)) {
            sql.append(' ');
            sql.append(getDataType("AUTO_INCREMENT"));
        }
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

    @Override
    public CoreEntity domainToCore(T domain, Date currentDate) {
        if (null == domain) {
            return null;
        }
        
        final CoreEntity core = createCore(getParentKey(domain), getSimpleKey(domain));
        
        // created, updated
        String principal = getCreatedBy(domain);
        if (null == principal) {
            principal = getPrincipalName();
            if (null == principal) {
                principal = PRINCIPAL_NAME_ANONYMOUS;
            }
            _setCreatedBy(domain, principal);
        }
        setCoreProperty(core, getCreatedByColumnName(), principal);
        
        Date date = getCreatedDate(domain);
        if (null == date) {
            date = currentDate;
            _setCreatedDate(domain, currentDate);
        }
        setCoreProperty(core, getCreatedDateColumnName(), date);
        
        principal = getPrincipalName();
        if (null == principal) {
            principal = PRINCIPAL_NAME_ANONYMOUS;
        }
        _setUpdatedBy(domain, principal);
        setCoreProperty(core, getUpdatedByColumnName(), principal);
        
        _setUpdatedDate(domain, currentDate);
        setCoreProperty(core, getUpdatedDateColumnName(), currentDate);
        
//        // Domain Entity-specific properties
//        for (String name : getColumnNames()) {
//            copyDomainPropertyToCore(name, domain, core);
//        }

        // geoboxes
        if (null != getGeoLocationColumnName()) {
            updateGeoModel(domain, core);
        }

        return core;
    }
    
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
    
    @Override
    public CoreEntity createCore(Object primaryKey) {
        final CoreEntity core = new CoreEntity();
        core.setPrimaryKey((CompositeKey) primaryKey);
        core.setProperties(new TreeMap<String, Object>());
        
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
        return new FilterEqual(fieldName, param);
    }
    
    @Override
    protected final Filter createInFilter(String fieldName, Collection param) {
        return new Filter(fieldName, "IN", param);
    }
    

    @Override
    protected String createMemCacheKey(Object parentKey, ID simpleKey) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final Key key = createCoreKey(parentKey, simpleKey);
//        return null != key ? KeyFactory.keyToString(key) : null;
    }
    
    @Override
    protected int doDelete(Object parentKey, Iterable<ID> simpleKeys) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final Collection<Key> coreKeys = createCoreKeys(parentKey, simpleKeys);
//        getDatastoreService().delete(coreKeys);
//        return -1;
    }

    @Override
    protected int doDelete(Iterable<T> domains) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final Iterable<Key> keys = (Iterable) domainsToPrimaryKeys(domains);
//        getDatastoreService().delete(keys);
//        return -1;
    }
    
    @Override
    protected T doFindByPrimaryKey(Object parentKey, ID simpleKey) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final Key coreKey = createCoreKey(parentKey, simpleKey);
//        try {
//            final Entity entity = getDatastoreService().get(coreKey);
//            final T domain = coreToDomain(entity);
//            return domain;
//        } catch (EntityNotFoundException expected) {
//            return null;
//        }
    }

    @Override
    protected Iterable<T> doQueryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys) {
        throw new UnsupportedOperationException("Not supported yet.");
//        // TODO: get batch with batch size
//        final Collection<Key> coreKeys = createCoreKeys(parentKey, simpleKeys);
//        final Map<Key, Entity> entities = getDatastoreService().get(coreKeys);
//        
//        final Collection<T> domains = new ArrayList<T>();
//        T domain;
//        for (Entity entity : entities.values()) {
//            domain = coreToDomain(entity);
//            domains.add(domain);
//        }
//        return domains;
    }
    
    @Override
    protected T findUniqueBy(Filter... filters) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final PreparedQuery pq = prepare(false, null, null,
//                null, false, null, false,
//                filters);
//        final Entity entity = pq.asSingleEntity();
//        final T domain = coreToDomain(entity);
//        return domain;
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
        throw new UnsupportedOperationException("Not supported yet.");
//        return null != primaryKey ? ((Key) primaryKey).getParent() : null;
    }
    
    @Override
    public Object getPrimaryKey(T domain) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (null == domain) {
//            return null;
//        }
//        if (Long.class.isAssignableFrom(simpleIdClass)) {
//            return KeyFactory.createKey((Key) getParentKey(domain), getTableName(), (Long) getSimpleKey(domain));
//        }
//        return KeyFactory.createKey((Key) getParentKey(domain), getTableName(), (String) getSimpleKey(domain));
    }

    @Override
    protected Collection<CompositeKey> persistCore(Iterable<CoreEntity> itrbl) {
        ArrayList<Map<String, Object>> propsList = new ArrayList<Map<String, Object>>();
        for (CoreEntity core : itrbl) {
            propsList.add(core.getProperties());
        }
        final Map[] empty = new Map[propsList.size()];
        final Map<String, Object>[] batch = propsList.toArray(empty);
        
        final int[] simpleKeys = jdbcInsert.executeBatch(batch);
        
        final Collection<CompositeKey> returnValue = new ArrayList<CompositeKey>(simpleKeys.length);
        CompositeKey pk;
        int i = 0;
        for (CoreEntity core : itrbl) {
            pk = core.getPrimaryKey();
            if (null == pk) {
                pk = new CompositeKey(null, null, null);
            }
            pk.setId((long)simpleKeys[i]);
            i++;
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
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ");
        sql.append(getTableName());
        final HashMap<String, Object> params = new HashMap<String, Object>();
        
        final List<Map<String, Object>> itemsProps = jdbcTemplate.queryForList(sql.toString(), params);
        
        final CursorPage<T, ID> cursorPage = new CursorPage<T, ID>();
        cursorPage.setRequestedPageSize(requestedPageSize);
        final Collection<T> domains = new ArrayList<T>();
        cursorPage.setItems(domains);
        
        CoreEntity core;
        CompositeKey pk;
        T domain;
        for (Map<String, Object> props : itemsProps) {
            pk = createCoreKey(props);
            core = new CoreEntity();
            core.setPrimaryKey(pk);
            core.setProperties(props);
            domain = coreToDomain(core);
            domains.add(domain);
        }
        
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
        throw new UnsupportedOperationException("Not supported yet.");
//        
//        final PreparedQuery pq = prepare(keysOnly, ancestorKey, simpleKey, 
//                              primaryOrderBy, primaryIsAscending, 
//                              secondaryOrderBy, secondaryIsAscending, filters);
//        
//        final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, 100, null);
//        final CursorIterable<T> returnValue = new CursorIterable<T>(_iterable);
//        
//        return returnValue;
    }

    @Override
    protected Iterable<ID> queryIterableKeys(
            int offset, int limit,
            Object ancestorKey, Object simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters) {
        throw new UnsupportedOperationException("Not supported yet.");
//        
//        final PreparedQuery pq = prepare(true, ancestorKey, simpleKey, 
//                              primaryOrderBy, primaryIsAscending, 
//                              secondaryOrderBy, secondaryIsAscending, filters);
//        
//        final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, 100, null);
//        final KeysIterable<ID> returnValue = new KeysIterable<ID>(_iterable);
//        
//        return returnValue;
    }

    @Override
    protected void setCoreProperty(Object core, String name, Object value) {
        if (null != name) {
            if (value instanceof DLocation) {
                final DLocation location = (DLocation) value;
                value = String.format("%f,%f", location.getLatitude(), location.getLongitude());
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

}
