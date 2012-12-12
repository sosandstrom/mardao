package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
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
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.geo.DLocation;

public abstract class TypeDaoImpl<T, ID extends Serializable> extends
        DaoImpl<T, ID, Key, QueryResultIterable, Entity, Key> implements Dao<T, ID> {
    
    protected TypeDaoImpl(Class<T> type, Class<ID> idType) {
        super(type, idType);
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
        
        if (Long.class.isAssignableFrom(simpleIdClass)) {
            return (ID) Long.valueOf(key.getId());
        }
        
        return (ID) key.getName();
    }

    @Override
    protected Key coreToParentKey(Entity core) {
        return null != core ? core.getParent() : null;
    }

    @Override
    protected Key coreKeyToParentKey(Key core) {
        return null != core ? core.getParent() : null;
    }

    @Override
    protected int count(Object ancestorKey, Object simpleKey, Filter... filters) {
       final PreparedQuery pq = prepare(true, (Key) ancestorKey, (Key) simpleKey, null, false, null, false, filters);
       return pq.countEntities(FetchOptions.Builder.withDefaults());
    }
    
    @Override
    public Entity createCore(Object primaryKey) {
        Key pk = (Key) primaryKey;
        Object parentKey = pk.getParent();
        ID simpleKey = coreKeyToSimpleKey(pk);
        return createCore(parentKey, simpleKey);
    }    
    
    @Override
    public Entity createCore(Object parentKey, ID simpleKey) {
        final Key pk = (Key) parentKey;
        Entity entity;
        if (null == simpleKey) {
            entity = new Entity(getTableName(), pk);
        }
        else {
            if (Long.class.isAssignableFrom(simpleIdClass)) {
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
        if (Long.class.isAssignableFrom(simpleIdClass)) {
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
    protected Filter createGreaterThanOrEqualFilter(String columnName, Object value) {
        return new Filter(columnName, FilterOperator.GREATER_THAN_OR_EQUAL, value);
    }
    
    @Override
    protected final Filter createInFilter(String fieldName, Collection param) {
        return new Filter(fieldName, FilterOperator.IN, param);
    }

    @Override
    protected String createMemCacheKey(Object parentKey, ID simpleKey) {
        final Key key = createCoreKey(parentKey, simpleKey);
        return null != key ? KeyFactory.keyToString(key) : null;
    }
    
    @Override
    protected int doDelete(Object parentKey, Iterable<ID> simpleKeys) {
        final Collection<Key> coreKeys = createCoreKeys(parentKey, simpleKeys);
        getDatastoreService().delete(coreKeys);
        return -1;
    }

    @Override
    protected int doDelete(Iterable<T> domains) {
        final Iterable<Key> keys = (Iterable) domainsToPrimaryKeys(domains);
        getDatastoreService().delete(keys);
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
    public int deleteAll() {
        final Iterable<ID> simpleKeys = queryAllKeys();
        return delete(null, simpleKeys);
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
    protected Object getCoreProperty(Entity core, String name, Class domainPropertyClass) {
        Object value = null;
        if (null != core && null != name) {
            value = core.getProperty(name);
            if (value instanceof GeoPt && DLocation.class.equals(domainPropertyClass)) {
                final GeoPt geoPt = (GeoPt) value;
                value = new DLocation(geoPt.getLatitude(), geoPt.getLongitude());
            }
            // Floats are persisted as 64-bit doubles in Datastore
            else if (value instanceof Double && Float.class.equals(domainPropertyClass)) {
                value = ((Double)value).floatValue();
            }
            else if (value instanceof Long) {
                if (Integer.class.equals(domainPropertyClass)) {
                    value = ((Long)value).intValue();
                }
                else if (Short.class.equals(domainPropertyClass)) {
                    value = ((Long)value).shortValue();
                }
                else if (Byte.class.equals(domainPropertyClass)) {
                    value = ((Long)value).byteValue();
                }
            }
            else if (value instanceof Text) {
                value = ((Text)value).getValue();
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
        return null != primaryKey ? ((Key) primaryKey).getParent() : null;
    }
    
    @Override
    public Object getPrimaryKey(T domain) {
        if (null == domain) {
            return null;
        }
        if (Long.class.isAssignableFrom(simpleIdClass)) {
            return KeyFactory.createKey((Key) getParentKey(domain), getTableName(), (Long) getSimpleKey(domain));
        }
        return KeyFactory.createKey((Key) getParentKey(domain), getTableName(), (String) getSimpleKey(domain));
    }

    @Override
    protected Collection<Key> persistCore(Iterable<Entity> itrbl) {
        final List<Key> returnValue = getDatastoreService().put(itrbl);
        return returnValue;
    }
    
    @Override
    protected CursorPage<T, ID> queryPage(boolean keysOnly, int requestedPageSize,
            Object ancestorKey, Object simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Serializable cursorString,
            Filter... filters) {
        
        final PreparedQuery pq = prepare(keysOnly, (Key)ancestorKey, (Key)simpleKey, 
                              primaryOrderBy, primaryIsAscending, 
                              secondaryOrderBy, secondaryIsAscending, filters);
        
        final QueryResultList<Entity> iterable = asQueryResultList(pq, requestedPageSize, (String) cursorString);
        
        final CursorPage<T, ID> cursorPage = new CursorPage<T, ID>();
        cursorPage.setRequestedPageSize(requestedPageSize);
        
        final Collection<T> domains = new ArrayList<T>();
        for (Entity core : iterable) {
            domains.add(coreToDomain(core));
        }
        cursorPage.setItems(domains);
        
        // only if next is available
        if (domains.size() == requestedPageSize) {
            cursorPage.setCursorKey(iterable.getCursor().toWebSafeString());
        }
        
        return cursorPage;
    }

    @Override
    protected QueryResultIterable<T> queryIterable(boolean keysOnly, 
            int offset, int limit,
            Object ancestorKey, Object simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters) {
        
        final PreparedQuery pq = prepare(keysOnly, (Key)ancestorKey, (Key)simpleKey, 
                              primaryOrderBy, primaryIsAscending, 
                              secondaryOrderBy, secondaryIsAscending, filters);
        
        final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, 100, null);
        final CursorIterable<T> returnValue = new CursorIterable<T>(_iterable);
        
        return returnValue;
    }

    @Override
    protected QueryResultIterable<ID> queryIterableKeys(
            int offset, int limit,
            Object ancestorKey, Object simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters) {
        
        final PreparedQuery pq = prepare(true, (Key)ancestorKey, (Key)simpleKey, 
                              primaryOrderBy, primaryIsAscending, 
                              secondaryOrderBy, secondaryIsAscending, filters);
        
        final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, 100, null);
        final KeysIterable<ID> returnValue = new KeysIterable<ID>(_iterable);
        
        return returnValue;
    }

    @Override
    protected void setCoreProperty(Object core, String name, Object value) {
        if (null != name) {
            if (value instanceof DLocation) {
                final DLocation location = (DLocation) value;
                value = new GeoPt(location.getLatitude(), location.getLongitude());
            }
            if (value instanceof String && 500 < value.toString().length()) {
                value = new Text(value.toString());
            }
            ((Entity) core).setProperty(name, value);
        }
    }
    
    // --- END persistence-type beans must implement these ---
    
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

    protected QueryResultIterable<Entity> asQueryResultIterable(PreparedQuery pq, int chunkSize, String cursorString) {
        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        fetchOptions.chunkSize(chunkSize);

        if (null != cursorString) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
        }
        
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
