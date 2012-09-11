package net.sf.mardao.api.dao;

import com.google.appengine.api.datastore.Cursor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.mardao.api.domain.DPrimaryKeyEntity;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Text;
import java.util.Collection;
import java.util.Collections;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import net.sf.mardao.api.CursorPage;
import net.sf.mardao.api.Filter;
import net.sf.mardao.api.domain.DLongEntity;

public abstract class AEDDaoImpl<T extends DPrimaryKeyEntity<ID>, ID extends Serializable> extends
        DaoImpl<T, ID, Key, QueryResultIterable, Entity, Key> implements Dao<T, ID> {
    
    /** Set this to true in subclass (TypeDaoBean) to enable the MemCache primaryKey - Entity */
    protected boolean memCacheEntity = false;

    /** Set this to true in subclass (TypeDaoBean) to enable the MemCache for findAll */
    protected boolean memCacheAll = false;
    
    protected static Cache _memCache = null;

    protected AEDDaoImpl(Class<T> type) {
        super(type);
    }
    
    // --- BEGIN persistence-type beans must implement these ---

    @Override
    protected ID coreToSimpleKey(Entity core) {
        return null != core ? coreKeyToSimpleKey(core.getKey()) : null;
    }

    @Override
    protected ID coreKeyToSimpleKey(Key key) {
        if (null == key) {
            return null;
        }
        
        if (DLongEntity.class.isAssignableFrom(persistentClass)) {
            return (ID) Long.valueOf(key.getId());
        }
        
        return (ID) key.getName();
    }

    @Override
    protected Key coreToParentKey(Entity core) {
        return null != core ? core.getParent() : null;
    }

    @Override
    public Entity createCore(Object parentKey, ID simpleKey) {
        final Key pk = (Key) parentKey;
        Entity entity;
        if (null == simpleKey) {
            entity = new Entity(getTableName(), pk);
        }
        else {
            if (DLongEntity.class.isAssignableFrom(persistentClass)) {
                entity = new Entity(getTableName(), ((Long)simpleKey).longValue(), pk);
            }
            else {
                entity = new Entity(getTableName(), (String)simpleKey, pk);
            }
        }        
        return entity;
    }

    protected Key createCoreKey(Object parentKey, ID simpleKey) {
        final Key pk = (Key) parentKey;
        Key core;
        if (DLongEntity.class.isAssignableFrom(persistentClass)) {
            core = KeyFactory.createKey(pk, getTableName(), ((Long)simpleKey).longValue());
        }
        else {
            core = KeyFactory.createKey(pk, getTableName(), (String)simpleKey);
        }
        return core;
    }
    
    protected Collection<Key> createCoreKeys(Object parentKey, Iterable<ID> simpleKeys) {
        final ArrayList<Key> coreKeys = new ArrayList<Key>();
        Key core;
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
    protected int doDelete(Object parentKey, Iterable<ID> simpleKeys) {
        final Collection<Key> coreKeys = createCoreKeys(parentKey, simpleKeys);
        getDatastoreService().delete(coreKeys);
        return -1;
    }

    @Override
    protected T doFindByPrimaryKey(Object parentKey, ID simpleKey) {
        final Key coreKey = createCoreKey(parentKey, simpleKey);
        try {
            final Entity entity = getDatastoreService().get(coreKey);
            final T domain = coreToDomain(entity);
            return domain;
        } catch (EntityNotFoundException expected) {
            return null;
        }
    }

    @Override
    protected Iterable<T> doQueryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys) {
        // TODO: get batch with batch size
        final Collection<Key> coreKeys = createCoreKeys(parentKey, simpleKeys);
        final Map<Key, Entity> entities = getDatastoreService().get(coreKeys);
        
        final Collection<T> domains = new ArrayList<T>();
        T domain;
        for (Entity entity : entities.values()) {
            domain = coreToDomain(entity);
            domains.add(domain);
        }
        return domains;
    }
    
    @Override
    protected T findUniqueBy(Filter... filters) {
        final PreparedQuery pq = prepare(false, null, null,
                null, false, null, false,
                filters);
        final Entity entity = pq.asSingleEntity();
        final T domain = coreToDomain(entity);
        return domain;
    }
    
    @Override
    protected Object getCoreProperty(Entity core, String name) {
        return null != core ? core.getProperty(name) : null;
    }
    
    @Override
    protected Collection<Key> persistCore(Iterable<Entity> itrbl) {
        final List<Key> returnValue = getDatastoreService().put(itrbl);
        return returnValue;
    }
    
    @Override
    protected CursorPage<T, ID> queryPage(boolean keysOnly, int pageSize,
            Key ancestorKey, Key simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Serializable cursorString,
            Filter... filters) {
        
        final PreparedQuery pq = prepare(keysOnly, ancestorKey, simpleKey, 
                              primaryOrderBy, primaryIsAscending, 
                              secondaryOrderBy, secondaryIsAscending, filters);
        
        final QueryResultList<Entity> iterable = asQueryResultList(pq, pageSize, (String) cursorString);
        
        final CursorPage<T, ID> cursorPage = new CursorPage<T, ID>();
        final Collection<T> domains = new ArrayList<T>();
        for (Entity core : iterable) {
            domains.add(coreToDomain(core));
        }
        cursorPage.setItems(domains);
        cursorPage.setCursorKey(iterable.getCursor().toWebSafeString());
        
        return cursorPage;
    }

    @Override
    protected QueryResultIterable<T> queryIterable(boolean keysOnly, 
            int offset, int limit,
            Key ancestorKey, Key simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters) {
        
        final PreparedQuery pq = prepare(keysOnly, ancestorKey, simpleKey, 
                              primaryOrderBy, primaryIsAscending, 
                              secondaryOrderBy, secondaryIsAscending, filters);
        
        final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, 100);
        final CursorIterable<T> returnValue = new CursorIterable<T>(_iterable);
        
        return returnValue;
    }

    @Override
    protected QueryResultIterable<ID> queryIterableKeys(
            int offset, int limit,
            Key ancestorKey, Key simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters) {
        
        final PreparedQuery pq = prepare(true, ancestorKey, simpleKey, 
                              primaryOrderBy, primaryIsAscending, 
                              secondaryOrderBy, secondaryIsAscending, filters);
        
        final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, 100);
        final KeysIterable<ID> returnValue = new KeysIterable<ID>(_iterable);
        
        return returnValue;
    }

    @Override
    protected void setCoreProperty(Entity core, String name, Object value) {
        if (null != name) {
            core.setProperty(name, value);
        }
    }
    
    // --- END persistence-type beans must implement these ---
    
    
    
    protected static Cache getMemCache() {
        if (null == _memCache) {
            LOG.debug("initializing memCache.");
            try {
                CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
                _memCache = cacheFactory.createCache(Collections.emptyMap());
            } catch (CacheException e) {
                LOG.error("Cannot initialize MemCache", e);
//                memCacheAll = false;
//                memCacheEntity = false;
            }            
        }
        return _memCache;
    }

    protected static final String convertText(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Text) {
            final Text text = (Text) value;
            return text.getValue();
        }
        return (String) value;
    }

    protected static DatastoreService getDatastoreService() {
        return DatastoreServiceFactory.getDatastoreService();
    }

    protected QueryResultIterable<Entity> asQueryResultIterable(PreparedQuery pq, int chunkSize) {
        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        fetchOptions.chunkSize(chunkSize);
        
        return pq.asQueryResultIterable(fetchOptions);
    }

    protected QueryResultList<Entity> asQueryResultList(PreparedQuery pq, int pageSize, String cursorString) {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
        
        if (null != cursorString) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
        }

        return pq.asQueryResultList(fetchOptions);
    }

    protected List<Key> asCoreKeys(PreparedQuery pq, int limit, int offset) {
        final List<Key> returnValue = new ArrayList<Key>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        if (0 < limit) {
            fetchOptions.limit(limit);
        }

        if (0 < offset) {
            fetchOptions.offset(offset);
        }

        for(Entity entity : pq.asIterable(fetchOptions)) {
            returnValue.add(entity.getKey());
        }

        return returnValue;
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(Map<String, Object> filters, String orderBy, boolean direction) {
        return prepare(filters, orderBy, direction, null, false);
    }

    /**
     * @param direction
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(Map<String, Object> filters, String orderBy, boolean direction, String secondaryOrderBy,
            boolean secondaryDirection) {
        LOG.debug("prepare {} filters {}", getTableName(), filters);
        final DatastoreService datastore = getDatastoreService();

        Query q = new Query(getTableName());

        // filter query:
        if (null != filters) {
            for(Entry<String, Object> filter : filters.entrySet()) {
                q.addFilter(filter.getKey(), FilterOperator.EQUAL, filter.getValue());
            }
        }

        // sort query?
        if (null != orderBy) {
            q.addSort(orderBy, direction ? SortDirection.ASCENDING : SortDirection.DESCENDING);

            // secondary sort order?
            if (null != secondaryOrderBy) {
                q.addSort(secondaryOrderBy, secondaryDirection ? SortDirection.ASCENDING : SortDirection.DESCENDING);
            }
        }

        return datastore.prepare(q);
    }

    /**
     * 
     * @param keysOnly
     * @param parentKey
     * @param orderBy
     * @param ascending
     * @param filters
     * @return
     */
    protected PreparedQuery prepare(boolean keysOnly, Key ancestorKey, Key simpleKey, String orderBy, boolean ascending,
            String secondaryOrderBy, boolean secondaryAscending, Filter... filters) {
        LOG.debug("prepare {} with filters {}", getTableName(), filters);
        final DatastoreService datastore = getDatastoreService();

        Query q = new Query(getTableName(), ancestorKey);

        // keys only?
        if (keysOnly) {
            q.setKeysOnly();
        }

        // filter on keyName:
        if (null != simpleKey) {
            q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, simpleKey);
        }

        // filter query:
        if (null != filters) {
            for(Filter f : filters) {
                q.addFilter(f.getColumn(), (FilterOperator) f.getOperation(), f.getOperand());
            }
        }

        // sort query?
        if (null != orderBy) {
            q.addSort(orderBy, ascending ? SortDirection.ASCENDING : SortDirection.DESCENDING);

            // secondary sort order?
            if (null != secondaryOrderBy) {
                q.addSort(secondaryOrderBy, secondaryAscending ? SortDirection.ASCENDING : SortDirection.DESCENDING);
            }
        }

        return datastore.prepare(q);
    }

    public class CursorIterable<T> implements QueryResultIterable<T> {
        final private QueryResultIterable<Entity> _iterable;

        public CursorIterable(QueryResultIterable<Entity> _iterable) {
            this._iterable = _iterable;
        }
        
        
        public QueryResultIterator<T> iterator() {
            return new CursorIterator<T>(_iterable.iterator());
        }
    }

    class CursorIterator<T> implements QueryResultIterator<T> {
        private final QueryResultIterator<Entity> _iterator;

        protected CursorIterator(QueryResultIterator<Entity> _iterator) {
            this._iterator = _iterator;
        }

        public boolean hasNext() {
            return _iterator.hasNext();
        }

        public T next() {
            return (T) coreToDomain(_iterator.next());
        }

        public void remove() {
            _iterator.remove();
        }

        public Cursor getCursor() {
            return _iterator.getCursor();
        }
    }
    
    public class KeysIterable<ID> implements QueryResultIterable<ID> {
        final private QueryResultIterable<Entity> _iterable;

        public KeysIterable(QueryResultIterable<Entity> _iterable) {
            this._iterable = _iterable;
        }
        
        
        public QueryResultIterator<ID> iterator() {
            return new KeysIterator<ID>(_iterable.iterator());
        }
    }

    class KeysIterator<ID> implements QueryResultIterator<ID> {
        private final QueryResultIterator<Entity> _iterator;

        protected KeysIterator(QueryResultIterator<Entity> _iterator) {
            this._iterator = _iterator;
        }

        public boolean hasNext() {
            return _iterator.hasNext();
        }

        public ID next() {
            return (ID) coreToSimpleKey(_iterator.next());
        }

        public void remove() {
            _iterator.remove();
        }

        public Cursor getCursor() {
            return _iterator.getCursor();
        }
    }
    
}
