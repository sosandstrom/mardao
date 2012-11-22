package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.util.Collection;
import javax.sql.DataSource;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class TypeDaoImpl<T, ID extends Serializable> extends
        DaoImpl<T, ID, Long, Iterable, T, ID> {
    
    protected JdbcTemplate jdbcTemplate;
    
    protected TypeDaoImpl(Class<T> type, Class<ID> idType) {
        super(type, idType);
    }
    
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    // --- BEGIN persistence-type beans must implement these ---

    @Override
    protected ID coreToSimpleKey(T core) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return null != core ? coreKeyToSimpleKey(core.getKey()) : null;
    }

    
    @Override
    protected ID coreKeyToSimpleKey(ID key) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (null == key) {
//            return null;
//        }
//        
//        if (Long.class.isAssignableFrom(simpleIdClass)) {
//            return (ID) Long.valueOf(key.getId());
//        }
//        
//        return (ID) key.getName();
    }

    @Override
    protected Long coreToParentKey(T core) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return null != core ? core.getParent() : null;
    }

    @Override
    protected Long coreKeyToParentKey(ID core) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return null != core ? core.getParent() : null;
    }
    
    @Override
    public T createCore(Object primaryKey) {
        throw new UnsupportedOperationException("Not supported yet.");
//        Key pk = (Key) primaryKey;
//        Object parentKey = pk.getParent();
//        ID simpleKey = coreKeyToSimpleKey(pk);
//        return createCore(parentKey, simpleKey);
    }    
    
    @Override
    public T createCore(Object parentKey, ID simpleKey) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final Key pk = (Key) parentKey;
//        Entity entity;
//        if (null == simpleKey) {
//            entity = new Entity(getTableName(), pk);
//        }
//        else {
//            if (Long.class.isAssignableFrom(simpleIdClass)) {
//                entity = new Entity(getTableName(), ((Long)simpleKey).longValue(), pk);
//            }
//            else {
//                entity = new Entity(getTableName(), (String)simpleKey, pk);
//            }
//        }        
//        return entity;
    }

    protected ID createCoreKey(Object parentKey, ID simpleKey) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final Key pk = (Key) parentKey;
//        Key core;
//        if (Long.class.isAssignableFrom(simpleIdClass)) {
//            core = KeyFactory.createKey(pk, getTableName(), ((Long)simpleKey).longValue());
//        }
//        else {
//            core = KeyFactory.createKey(pk, getTableName(), (String)simpleKey);
//        }
//        return core;
    }
    
    protected Collection<ID> createCoreKeys(Object parentKey, Iterable<ID> simpleKeys) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final ArrayList<Key> coreKeys = new ArrayList<Key>();
//        Key core;
//        for (ID simpleKey : simpleKeys) {
//            core = createCoreKey(parentKey, simpleKey);
//            coreKeys.add(core);
//        }
//        return coreKeys;
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
    protected Object getCoreProperty(T core, String name, Class domainPropertyClass) {
        throw new UnsupportedOperationException("Not supported yet.");
//        Object value = null;
//        if (null != core && null != name) {
//            value = core.getProperty(name);
//            if (value instanceof GeoPt) {
//                final GeoPt geoPt = (GeoPt) value;
//                value = new DLocation(geoPt.getLatitude(), geoPt.getLongitude());
//            }
//        }
//        return value;
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
    protected Collection<ID> persistCore(Iterable<T> itrbl) {
        throw new UnsupportedOperationException("Not supported yet.");
//        final List<Key> returnValue = getDatastoreService().put(itrbl);
//        return returnValue;
    }
    
    @Override
    protected CursorPage<T, ID> queryPage(boolean keysOnly, int requestedPageSize,
            Object ancestorKey, Object simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Serializable cursorString,
            Filter... filters) {
        throw new UnsupportedOperationException("Not supported yet.");
//        
//        final PreparedQuery pq = prepare(keysOnly, ancestorKey, simpleKey, 
//                              primaryOrderBy, primaryIsAscending, 
//                              secondaryOrderBy, secondaryIsAscending, filters);
//        
//        final QueryResultList<Entity> iterable = asQueryResultList(pq, requestedPageSize, (String) cursorString);
//        
//        final CursorPage<T, ID> cursorPage = new CursorPage<T, ID>();
//        cursorPage.setRequestedPageSize(requestedPageSize);
//        
//        final Collection<T> domains = new ArrayList<T>();
//        for (Entity core : iterable) {
//            domains.add(coreToDomain(core));
//        }
//        cursorPage.setItems(domains);
//        
//        // only if next is available
//        if (domains.size() == requestedPageSize) {
//            cursorPage.setCursorKey(iterable.getCursor().toWebSafeString());
//        }
//        
//        return cursorPage;
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
        throw new UnsupportedOperationException("Not supported yet.");
//        if (null != name) {
//            if (value instanceof DLocation) {
//                final DLocation location = (DLocation) value;
//                value = new GeoPt(location.getLatitude(), location.getLongitude());
//            }
//            ((Entity) core).setProperty(name, value);
//        }
    }
    
    // --- END persistence-type beans must implement these ---
    
    protected static final String convertText(Object value) {
        if (null == value) {
            return null;
        }
        return (String) value;
    }

}
